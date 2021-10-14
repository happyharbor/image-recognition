package io.happyharbor.image.recognition.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import io.happyharbor.image.recognition.service.ImageRecognitionService;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class, AwsModule.class})
public interface ImageRecognition {
    ImageRecognitionService imageRecognitionService();
    ObjectMapper provideObjectMapper();
}
