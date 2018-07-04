package com.github.fritesh.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.common.ExceptionMessages;
import com.github.fritesh.common.JsonOperation;
import com.github.fritesh.constant.JsonConstants;
import com.github.fritesh.exception.PatchException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public final class JsonPatch {

    private final List<JsonOperation> operations;
    private static ObjectMapper objectMapper;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonPatch.objectMapper = objectMapper;
    }

    @JsonCreator
    public JsonPatch(List<JsonOperation> operations) {
        this.operations = ImmutableList.copyOf(operations);
    }

    public static JsonPatch operations(final JsonNode operationNode) {
        if (operationNode == null)
            return null;
        else {
            return objectMapper.convertValue(operationNode, new TypeReference<JsonPatch>() {
            });
        }
    }

    public JsonNode applyPatch(JsonNode json) throws PatchException {


        //A Node to return that wont harm the input JSON
        JsonNode returnNode = json.deepCopy();

        for (JsonOperation operation : operations) {
            /*
            Each operation will be performed on the previously applied patch so the output json will containb
             */
            operation.setPath(resolveReference(operation.getPath(), operation.getLocator(), returnNode));
            // operation.setObjectMapper(objectMapper);
            if (operation.getPath() != null) {
                (operation).applyPatch(returnNode);
            } else {
                throw new PatchException(ExceptionMessages.EMPTY_PATH);
            }
        }
        return returnNode;
    }

    /**
     * @param path    path whose reference is to be resolved
     * @param locator helping value in case path contains references
     * @param json    the json where this path is to be found
     * @return return a valid JsonPointer with all its references resolved
     * @throws PatchException If anything wrong happens like if a reference is missing in locator or due to some reason we cant resolve the path we throw a PatchException
     */
    private JsonPointer resolveReference(JsonPointer path, JsonNode locator, JsonNode json) throws PatchException {
        if (path == null || path.isEmpty()) {
            //If path is null means that the operation is to be perform on complete data
            return path;
        } else {
            String pathString = String.valueOf(path);
            if (pathString.contains(JsonConstants.REF)) {
                JsonPointer newPath = JsonPointer.empty();
                String[] pathParts = pathString.split("/");
                for (int i = 1; i < pathParts.length; i++) {
                    String pathPart = pathParts[i];
                    {
                        if (pathPart.startsWith(JsonConstants.REF)) {
                            if (locator.has(pathPart)) {
                                newPath = findIndex(newPath, locator.get(pathPart), json);
                            } else {
                                //could not find $ref in locator
                                throw new PatchException(ExceptionMessages.REFERENCE_NOT_FOUND);
                            }
                        } else {
                            newPath = newPath.append(pathPart);
                        }
                    }
                }
                return newPath;
            } else {
                return path;
            }
        }
    }

    private JsonPointer findIndex(JsonPointer newPath, JsonNode locator, JsonNode json) throws PatchException {
        JsonNode parentNode = json.at(String.valueOf(newPath));
        if (parentNode == null) {
            //could not find parentNode
            throw new PatchException(ExceptionMessages.REFERENCE_NOT_FOUND);
        } else if (parentNode.isArray()) {
            if (locator.isObject()) {
                final Set<String> keys = Sets.newTreeSet(Sets.newHashSet(locator.fieldNames()));
                int keyMatchCount = 0;
                JsonNode actualValue;
                JsonNode expectedValue;
                for (int i = 0; i < parentNode.size(); i++) {
                    JsonNode child = parentNode.get(i);
                    keyMatchCount = 0;
                    for (String key : keys) {
                        actualValue = child.get(key);
                        expectedValue = locator.get(key);
                        if (actualValue.equals(expectedValue)) {
                            keyMatchCount++;
                        }
                    }

                    if (keyMatchCount == locator.size()) {
                        newPath = newPath.append(i);
                        break;
                    }
                }
                if (keyMatchCount == locator.size()) {
                    return newPath;
                } else {
                    //could not find  match for key/value pairs given in locator
                    throw new PatchException(ExceptionMessages.REFERENCE_NOT_FOUND);
                }
            } else {
                // locator is not an Object
                boolean notFound = true;

                for (int i = 0; i < parentNode.size(); i++) {
                    JsonNode child = parentNode.get(i);
                    if (locator.equals(child)) {
                        newPath = newPath.append(i);
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    //could not find the index
                    throw new PatchException(ExceptionMessages.REFERENCE_NOT_FOUND);
                }
            }
        } else {
            //parentNode is not array
            throw new PatchException(ExceptionMessages.UNSUPPORTED_TYPE);
        }
        return newPath;
    }
}
