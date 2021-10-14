package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.dto.BlobInfoResponse;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;

@Slf4j
public class GetBlobInfoApp implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final ObjectMapper mapper;
    private final ImageRecognitionService imageRecognitionService;

    public GetBlobInfoApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
        this.mapper = imageRecognition.provideObjectMapper();
    }

    @SneakyThrows
    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent input, final Context context) {

        log.debug(input.getPathParameters().toString());
        if (!input.getPathParameters().containsKey("blob_id")) {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(406)
                    .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                    .withBody(mapper.writeValueAsString(BlobInfoResponse.failed("blob_id is mandatory")))
                    .build();
        }

        val blobInfo = imageRecognitionService.getBlobInfo(input.getPathParameters().get("blob_id"));

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                .withBody(mapper.writeValueAsString(blobInfo))
                .build();
    }
}
