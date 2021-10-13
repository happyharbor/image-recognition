package io.happyharbor.image.recognition.dto;

import lombok.Builder;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

@Value
@Builder
public class CreateBlobResponse {
    URL uploadUrl;
    UUID blobId;
}
