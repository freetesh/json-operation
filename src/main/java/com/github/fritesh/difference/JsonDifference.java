package com.github.fritesh.difference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.common.ExceptionMessages;
import com.github.fritesh.common.JsonOperation;
import com.github.fritesh.constant.JsonConstants;
import com.github.fritesh.exception.DifferenceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JsonDifference {

    private JsonDifference() {
    }

    private static ObjectMapper objectMapper;
    private static Logger logger = LoggerFactory.getLogger(JsonDifference.class);

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonDifference.objectMapper = objectMapper;
    }

    /**
     * This method finds difference between two JSON Node
     *
     * @param newJson       New JsonNode
     * @param oldJson       Old JsonNode on which the operations could be applied to get
     * @param primaryKeyMap Map of JsonPointer, Set<String> where JsonPointer Points to the Path in Array where U want to consider a specific set of values as primary key.
     * @return A JsonNode in the format of @JsonFormat
     * @throws DifferenceException This Library throws a Difference exception
     */
    public static  List<DifferenceOperation> findDifference(final JsonNode newJson, final JsonNode oldJson, Map<JsonPointer, Set<String>> primaryKeyMap) throws DifferenceException {

        if (oldJson == null || newJson == null) {
            logger.warn("Json is Empty!!");
            throw new DifferenceException(ExceptionMessages.EMPTY_INPUT);
        } else {
            logger.info("Finding Difference");
            final DifferenceProcessor differenceProcessor = new DifferenceProcessor();
            generateDifference(differenceProcessor, JsonPointer.empty(), newJson, oldJson, objectMapper.createObjectNode(), primaryKeyMap);
            /*
            returning the list of differences
             */
            return differenceProcessor.getDifferenceOperationList();
        }
    }

    /**
     * This method generate difference between two JSON Node
     *
     * @param differenceProcessor The Processor that process all the difference operations
     * @param path                path referring to the current place where the difference in calculated
     * @param newJson             The Previous Json value
     * @param oldJson             The New Json value
     * @param primaryKeyMap       Map of JsonPointer, Set<String> where JsonPointer Points to the Path in Array where U want to consider a specific set of values as primary key.
     * @throws DifferenceException This Library throws a Difference exception
     */
    private static void generateDifference(DifferenceProcessor differenceProcessor, JsonPointer path, JsonNode newJson, JsonNode oldJson, ObjectNode locator, Map<JsonPointer, Set<String>> primaryKeyMap) throws DifferenceException {

        if (oldJson.equals(newJson)) {
            return;
        }

        JsonNodeType oldType = oldJson.getNodeType();
        JsonNodeType newType = newJson.getNodeType();

        final int oldSize = oldJson.size();
        final int newSize = newJson.size();

        if (oldSize == 0 && newSize == 0 && oldType != newType)
            return;

        if (oldType.equals(newType) && oldJson.isContainerNode()) {
            if (oldType.equals(JsonNodeType.OBJECT))
                generateObjectDifference(differenceProcessor, path, (ObjectNode) newJson, (ObjectNode) oldJson, locator, primaryKeyMap);
            else if (oldType.equals(JsonNodeType.ARRAY))
                generateArrayDifference(differenceProcessor, path, (ArrayNode) newJson, (ArrayNode) oldJson, locator, primaryKeyMap);
            else
                throw new DifferenceException(ExceptionMessages.UNSUPPORTED_TYPE);

        } else {
            if (oldSize != 0 && oldJson.isArray()) {
                for (int i = 0; i < oldSize; i++) {
                    differenceProcessor.operationRemove(path.append(i), null, oldJson.get(i), locator);
                }

            } else if (newSize != 0 && newJson.isArray()) {
                for (JsonNode addObject : newJson) {
                    differenceProcessor.operationAdd(path.append(JsonConstants.BLT), addObject, null, locator);
                }
            } else {
              /*
            in case the type differ, irrespective of the locator we call it as replace operation.
            */
                differenceProcessor.operationReplace(path, newJson, oldJson, locator);
            }

        }
    }

    private static void generateObjectDifference(DifferenceProcessor differenceProcessor, JsonPointer pathPointer, ObjectNode newJson, ObjectNode oldJson, ObjectNode locator, Map<JsonPointer, Set<String>> primaryKeyMap) throws DifferenceException {
        final Set<String> oldJsonKeys = Sets.newTreeSet(Sets.newHashSet(oldJson.fieldNames()));
        final Set<String> newJsonKeys = Sets.newTreeSet(Sets.newHashSet(newJson.fieldNames()));


        for (String addedField : Sets.difference(oldJsonKeys, newJsonKeys)) {
            differenceProcessor.operationRemove(pathPointer.append(addedField), newJson.get(addedField), oldJson.get(addedField), locator);
        }

        for (String removedField : Sets.difference(newJsonKeys, oldJsonKeys)) {
            differenceProcessor.operationAdd(pathPointer.append(removedField), newJson.get(removedField), oldJson.get(removedField), locator);
        }

        for (String commonField : Sets.intersection(newJsonKeys, oldJsonKeys)) {
            generateDifference(differenceProcessor, pathPointer.append(commonField), newJson.get(commonField), oldJson.get(commonField), locator, primaryKeyMap);
        }

    }

    private static void generateArrayDifference(DifferenceProcessor differenceProcessor, JsonPointer pathPointer, ArrayNode newJson, ArrayNode oldJson, ObjectNode locator, Map<JsonPointer, Set<String>> primaryKeyMap) throws DifferenceException {

        Set<String> primaryKeys = primaryKeyMap == null ? null : pathValueResolver(primaryKeyMap, pathPointer);

        if (primaryKeys == null || primaryKeys.isEmpty()) {
            logger.info("As No Map was given so Calculating Array Difference in the form of Add and Remove");
            generateArrayDifferenceWithoutKeys(differenceProcessor, pathPointer, newJson, oldJson);

        } else {
            logger.info("Map was given so Calculating Array Difference in the form of Add and Remove and Replace");
            Map<Map<String, JsonNode>, Integer> oldIndexKeyValueMap = generateMapOfKeysValues(oldJson, primaryKeys);

            Map<Map<String, JsonNode>, Integer> newIndexKeyValueMap = generateMapOfKeysValues(newJson, primaryKeys);

            Set<Map<String, JsonNode>> oldKeyValueSet = oldIndexKeyValueMap.keySet();

            Set<Map<String, JsonNode>> newKeyValueSet = newIndexKeyValueMap.keySet();

            for (Map<String, JsonNode> removeObject : Sets.difference(oldKeyValueSet, newKeyValueSet)) {

                int index = oldIndexKeyValueMap.get(removeObject);
                String ref = JsonConstants.REF + String.valueOf(locator.size());
                locator.set(ref, objectMapper.convertValue(removeObject, JsonNode.class));

                differenceProcessor.operationRemove(pathPointer.append(ref), null, oldJson.get(index), locator);
                locator = objectMapper.createObjectNode();

            }

            for (Map<String, JsonNode> addObject : Sets.difference(newKeyValueSet, oldKeyValueSet)) {

                int index = newIndexKeyValueMap.get(addObject);
                String ref = JsonConstants.BLT + String.valueOf(locator.size());
                locator.set(ref, objectMapper.convertValue(addObject, JsonNode.class));

                differenceProcessor.operationAdd(pathPointer.append(ref), newJson.get(index), null, locator);
                locator = objectMapper.createObjectNode();
            }

            for (Map<String, JsonNode> replaceObject : Sets.intersection(newKeyValueSet, oldKeyValueSet)) {

                int oldIndex = oldIndexKeyValueMap.get(replaceObject);
                int newIndex = newIndexKeyValueMap.get(replaceObject);
                String ref = JsonConstants.REF + String.valueOf(locator.size());
                locator.set(ref, objectMapper.convertValue(replaceObject, JsonNode.class));
                generateDifference(differenceProcessor, pathPointer.append(ref), newJson.get(newIndex), oldJson.get(oldIndex), locator, primaryKeyMap);
                locator = objectMapper.createObjectNode();
            }
        }

    }

    private static void generateArrayDifferenceWithoutKeys(DifferenceProcessor differenceProcessor, JsonPointer pathPointer, ArrayNode newJson, ArrayNode oldJson) {
        List<JsonNode> newList = Lists.newArrayList(newJson.iterator());
        final int oldSize = oldJson.size();
        final int newSize = newJson.size();
        final int max = Math.max(oldSize, newSize);

        for (int i = 0; i < max; i++) {
            JsonNode removeObject = oldJson.get(i);
            if (newList.contains(removeObject)) {
                newList.remove(removeObject);
            } else if (removeObject != null) {
                differenceProcessor.operationRemove(pathPointer.append(JsonConstants.REF), null, removeObject, objectMapper.createObjectNode().set(JsonConstants.REF, removeObject));
            }
        }
        for (JsonNode addObject : newList) {
            differenceProcessor.operationAdd(pathPointer.append(JsonConstants.BLT), addObject, null, null);
        }
    }

    private static Map<Map<String, JsonNode>, Integer> generateMapOfKeysValues(ArrayNode json, Set<String> primaryKeys) throws DifferenceException {
        int size = json.size();
        Map<Map<String, JsonNode>, Integer> indexKeyValueMap = new HashMap<>();
        Map<String, JsonNode> keyValueMap;
        JsonNode valueNode;
        for (int i = 0; i < size; i++) {
            valueNode = json.get(i);
            keyValueMap = new HashMap<>();
            for (String primaryKey : primaryKeys) {
                if (valueNode.has(primaryKey)) {
                    keyValueMap.put(primaryKey, valueNode.get(primaryKey));
                } else {
                    //Primary keys are always expected to be present if the path is given in Map
                    throw new DifferenceException(ExceptionMessages.EXPECTED_TO_BE_PRESENT);
                }
            }
            indexKeyValueMap.put(keyValueMap, i);
        }
        return indexKeyValueMap;
    }

    private static Set<String> pathValueResolver(Map<JsonPointer, Set<String>> primaryKeyMap, JsonPointer path) {
        Set<String> primaryKeys = primaryKeyMap.get(path);
        if (primaryKeys == null || primaryKeys.isEmpty()) {
            String pathString = path.toString();
            if (pathString.contains(JsonConstants.REF)) {
                JsonPointer newPath = JsonPointer.empty();
                String[] pathParts = pathString.split("/");
                for (String pathPart : pathParts) {
                    newPath = newPath.append(pathPart);
                    if (pathPart.startsWith(JsonConstants.REF) && primaryKeyMap.get(newPath) != null) {
                        return new TreeSet<>();
                    }
                }
            }
            return primaryKeys;

        } else {
            return primaryKeys;
        }
    }
}
