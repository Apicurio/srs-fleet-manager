package io.bf2fc6cc711aee1a0c2a.rest.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

//@Singleton
public class DefaultObjectMapperCustomizer /*implements ObjectMapperCustomizer*/ {

    //@Override
    public void customize(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPolymorphicTypeValidator(LaissezFaireSubTypeValidator.instance);
    }
}
