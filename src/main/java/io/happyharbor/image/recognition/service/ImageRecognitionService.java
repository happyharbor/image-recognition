package io.happyharbor.image.recognition.service;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import io.happyharbor.image.recognition.aws.DynamoDbHelper;
import io.happyharbor.image.recognition.aws.RekognitionHelper;
import io.happyharbor.image.recognition.aws.S3Helper;
import io.happyharbor.image.recognition.dto.BlobInfo;
import io.happyharbor.image.recognition.dto.CreateBlobRequest;
import io.happyharbor.image.recognition.dto.CreateBlobResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.UUID;

import static io.happyharbor.image.recognition.dto.ImageRecognitionResults.SUCCESS_MSG;

@Slf4j
@Singleton
public class ImageRecognitionService {

    private final DynamoDbHelper dynamoDbHelper;
    private final S3Helper s3Helper;
    private final RekognitionHelper rekognitionHelper;

    @Inject
    public ImageRecognitionService(final DynamoDbHelper dynamoDbHelper, final S3Helper s3Helper, final RekognitionHelper rekognitionHelper) {
        this.dynamoDbHelper = dynamoDbHelper;
        this.s3Helper = s3Helper;
        this.rekognitionHelper = rekognitionHelper;
    }

    public CreateBlobResponse createBlob(CreateBlobRequest createBlobRequest) {
        log.debug("Starting Post image of {}", createBlobRequest);

        val blobId = UUID.randomUUID();
        dynamoDbHelper.save(BlobInfo.builder()
                                    .blobId(blobId)
                                    .callbackUrl(createBlobRequest.getCallbackUrl())
                                    .status("waiting for image upload")
                                    .build());

        val presignedUrl = s3Helper.generatePresignedUrl(blobId, createBlobRequest.getContentType());

        final CreateBlobResponse response = CreateBlobResponse.success(presignedUrl, blobId);
        log.info("Post image finished with {}", response);
        return response;
    }

    public void processBlob(final S3Event s3Event) {
        final String key = s3Event.getRecords().get(0).getS3().getObject().getKey();

        log.debug("Start processing {}", key);

        final int endIndex = key.lastIndexOf('.') == -1 ? key.length() : key.lastIndexOf('.');
        var blobId = key.substring(key.lastIndexOf('/') + 1, endIndex);

        val blobInfo = dynamoDbHelper.get(blobId);

        val blobInfoInProgress = blobInfo.toBuilder()
                .status("in progress")
                .build();
        dynamoDbHelper.save(blobInfoInProgress);

        val imageRecognitionResults = rekognitionHelper.recognizeImage(key);

        val blobInfoFinished = blobInfo.toBuilder()
                .status(imageRecognitionResults.getStatus())
                .imageRecognitionResults(imageRecognitionResults.getStatus().equals(SUCCESS_MSG) ?
                        imageRecognitionResults.getImageResults() :
                        Collections.emptyList())
                .build();
        dynamoDbHelper.save(blobInfoFinished);
        log.info("Finished processing of {}", blobInfo);
    }
}
