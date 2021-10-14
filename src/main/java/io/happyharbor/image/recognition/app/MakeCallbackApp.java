package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class MakeCallbackApp implements RequestHandler<DynamodbEvent, Void> {

    private final ImageRecognitionService imageRecognitionService;

    public MakeCallbackApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
    }

    @Override
    public Void handleRequest(final DynamodbEvent dynamodbEvent, final Context context) {
        try {
            imageRecognitionService.makeCallback(dynamodbEvent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
