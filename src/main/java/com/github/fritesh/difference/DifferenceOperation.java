package com.github.fritesh.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.common.OperationType;

public final class DifferenceOperation {
    private final OperationType op;

    private final JsonPointer path;

    private final JsonNode oldValue;

    private final JsonNode newValue;

    private final JsonNode locator;

    protected DifferenceOperation(OperationType op, JsonPointer path, JsonNode oldValue, JsonNode newValue, JsonNode locator) {
        this.op=op;
        this.path=path;
        this.oldValue=oldValue;
        this.newValue=newValue;
        this.locator=locator;
    }

    public OperationType getOp() {
        return op;
    }

    public JsonPointer getPath() {
        return path;
    }

    public JsonNode getOldValue() {
        return oldValue;
    }

    public JsonNode getNewValue() {
        return newValue;
    }

    public JsonNode getLocator() {
        return locator;
    }

    static DifferenceOperation add(final JsonPointer path,final JsonNode oldValue, final JsonNode newValue, final JsonNode locator){
        return new DifferenceOperation(OperationType.ADD,path,oldValue,newValue,locator);
    }

    static DifferenceOperation remove(final JsonPointer path,final JsonNode oldValue, final JsonNode newValue, final JsonNode locator){
        return new DifferenceOperation(OperationType.REMOVE,path,oldValue,newValue,locator);
    }

    static DifferenceOperation replace(final JsonPointer path,final JsonNode oldValue, final JsonNode newValue, final JsonNode locator){
        return new DifferenceOperation(OperationType.REPLACE,path,oldValue,newValue,locator);
    }
}
