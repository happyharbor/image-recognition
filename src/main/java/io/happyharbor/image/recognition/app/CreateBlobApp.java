package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.dto.CreateBlobRequest;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class CreateBlobApp implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final Gson gson;
    private final ImageRecognitionService imageRecognitionService;

    public CreateBlobApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
        this.gson = imageRecognition.provideGson();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent input, final Context context) {
        val request = gson.fromJson(input.getBody(), CreateBlobRequest.class);
        try {
            val response = imageRecognitionService.createBlob(request);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(gson.toJson(response))
                    .build();
        } catch (Exception e) {
            log.error("There was an error during post image {}", request, e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(400)
                    .withBody("Apologies there was an error")
                    .build();
        }
    }
}
