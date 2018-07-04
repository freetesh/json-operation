package com.github.fritesh.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.google.common.collect.Lists;

import java.util.List;

final class DifferenceProcessor {
    private final List<DifferenceOperation> differenceOperationList = Lists.newArrayList();

    public List<DifferenceOperation> getDifferenceOperationList() {
        return differenceOperationList;
    }

    void operationAdd(final JsonPointer path, JsonNode newValue, JsonNode oldValue, JsonNode locator){
        DifferenceOperation newDifference = DifferenceOperation.add(path, oldValue, newValue, locator);
        this.addToList(newDifference);

    }

    void operationRemove(final JsonPointer path, JsonNode newValue, JsonNode oldValue, JsonNode locator) {
        DifferenceOperation newDifference = DifferenceOperation.remove(path, oldValue, newValue, locator);
        this.addToList(newDifference);
    }

    void operationReplace(final JsonPointer path, JsonNode newValue, JsonNode oldValue, JsonNode locator) {
        DifferenceOperation newDifference = DifferenceOperation.replace(path, oldValue, newValue, locator);
        this.addToList(newDifference);
    }

    private void addToList( DifferenceOperation newDifference){
        if (!differenceOperationList.contains(newDifference))
            differenceOperationList.add(newDifference);
    }


}
