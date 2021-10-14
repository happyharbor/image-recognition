package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ProcessBlobApp implements RequestHandler<S3Event, Void> {

    private final ImageRecognitionService imageRecognitionService;

    public ProcessBlobApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
    }

    @Override
    public Void handleRequest(final S3Event s3Event, final Context context) {
        imageRecognitionService.processBlob(s3Event);
        return null;
    }
}
