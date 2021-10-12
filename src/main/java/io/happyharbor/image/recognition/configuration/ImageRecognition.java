package io.happyharbor.image.recognition.configuration;

import com.google.gson.Gson;
import dagger.Component;
import io.happyharbor.image.recognition.service.ImageRecognitionService;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class})
public interface ImageRecognition {
    ImageRecognitionService imageRecognitionService();
    Gson provideGson();
}
