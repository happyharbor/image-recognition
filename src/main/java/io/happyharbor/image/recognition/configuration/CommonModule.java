package io.happyharbor.image.recognition.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public interface CommonModule {

    @Provides @Singleton static ObjectMapper provideObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return objectMapper;
    }
}
