package com.github.fritesh.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;

public abstract class JsonFormat extends JsonOperation {

    public JsonFormat(OperationType op, JsonPointer path, JsonNode newValue, JsonNode oldValue, JsonNode locator) {
        super(op, path, newValue, oldValue, locator);
    }
}
