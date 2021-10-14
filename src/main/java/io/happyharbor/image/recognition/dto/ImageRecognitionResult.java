package io.happyharbor.image.recognition.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@DynamoDbImmutable(builder = ImageRecognitionResult.ImageRecognitionResultBuilder.class)
public class ImageRecognitionResult {
    String name;
    Float confidence;
}
