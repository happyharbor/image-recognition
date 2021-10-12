package io.happyharbor.image.recognition.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.happyharbor.image.recognition.configuration.DaggerImageRecognition;
import io.happyharbor.image.recognition.dto.PostImageRequest;
import io.happyharbor.image.recognition.service.ImageRecognitionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PostImageApp implements RequestStreamHandler {

    private final Gson gson;
    private final ImageRecognitionService imageRecognitionService;

    public PostImageApp() {
        val imageRecognition = DaggerImageRecognition.create();
        this.imageRecognitionService = imageRecognition.imageRecognitionService();
        this.gson = imageRecognition.provideGson();
    }

    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.US_ASCII));
             PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.US_ASCII)))) {

            val request = gson.fromJson(reader, PostImageRequest.class);
            val response = imageRecognitionService.postImage(request);
            writer.write(gson.toJson(response));
            if (writer.checkError()) {
                log.warn("WARNING: Writer encountered an error.");
            }
        } catch (IllegalStateException | JsonSyntaxException exception) {
            log.error(exception.toString());
        }
    }
}
