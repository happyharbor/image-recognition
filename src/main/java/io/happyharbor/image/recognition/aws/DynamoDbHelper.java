package io.happyharbor.image.recognition.aws;

import io.happyharbor.image.recognition.dto.BlobInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class DynamoDbHelper {
    private final DynamoDbTable<BlobInfo> table;

    @Inject
    public DynamoDbHelper() {
        val dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .region(Region.of(System.getenv("REGION")))
                        .build())
                .build();
        table = dynamoDbEnhancedClient.table(System.getenv("DYNAMO_DB_TABLE_NAME"),
                TableSchema.fromImmutableClass(BlobInfo.class));
    }

    public void save(final BlobInfo blobInfo) {
        table.putItem(blobInfo);
    }
}
