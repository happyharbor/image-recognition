package io.happyharbor.image.recognition.aws;

import io.happyharbor.image.recognition.configuration.AwsModule;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Singleton
public class S3Helper {

    public static final Map<String, String> CONTENT_TYPES_SUFFIX_MAP =Map.of("image/jpeg", "jpg", "image/png", "png");

    private final String bucketName;
    private final S3Presigner presigner;

    @Inject
    public S3Helper(@Named(AwsModule.BUCKET_NAME) final String bucketName, final S3Presigner presigner) {
        this.bucketName = bucketName;
        this.presigner = presigner;
    }

    public URL generatePresignedUrl(final UUID blobId, final String contentType) {
        val objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(String.format("%s.%s", blobId.toString(), CONTENT_TYPES_SUFFIX_MAP.get(contentType)))
                .contentType(contentType)
                .build();

        val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        val presignedRequest = presigner.presignPutObject(presignRequest);

        val url = presignedRequest.url();
        log.debug("The presigned url is {}", url);
        return url;
    }
}
