package com.github.fritesh.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.common.ExceptionMessages;
import com.github.fritesh.common.JsonFormat;
import com.github.fritesh.common.OperationType;
import com.github.fritesh.constant.JsonConstants;
import com.github.fritesh.exception.PatchException;
import com.google.common.collect.Iterables;

public class Add extends JsonFormat {

    @JsonCreator
    public Add(@JsonProperty("path") final JsonPointer path, @JsonProperty("newValue") final JsonNode newValue, @JsonProperty("oldValue") final JsonNode oldValue, @JsonProperty("locator") final JsonNode locator) {
        super(OperationType.ADD, path, newValue, oldValue, locator);
    }

    @Override
    public JsonNode applyPatch(JsonNode json) throws PatchException {

        if (path == null) {
            //if path is empty we return a null node
            return newValue;
        }
        JsonNode parentNode;
        String pathString = path.toString();
        if (pathString.contains(JsonConstants.BLT)) {
            parentNode = resolveBuilt(pathString, json);
        } else {
            parentNode = path.parent().get(json);
        }
        if (parentNode ==null || parentNode.isMissingNode()) {
            throw new PatchException(ExceptionMessages.PATH_NOT_FOUND);
        }
        if (parentNode.isObject()) {

            String lastOfPath = Iterables.getLast(path).getToken().getRaw();
            ((ObjectNode) parentNode).set(lastOfPath, newValue);
        } else {
            ((ArrayNode) parentNode).add(newValue);
        }
        return json;
    }

    private JsonNode resolveBuilt(String pathString, JsonNode json) {

        JsonNode updateNode = null;
        JsonPointer newPath = JsonPointer.empty();
        String[] pathParts = pathString.split("/");
        for (int i = 1; i < pathParts.length; i++) {
            String pathPart = pathParts[i];
            if (pathPart.startsWith(JsonConstants.BLT)) {
                if (newPath.path(json) == null || newPath.path(json).isNull()) {
                    updateNode = newPath.parent().path(json);
                    ((ObjectNode) updateNode).set(pathParts[i - 1], new ObjectMapper().createArrayNode());
                    updateNode = updateNode.get(pathParts[i - 1]);
                } else {
                    updateNode = newPath.path(json);
                }

            } else {
                newPath = newPath.append(pathPart);
            }
        }
        return updateNode;
    }
}
