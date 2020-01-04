package com.semiz.boundary;
import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import io.quarkus.jsonb.JsonbConfigCustomizer;
@Singleton
public class FooSerializerRegistrationCustomizer implements JsonbConfigCustomizer {
    public void customize(JsonbConfig config) {
        //config.withSerializers(new FooSerializer());
    }
}