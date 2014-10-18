package com.digitalbuddha.daggerdemo.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Nakhimovich on 6/13/14.
 */
public class JsonParser {
    @Inject
    public ObjectMapper mapper;

    public String convertObjectToJSON(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object convertJsonToObject(JSONObject jsonObject, Class clazz) {
        try {
            return mapper.readValue(jsonObject.toString(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object convertJsonToList(JSONArray jsonObject, Class clazz) {
        try {
            return mapper.readValue(jsonObject.toString(), mapper.getTypeFactory().constructCollectionType(
                    List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
