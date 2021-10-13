package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.dto.CreateBlobRequest;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class CreateBlobApp implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final ObjectMapper mapper;
    private final ImageRecognitionService imageRecognitionService;

    public CreateBlobApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
        this.mapper = imageRecognition.provideObjectMapper();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent input, final Context context) {
        try {
            val request = mapper.readValue(input.getBody(), CreateBlobRequest.class);

            if (request.getContentType() == null || request.getContentType().length() == 0) {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(406)
                        .withBody("content_type is mandatory")
                        .build();
            }
            val response = imageRecognitionService.createBlob(request);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(mapper.writeValueAsString(response))
                    .build();
        } catch (Exception e) {
            log.error("There was an error during post image {}", input, e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(400)
                    .withBody("Apologies there was an error")
                    .build();
        }
    }
}
