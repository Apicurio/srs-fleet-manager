package org.bf2.srs.fleetmanager.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class SerDesObjectMapperProducer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);

        // TODO Merge somehow?
        YAML_MAPPER.registerModule(new JavaTimeModule());
        YAML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static YAMLMapper getYAMLMapper() {
        return YAML_MAPPER;
    }
}
