package com.github.fritesh.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;

import java.util.Map;
import java.util.Set;

public class DifferenceParamenter {

    JsonNode oldJson;

    JsonNode newJson;

    Map<JsonPointer, Set<String>> keyValueMap;

    public JsonNode getOldJson() {
        return oldJson;
    }

    public JsonNode getNewJson() {
        return newJson;
    }

    public Map<JsonPointer, Set<String>> getKeyValueMap() {
        return keyValueMap;
    }
}
