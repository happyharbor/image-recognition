package io.happyharbor.image.recognition.dto;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class ImageRecognitionResults {
    public static final String SUCCESS_MSG = "success";
    String status;
    List<ImageRecognitionResult> imageResults;

    public static ImageRecognitionResults success(final List<ImageRecognitionResult> imageRecognitionResults) {
        return new ImageRecognitionResults(SUCCESS_MSG, imageRecognitionResults);
    }

    public static ImageRecognitionResults failed(final String error) {
        return new ImageRecognitionResults(error, Collections.emptyList());
    }
}
