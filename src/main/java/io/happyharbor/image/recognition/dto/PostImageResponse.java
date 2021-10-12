package io.happyharbor.image.recognition.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostImageResponse {
    String uploadUrl;
}
