package io.happyharbor.image.recognition.service;

import io.happyharbor.image.recognition.dto.PostImageRequest;
import io.happyharbor.image.recognition.dto.PostImageResponse;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class ImageRecognitionService {

    @Inject
    public ImageRecognitionService() {
        // required by dagger
    }

    public PostImageResponse postImage(PostImageRequest postImageRequest) {
        log.info("Starting Post image of {}", postImageRequest);

        final PostImageResponse response = PostImageResponse.builder().uploadUrl("upload_url").build();
        log.info("Post image finished with {}", response);
        return response;
    }
}
