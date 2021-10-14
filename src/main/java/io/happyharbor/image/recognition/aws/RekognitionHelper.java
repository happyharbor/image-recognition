package io.happyharbor.image.recognition.aws;

import io.happyharbor.image.recognition.configuration.AwsModule;
import io.happyharbor.image.recognition.dto.ImageRecognitionResult;
import io.happyharbor.image.recognition.dto.ImageRecognitionResults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class RekognitionHelper {

    private static final int CONFIDENCE_THRESHOLD = 80;

    private final RekognitionClient rekognitionClient;
    private final String bucketName;

    @Inject
    public RekognitionHelper(final RekognitionClient rekognitionClient, @Named(AwsModule.BUCKET_NAME) final String bucketName) {

        this.rekognitionClient = rekognitionClient;
        this.bucketName = bucketName;
    }

    public ImageRecognitionResults recognizeImage(final String key) {
        try {
            val imageRecognitionResults = rekognitionClient.detectLabels(DetectLabelsRequest.builder()
                            .image(Image.builder()
                                    .s3Object(S3Object.builder()
                                            .bucket(bucketName)
                                            .name(key)
                                            .build())
                                    .build())
                            .build())
                    .labels()
                    .stream()
                    .filter(label -> label.confidence() >= CONFIDENCE_THRESHOLD)
                    .map(label -> new ImageRecognitionResult(label.name(), label.confidence()))
                    .collect(Collectors.toList());

            return ImageRecognitionResults.success(imageRecognitionResults);
        } catch (AwsServiceException | SdkClientException e) {
            log.error(e.getMessage(), e);
            return ImageRecognitionResults.failed(e.getMessage());
        }
    }
}
