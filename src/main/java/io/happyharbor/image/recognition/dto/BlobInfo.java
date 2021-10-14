package io.happyharbor.image.recognition.dto;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@DynamoDbImmutable(builder = BlobInfo.BlobInfoBuilder.class)
public class BlobInfo {
    UUID blobId;
    URI callbackUrl;
    String status;
    List<ImageRecognitionResult> imageRecognitionResults;

    @DynamoDbPartitionKey
    public UUID getBlobId() {
        return blobId;
    }
}
