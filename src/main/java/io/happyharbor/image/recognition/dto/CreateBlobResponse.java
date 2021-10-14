package io.happyharbor.image.recognition.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class CreateBlobResponse {
    URL uploadUrl;
    UUID blobId;
    boolean error;
    String errorMessage;

    public static CreateBlobResponse success(final URL uploadUrl, final UUID blobId) {
        return CreateBlobResponse.builder()
                .uploadUrl(uploadUrl)
                .blobId(blobId)
                .error(false)
                .build();
    }

    public static CreateBlobResponse failed(String errorMessage) {
        return CreateBlobResponse.builder()
                .error(true)
                .errorMessage(errorMessage)
                .build();
    }
}
