package io.happyharbor.image.recognition.service;

import io.happyharbor.image.recognition.aws.DynamoDbHelper;
import io.happyharbor.image.recognition.aws.S3Helper;
import io.happyharbor.image.recognition.dto.BlobInfo;
import io.happyharbor.image.recognition.dto.CreateBlobRequest;
import io.happyharbor.image.recognition.dto.CreateBlobResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Slf4j
@Singleton
public class ImageRecognitionService {

    private final DynamoDbHelper dynamoDbHelper;
    private final S3Helper s3Helper;

    @Inject
    public ImageRecognitionService(final DynamoDbHelper dynamoDbHelper, final S3Helper s3Helper) {
        this.dynamoDbHelper = dynamoDbHelper;
        this.s3Helper = s3Helper;
    }

    public CreateBlobResponse createBlob(CreateBlobRequest createBlobRequest) {
        log.info("Starting Post image of {}", createBlobRequest);

        val blobId = UUID.randomUUID();
        dynamoDbHelper.save(BlobInfo.builder()
                .blobId(blobId)
                .callbackUrl(createBlobRequest.getCallbackUrl())
                .build());

        val presignedUrl = s3Helper.generatePresignedUrl(blobId, createBlobRequest.getContentType());

        final CreateBlobResponse response = CreateBlobResponse.builder()
                .uploadUrl(presignedUrl)
                .blobId(blobId)
                .build();
        log.info("Post image finished with {}", response);
        return response;
    }
}
