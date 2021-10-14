package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.dto.CreateBlobRequest;
import io.happyharbor.image.recognition.dto.CreateBlobResponse;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;
import java.util.Set;

import static io.happyharbor.image.recognition.aws.S3Helper.CONTENT_TYPES_SUFFIX_MAP;

@Slf4j
public class CreateBlobApp implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final ObjectMapper mapper;
    private final ImageRecognitionService imageRecognitionService;
    private static final Set<String> ALLOWED_CONTENT_TYPES = CONTENT_TYPES_SUFFIX_MAP.keySet();

    public CreateBlobApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
        this.mapper = imageRecognition.provideObjectMapper();
    }

    @SneakyThrows
    @Override
    public APIGatewayV2HTTPResponse handleRequest(final APIGatewayV2HTTPEvent input, final Context context) {
        try {
            val request = mapper.readValue(input.getBody(), CreateBlobRequest.class);

            if (request.getCallbackUrl().getScheme() == null || request.getCallbackUrl().getScheme().length() == 0) {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(406)
                        .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                        .withBody(mapper.writeValueAsString(CreateBlobResponse.failed("malformed callback_url")))
                        .build();
            }

            if (request.getContentType() == null || request.getContentType().length() == 0) {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(406)
                        .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                        .withBody(mapper.writeValueAsString(CreateBlobResponse.failed("content_type is mandatory")))
                        .build();
            }

            if (!ALLOWED_CONTENT_TYPES.contains(request.getContentType())) {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(406)
                        .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                        .withBody(mapper.writeValueAsString(CreateBlobResponse.failed("Content_type needs to be among the following: " +
                                String.join(", ", ALLOWED_CONTENT_TYPES))))
                        .build();
            }
            val response = imageRecognitionService.createBlob(request);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                    .withBody(mapper.writeValueAsString(response))
                    .build();
        } catch (Exception e) {
            log.error("There was an error during creating the blob {}", input, e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(400)
                    .withHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON))
                    .withBody(mapper.writeValueAsString(CreateBlobResponse.failed("Apologies there was an error")))
                    .build();
        }
    }
}
