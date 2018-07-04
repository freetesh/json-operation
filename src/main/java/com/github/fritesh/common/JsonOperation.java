package com.github.fritesh.common;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.exception.PatchException;
import com.github.fritesh.operation.Add;
import com.github.fritesh.operation.Remove;
import com.github.fritesh.operation.Replace;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "op")

@JsonSubTypes({
        @JsonSubTypes.Type(name = "ADD", value = Add.class),
        @JsonSubTypes.Type(name = "REMOVE", value = Remove.class),
        @JsonSubTypes.Type(name = "REPLACE", value = Replace.class),
})

public abstract class JsonOperation {



    private final OperationType op;

    protected JsonPointer path;

    protected final JsonNode newValue;

    private final JsonNode oldValue;

    private final JsonNode locator;

    /**
     * @param op       operation to perform {"add","remove","delete"}.
     * @param path     path at which the operation should be performed in RFC 6901 format.
     * @param newValue new value that the path will have.
     * @param oldValue previous value it path had
     * @param locator  json which hasvallue that will help locate indexes in Array
     */
    protected JsonOperation(OperationType op, JsonPointer path, JsonNode newValue, JsonNode oldValue, JsonNode locator) {
        this.op = op;
        this.path = path;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.locator = locator;

    }

    public abstract JsonNode applyPatch(final JsonNode json) throws PatchException;

    public JsonPointer getPath() {
        return path;
    }

    public void setPath(JsonPointer path) {
        this.path = path;
    }

    public JsonNode getLocator() {
        return locator;
    }
}
