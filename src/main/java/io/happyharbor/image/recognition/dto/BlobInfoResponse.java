package io.happyharbor.image.recognition.dto;

import lombok.Value;

import java.util.List;

@Value
public class BlobInfoResponse {
    String status;
    String errorMessage;
    List<ImageRecognitionResult> imageRecognitionResults;

    public static BlobInfoResponse failed(String errorMessage) {
        return new BlobInfoResponse("failed", errorMessage, null);
    }
}
