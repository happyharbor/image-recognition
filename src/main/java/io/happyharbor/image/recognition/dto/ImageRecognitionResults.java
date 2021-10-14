package io.happyharbor.image.recognition.dto;

import lombok.Value;

import java.util.Collections;
import java.util.List;

import static io.happyharbor.image.recognition.dto.BlobInfo.BLOB_STATUS_SUCCESS;

@Value
public class ImageRecognitionResults {
    String status;
    List<ImageRecognitionResult> imageResults;

    public static ImageRecognitionResults success(final List<ImageRecognitionResult> imageRecognitionResults) {
        return new ImageRecognitionResults(BLOB_STATUS_SUCCESS, imageRecognitionResults);
    }

    public static ImageRecognitionResults failed(final String error) {
        return new ImageRecognitionResults(error, Collections.emptyList());
    }
}
