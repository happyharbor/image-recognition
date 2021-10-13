package io.happyharbor.image.recognition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlobRequest {
    private URI callbackUrl;
}
