package ua.ubki.cassmon.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ua.ubki.cassmon.exception.UbkiException;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.ubki.cassmon.utils.Const.UBKI_DATETIME_FORMAT;
import static ua.ubki.cassmon.utils.Const.UBKI_DATE_FORMATTER;

public class JsonUtils {

    private JsonUtils() {
        //
    }

    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(null, null);
    }

    public static ObjectMapper getObjectMapper(Map<SerializationFeature, Boolean> serializationFeature,
                                               Map<DeserializationFeature, Boolean> deserializationFeature) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.registerModule(new JavaTimeModule()
                // Serializers
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(UBKI_DATETIME_FORMAT))
                .addSerializer(LocalDate.class, new LocalDateSerializer(UBKI_DATE_FORMATTER))
                .addSerializer(BigDecimal.class, new DecimalJsonSerializer())

                // Deserializers
                .addDeserializer(BigDecimal.class, new DecimalJsonDeserializer())
                .addDeserializer(LocalDate.class, new LocalDateJsonDeserializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeJsonDeserializer())
        );

        if (serializationFeature != null) {
            serializationFeature.forEach(objectMapper::configure);
        }
        if (deserializationFeature != null) {
            deserializationFeature.forEach(objectMapper::configure);
        }

        return objectMapper;
    }

    public static String buildJsonString(Object value) {
        return buildJson(value, null);
    }

    public static String buildJsonString(Object value, Map<SerializationFeature, Boolean> serializationFeature) {
        return buildJson(value, serializationFeature);
    }

    private static String buildJson(Object value, Map<SerializationFeature, Boolean> serializationFeature) {
        if (value == null) {
            return "null";
        }

        ObjectMapper objectMapper = getObjectMapper(serializationFeature, null);

        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }
        return jsonString;
    }

    public static void buildJsonFile(Object value, String fileName) {
        buildJson(value, new File(fileName), null);
    }

    public static void buildJsonFile(Object value, File fileName) {
        buildJson(value, fileName, null);
    }

    public static void buildJsonFile(Object value, String fileName, Map<SerializationFeature, Boolean> serializationFeature) {
        buildJson(value, new File(fileName), serializationFeature);
    }

    public static void buildJsonFile(Object value, File fileName, Map<SerializationFeature, Boolean> serializationFeature) {
        buildJson(value, fileName, serializationFeature);
    }

    private static void buildJson(Object value, File fileName, Map<SerializationFeature, Boolean> serializationFeature) {
        ObjectMapper objectMapper = getObjectMapper(serializationFeature, null);
        try {
            objectMapper.writeValue(fileName, value);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }
    }

    public static <T> T buildObjectFromJsonString(String jsonString, Class<T> targetObjectClass) {
        return buildObject(jsonString, targetObjectClass, null);
    }

    public static <T> T buildObjectFromJsonString(String jsonString, Class<T> targetObjectClass,
                                                  Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildObject(jsonString, targetObjectClass, deserializationFeature);
    }

    private static <T> T buildObject(String jsonString, Class<T> targetObjectClass,
                                     Map<DeserializationFeature, Boolean> deserializationFeature) {
        if (jsonString == null) {
            return null;
        }
        T result;
        ObjectMapper objectMapper = getObjectMapper(null, deserializationFeature);
        try {
            result = objectMapper.readValue(jsonString, targetObjectClass);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }
        return result;
    }

    public static <T> T buildObjectFromJsonFile(File fileName, Class<T> targetObjectClass) {
        return buildObject(fileName, targetObjectClass, null);
    }

    public static <T> T buildObjectFromJsonFile(String fileName, Class<T> targetObjectClass) {
        return buildObject(new File(fileName), targetObjectClass, null);
    }

    public static <T> T buildObjectFromJsonFile(File fileName, Class<T> targetObjectClass,
                                                Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildObject(fileName, targetObjectClass, deserializationFeature);
    }

    public static <T> T buildObjectFromJsonFile(String fileName, Class<T> targetObjectClass,
                                                Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildObject(new File(fileName), targetObjectClass, deserializationFeature);
    }

    private static <T> T buildObject(File fileName, Class<T> targetObjectClass,
                                     Map<DeserializationFeature, Boolean> deserializationFeature) {
        if (!fileName.exists()) {
            throw new UbkiException(500, "Json file not found");
        }

        T result;
        try {
            result = getObjectMapper(null, deserializationFeature)
                    .readValue(fileName, targetObjectClass);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }

        return result;
    }

    public static <T> List<T> buildListFromJsonString(String jsonString, Class<T> targetObjectClass) {
        return buildList(jsonString, targetObjectClass, null);
    }

    public static <T> List<T> buildListFromJsonString(String jsonString, Class<T> targetObjectClass,
                                                      Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildList(jsonString, targetObjectClass, deserializationFeature);
    }

    private static <T> List<T> buildList(String jsonString, Class<T> targetObjectClass,
                                         Map<DeserializationFeature, Boolean> deserializationFeature) {
        if (jsonString == null) {
            return new ArrayList<>();
        }

        ObjectMapper objectMapper = getObjectMapper(null, deserializationFeature);
        JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, targetObjectClass);
        List<T> result;

        try {
            result = objectMapper.readValue(jsonString, listType);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }

        return result;
    }

    public static <T> List<T> buildListFromJsonFile(File fileName, Class<T> targetObjectClass) {
        return buildList(fileName, targetObjectClass, null);
    }

    public static <T> List<T> buildListFromJsonFile(File fileName, Class<T> targetObjectClass,
                                                    Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildList(fileName, targetObjectClass, deserializationFeature);
    }

    public static <T> List<T> buildListFromJsonFile(String fileName, Class<T> targetObjectClass) {
        return buildList(new File(fileName), targetObjectClass, null);
    }

    public static <T> List<T> buildListFromJsonFile(String fileName, Class<T> targetObjectClass,
                                                    Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildList(new File(fileName), targetObjectClass, deserializationFeature);
    }

    private static <T> List<T> buildList(File fileName, Class<T> targetObjectClass,
                                         Map<DeserializationFeature, Boolean> deserializationFeature) {
        ObjectMapper objectMapper = getObjectMapper(null, deserializationFeature);
        JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, targetObjectClass);
        List<T> result;

        try {
            result = objectMapper.readValue(fileName, listType);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }

        return result;
    }

    public static <T, P> Map<T, P> buildMapFromJsonString(String jsonString, Class<T> keyClass, Class<P> valueClass) {
        return buildMap(jsonString, keyClass, valueClass, null);
    }

    public static <T, P> Map<T, P> buildMapFromJsonString(String jsonString, Class<T> keyClass, Class<P> valueClass,
                                                          Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildMap(jsonString, keyClass, valueClass, deserializationFeature);
    }

    private static <T, P> Map<T, P> buildMap(String jsonString, Class<T> keyClass, Class<P> valueClass,
                                             Map<DeserializationFeature, Boolean> deserializationFeature) {
        if (jsonString == null) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = getObjectMapper(null, deserializationFeature);
        JavaType listType = objectMapper.getTypeFactory()
                .constructMapType(HashMap.class, keyClass, valueClass);
        Map<T, P> result;

        try {
            result = objectMapper.readValue(jsonString, listType);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }

        return result;
    }

    public static <T, P> Map<T, P> buildMapFromJsonFile(File fileName, Class<T> keyClass, Class<P> valueClass) {
        return buildMap(fileName, keyClass, valueClass, null);
    }

    public static <T, P> Map<T, P> buildMapFromJsonFile(File fileName, Class<T> keyClass, Class<P> valueClass,
                                                        Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildMap(fileName, keyClass, valueClass, deserializationFeature);
    }

    public static <T, P> Map<T, P> buildMapFromJsonFile(String fileName, Class<T> keyClass, Class<P> valueClass) {
        return buildMap(new File(fileName), keyClass, valueClass, null);
    }

    public static <T, P> Map<T, P> buildMapFromJsonFile(String fileName, Class<T> keyClass, Class<P> valueClass,
                                                        Map<DeserializationFeature, Boolean> deserializationFeature) {
        return buildMap(new File(fileName), keyClass, valueClass, deserializationFeature);
    }

    private static <T, P> Map<T, P> buildMap(File fileName, Class<T> keyClass, Class<P> valueClass,
                                             Map<DeserializationFeature, Boolean> deserializationFeature) {
        ObjectMapper objectMapper = getObjectMapper(null, deserializationFeature);
        JavaType listType = objectMapper.getTypeFactory()
                .constructMapType(HashMap.class, keyClass, valueClass);
        Map<T, P> result;

        try {
            result = objectMapper.readValue(fileName, listType);
        } catch (Exception ex) {
            throw new UbkiException(500, "Json error", ex);
        }

        return result;
    }

}
