package io.happyharbor.image.recognition.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.net.URI;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@DynamoDbImmutable(builder = BlobInfo.BlobInfoBuilder.class)
public class BlobInfo {
    UUID blobId;
    URI callbackUrl;
    String status;

    @DynamoDbPartitionKey
    public UUID getBlobId() {
        return blobId;
    }
}
