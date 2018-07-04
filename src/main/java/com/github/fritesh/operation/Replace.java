package com.github.fritesh.operation;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.common.ExceptionMessages;
import com.github.fritesh.common.JsonFormat;
import com.github.fritesh.common.OperationType;
import com.github.fritesh.exception.PatchException;
import com.google.common.collect.Iterables;

public class Replace extends JsonFormat {

    @JsonCreator
    public Replace(@JsonProperty("path") final JsonPointer path, @JsonProperty("newValue") final JsonNode newValue, @JsonProperty("oldValue") final JsonNode oldValue, @JsonProperty("locator") final JsonNode locator) {
        super(OperationType.REPLACE, path, newValue, oldValue, locator);
    }

    @Override
    public JsonNode applyPatch(JsonNode json) throws PatchException {
        if (path == null) {
            //if path is empty we return a null node
            return newValue;
        }
        if (path.path(json).isMissingNode()) {
            //could not find value to remove at given path
            throw new PatchException(ExceptionMessages.PATH_NOT_FOUND);
        }
        final JsonNode parentNode = path.parent().get(json);
        final String lastOfPath = Iterables.getLast(path).getToken().getRaw();
        if (parentNode.isObject()) {
            ((ObjectNode) parentNode).set(lastOfPath, newValue);
        } else {
            ((ArrayNode) parentNode).add(newValue);
        }
        return json;
    }

}
