package io.happyharbor.image.recognition.service;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.happyharbor.image.recognition.aws.DynamoDbHelper;
import io.happyharbor.image.recognition.aws.RekognitionHelper;
import io.happyharbor.image.recognition.aws.S3Helper;
import io.happyharbor.image.recognition.dto.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.happyharbor.image.recognition.dto.BlobInfo.*;

@Slf4j
@Singleton
public class ImageRecognitionService {

    private final DynamoDbHelper dynamoDbHelper;
    private final S3Helper s3Helper;
    private final RekognitionHelper rekognitionHelper;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Inject
    public ImageRecognitionService(final DynamoDbHelper dynamoDbHelper, final S3Helper s3Helper,
                                   final RekognitionHelper rekognitionHelper, final ObjectMapper objectMapper,
                                   final HttpClient httpClient) {
        this.dynamoDbHelper = dynamoDbHelper;
        this.s3Helper = s3Helper;
        this.rekognitionHelper = rekognitionHelper;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public CreateBlobResponse createBlob(CreateBlobRequest createBlobRequest) {
        log.debug("Starting creating blob of {}", createBlobRequest);

        val blobId = UUID.randomUUID();

        val saveFuture = dynamoDbHelper.save(builder()
                .blobId(blobId)
                .callbackUrl(createBlobRequest.getCallbackUrl())
                .status(BLOB_STATUS_UPLOAD)
                .build());

        val presignedUrl = s3Helper.generatePresignedUrl(blobId, createBlobRequest.getContentType());

        saveFuture.join();

        final CreateBlobResponse response = CreateBlobResponse.success(presignedUrl, blobId);
        log.info("Create blob finished with {}", response);
        return response;
    }

    public void processBlob(final S3Event s3Event) {
        s3Event.getRecords().forEach(r -> processBlob(r.getS3().getObject().getKey()));
    }

    public void makeCallback(final DynamodbEvent dynamodbEvent) {
        log.debug("Start make callback based on {}", dynamodbEvent);

        dynamodbEvent.getRecords().forEach(this::processRecord);

        log.info("Finish callback of {}", dynamodbEvent);
    }

    public BlobInfoResponse getBlobInfo(final String blobId) {
        val blobInfo = dynamoDbHelper.get(blobId).join();
        return blobInfo == null ?
                BlobInfoResponse.failed(String.format("No record with blob_id: %s was found", blobId)) :
                new BlobInfoResponse(blobInfo.getStatus(), null, blobInfo.getImageRecognitionResults());
    }

    private void processRecord(final DynamodbEvent.DynamodbStreamRecord dynamoDbRecord) {
        log.debug("Record to process: {}", dynamoDbRecord);

        if (!dynamoDbRecord.getEventName().equals("MODIFY")) {
            return;
        }
        val callbackUrl = dynamoDbRecord.getDynamodb().getNewImage().get("callbackUrl").getS();

        val newStatus = dynamoDbRecord.getDynamodb().getNewImage().get("status").getS();
        val oldStatus = dynamoDbRecord.getDynamodb().getOldImage().get("status").getS();

        if (newStatus.equals(oldStatus)) {
            return;
        }

        switch (newStatus) {
            case BLOB_STATUS_FINISHED:
                val imageRecognitionResults = dynamoDbRecord.getDynamodb()
                        .getNewImage()
                        .get("imageRecognitionResults")
                        .getL()
                        .stream()
                        .map(AttributeValue::getM)
                        .map(r -> new ImageRecognitionResult(r.get("name").getS(), Float.parseFloat(r.get("confidence").getN())))
                        .collect(Collectors.toList());
                log.debug("The image recognition results from dynamo db stream: {}", imageRecognitionResults);
                sendCallBack(new BlobInfoResponse(newStatus, null, imageRecognitionResults), callbackUrl);
                break;
            case BLOB_STATUS_UPLOAD:
            case BLOB_STATUS_IN_PROGRESS:
                break;
            default:
                sendCallBack(BlobInfoResponse.failed(newStatus), callbackUrl);
        }
    }

    @SneakyThrows
    private void sendCallBack(final BlobInfoResponse callBackDto, final String callbackUrl) {
        val httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(callBackDto)))
                .uri(URI.create(callbackUrl))
                .header("Content-Type", "application/json")
                .build();

        val send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Status code: {}, body: {}", send.statusCode(), send.body());
    }

    private void processBlob(final String key) {
        log.debug("Start processing {}", key);

        final int endIndex = key.lastIndexOf('.') == -1 ? key.length() : key.lastIndexOf('.');
        var blobId = key.substring(key.lastIndexOf('/') + 1, endIndex);

        val blobInfo = dynamoDbHelper.get(blobId).join();

        val blobInfoInProgress = blobInfo.toBuilder()
                .status(BLOB_STATUS_IN_PROGRESS)
                .build();
        dynamoDbHelper.save(blobInfoInProgress);

        val imageRecognitionResults = rekognitionHelper.recognizeImage(key);

        val blobInfoFinished = blobInfo.toBuilder()
                .status(imageRecognitionResults.getStatus())
                .imageRecognitionResults(imageRecognitionResults.getStatus().equals(BLOB_STATUS_FINISHED) ?
                        imageRecognitionResults.getImageResults() :
                        Collections.emptyList())
                .build();
        dynamoDbHelper.save(blobInfoFinished);
        log.info("Finished processing of {}", blobInfo);
    }
}
