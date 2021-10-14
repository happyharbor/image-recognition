package io.happyharbor.image.recognition.aws;

import io.happyharbor.image.recognition.dto.BlobInfo;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class DynamoDbHelper {
    private final DynamoDbTable<BlobInfo> table;

    @Inject
    public DynamoDbHelper(final DynamoDbTable<BlobInfo> table) {
        this.table = table;
    }

    public void save(final BlobInfo blobInfo) {
        table.putItem(blobInfo);
    }

    public BlobInfo get(final String blobId) {
        return table.getItem(Key.builder()
                .partitionValue(blobId)
                .build());
    }
}
