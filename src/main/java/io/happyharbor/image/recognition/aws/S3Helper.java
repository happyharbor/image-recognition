package io.happyharbor.image.recognition.aws;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Singleton
public class S3Helper {

    private final String bucketName;
    private final S3Presigner presigner;

    @Inject
    public S3Helper() {
        bucketName = System.getenv("BUCKET_NAME");
        presigner = S3Presigner.builder()
                .region(Region.of(System.getenv("REGION")))
                .build();
    }

    @SneakyThrows
    public URL generatePresignedUrl(final UUID blobId, final String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(blobId.toString())
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        val url = presignedRequest.url();
        log.debug("The presigned url is {}", url);
        return url;
    }
}
