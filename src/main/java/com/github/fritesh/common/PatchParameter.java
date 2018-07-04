package com.github.fritesh.common;

import com.fasterxml.jackson.databind.JsonNode;

public class PatchParameter {

    JsonNode originalJson;

    JsonNode operations;

    public JsonNode getOriginalJson() {
        return originalJson;
    }

    public JsonNode getOperations() {
        return operations;
    }
}
