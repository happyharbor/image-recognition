package io.happyharbor.image.recognition.service;

import io.happyharbor.image.recognition.aws.DynamoDbHelper;
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

    @Inject
    public ImageRecognitionService(final DynamoDbHelper dynamoDbHelper) {
        this.dynamoDbHelper = dynamoDbHelper;
    }

    public CreateBlobResponse createBlob(CreateBlobRequest createBlobRequest) {
        log.info("Starting Post image of {}", createBlobRequest);

        val blobId = UUID.randomUUID();
        dynamoDbHelper.save(BlobInfo.builder()
                .blobId(blobId)
                .callbackUrl(createBlobRequest.getCallbackUrl())
                .build());
        final CreateBlobResponse response = CreateBlobResponse.builder()
                .uploadUrl("upload_url")
                .blobId(blobId)
                .build();
        log.info("Post image finished with {}", response);
        return response;
    }
}
