package io.happyharbor.image.recognition.configuration;

import dagger.Module;
import dagger.Provides;
import io.happyharbor.image.recognition.dto.BlobInfo;
import lombok.val;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface AwsModule {

    String REGION = "REGION";
    String BUCKET_NAME = "bucket-name";

    @Provides @Singleton static S3Presigner provideS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(System.getenv(REGION)))
                .build();
    }

    @Provides @Named(BUCKET_NAME) static String provideBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    @Provides @Singleton static RekognitionClient provideRekognitionClient() {
        return RekognitionClient.builder()
                .region(Region.of(System.getenv(REGION)))
                .build();
    }

    @Provides @Singleton static DynamoDbTable<BlobInfo> providedDynamoDbTable() {
        val dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .region(Region.of(System.getenv(REGION)))
                        .build())
                .build();
        return dynamoDbEnhancedClient.table(System.getenv("DYNAMO_DB_TABLE_NAME"),
                TableSchema.fromImmutableClass(BlobInfo.class));
    }
}
