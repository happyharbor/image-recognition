package io.happyharbor.image.recognition.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateBlobResponse {
    String uploadUrl;
    UUID blobId;
}
