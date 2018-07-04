package com.github.fritesh.common;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fritesh.difference.DifferenceOperation;
import com.github.fritesh.difference.JsonDifference;
import com.github.fritesh.exception.DifferenceException;
import com.github.fritesh.exception.PatchException;
import com.github.fritesh.patch.JsonPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestJsonOperation {
    Logger logger = LoggerFactory.getLogger(TestJsonOperation.class);
    static ObjectMapper objectMapper = new ObjectMapper();


    @Test(description = "Test Add and Remove JsonDifference and JsonPatch", dataProvider = "Test (Add and Remove) Difference and Patch Combine", dataProviderClass = TestDataProvider.class)
    public void testAddRemoveWithoutMap(String testName, Map<JsonPointer, Set<String>> map, JsonNode newJson, JsonNode oldJson) throws DifferenceException, PatchException {
        this.testMainMethod(testName, map, newJson, oldJson);
    }

    @Test(description = "Test Replace Operations JsonDifference and JsonPatch", dataProvider = "Replace Operations", dataProviderClass = TestDataProvider.class)
    public void testReplaceOperation(String testName, Map<JsonPointer, Set<String>> map, JsonNode newJson, JsonNode oldJson) throws DifferenceException, PatchException {
        this.testMainMethod(testName, map,newJson, oldJson);
    }

    private void testMainMethod(String testName, Map<JsonPointer, Set<String>> map,JsonNode newJson, JsonNode oldJson) throws DifferenceException, PatchException {
        logger.info(testName);
        JsonDifference.setObjectMapper(objectMapper);
        List<DifferenceOperation> difference = JsonDifference.findDifference(newJson, oldJson, map);
        logger.info("difference:  {}", difference);

        JsonPatch.setObjectMapper(objectMapper);

        JsonPatch jsonPatch = JsonPatch.operations(objectMapper.convertValue(difference,JsonNode.class));
        JsonNode afterPatch = jsonPatch.applyPatch(oldJson);

        logger.info("OldJson :  {}", oldJson);
        logger.info("After Patch: {}", afterPatch);
        logger.info("NewJson :  {}", newJson);
        Assert.assertEquals(afterPatch.size(), newJson.size());

        List<DifferenceOperation> difference2 = JsonDifference.findDifference(newJson, afterPatch, map);
        Assert.assertTrue(difference2.isEmpty());
        logger.info("difference 2: {} \n", difference2);
    }
}
