package io.happyharbor.image.recognition.configuration;

import dagger.Module;
import dagger.Provides;
import io.happyharbor.image.recognition.dto.BlobInfo;
import lombok.val;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
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

    @Provides @Singleton static DynamoDbAsyncTable<BlobInfo> providedDynamoDbTable() {
        val dynamoClient = DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(DynamoDbAsyncClient.builder()
                        .region(Region.of(System.getenv("REGION")))
                        .build())
                .build();
        return dynamoClient.table(System.getenv("DYNAMO_DB_TABLE_NAME"),
                TableSchema.fromImmutableClass(BlobInfo.class));
    }
}
