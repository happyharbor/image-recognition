package io.happyharbor.image.recognition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.net.http.HttpClient;

@Module
public interface CommonModule {

    @Provides @Singleton static ObjectMapper provideObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Provides @Singleton static HttpClient provideHttpClient() {
        return HttpClient.newHttpClient();
    }
}
