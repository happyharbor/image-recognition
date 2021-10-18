package io.happyharbor.image.recognition.aws;

import io.happyharbor.image.recognition.dto.BlobInfo;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Singleton
public class DynamoDbHelper {
    private final DynamoDbAsyncTable<BlobInfo> table;

    @Inject
    public DynamoDbHelper(final DynamoDbAsyncTable<BlobInfo> table) {
        this.table = table;
    }

    public CompletableFuture<Void> save(final BlobInfo blobInfo) {
        return table.putItem(blobInfo);
    }

    public CompletableFuture<BlobInfo> get(final String blobId) {
        return table.getItem(Key.builder()
                .partitionValue(blobId)
                .build());
    }
}
