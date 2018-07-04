package com.github.fritesh.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TestDataProvider {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "Test (Add and Remove) Difference and Patch Combine")
    public static Object[][] provideDataDiff1() throws IOException {
        String add_1_KeyValueToObject = "Test: Adding Key<->Value[String,Integer,boolean,null] to an Object";
        String add_2_ValueToArray = "Test: Add Value[String,Integer,boolean,null] to an Array";
        String add_3_ObjectToObject = "Test: Adding  Object as Value to a Key in Object (Object's -> key) <-> Value";
        String add_4_ObjectToArray = "Test: Adding Object to an Array ( Array -> Object)";
        String add_5_ArrayToObject = "Test Adding Array as Value to an Object's Key (Objects -> Key) <-> Array(as Value))";
        String add_6_KeyValueToAnArrayObject = "Test: Adding Key <-> Value to an Array's Object";

        JsonNode add_1_KeyValueToObjectNew = objectMapper.readTree(new File("src/test/resources/add_1_KeyValueToObjectNew.json"));
        JsonNode add_1_KeyValueToObjectOld = objectMapper.readTree(new File("src/test/resources/add_1_KeyValueToObjectOld.json"));

        JsonNode add_2_ValueToArrayNew = objectMapper.readTree(new File("src/test/resources/add_2_ValueToArrayNew.json"));
        JsonNode add_2_ValueToArrayOld = objectMapper.readTree(new File("src/test/resources/add_2_ValueToArrayOld.json"));

        JsonNode add_3_ObjectToObjectNew = objectMapper.readTree(new File("src/test/resources/add_3_ObjectToObjectNew.json"));
        JsonNode add_3_ObjectToObjectOld = objectMapper.readTree(new File("src/test/resources/add_3_ObjectToObjectOld.json"));

        JsonNode add_4_ObjectToArrayNew = objectMapper.readTree(new File("src/test/resources/add_4_ObjectToArrayNew.json"));
        JsonNode add_4_ObjectToArrayOld = objectMapper.readTree(new File("src/test/resources/add_4_ObjectToArrayOld.json"));

        JsonNode add_5_ArrayToObjectNew = objectMapper.readTree(new File("src/test/resources/add_5_ArrayToObjectNew.json"));
        JsonNode add_5_ArrayToObjectOld = objectMapper.readTree(new File("src/test/resources/add_5_ArrayToObjectOld.json"));

        JsonNode add_6_KeyValueToAnArrayObjectNew = objectMapper.readTree(new File("src/test/resources/add_6_KeyValueToAnArrayObjectNew.json"));
        JsonNode add_6_KeyValueToAnArrayObjectOld = objectMapper.readTree(new File("src/test/resources/add_6_KeyValueToAnArrayObjectOld.json"));

        String remove_1_KeyValueToObject = "Test: Removing Key<->Value[String,Integer,boolean,null] to an Object";
        String remove_2_ValueToArray = "Test: Removing Value[String,Integer,boolean,null] to an Array";
        String remove_3_ObjectToObject = "Test: Removing  Object as Value to a Key in Object (Object's -> key) <-> Value";
        String remove_4_ObjectToArray = "Test: Removing Object to an Array ( Array -> Object)";
        String remove_5_ArrayToObject = "Test Removing Array as Value to an Object's Key (Objects -> Key) <-> Array(as Value))";
        String remove_6_KeyValueToAnArrayObject = "Test: Removing Key <-> Value to an Array's Object";

        return new Object[][]{
                /* Test Name, New Json, Old Json*/

                {add_1_KeyValueToObject, null, add_1_KeyValueToObjectNew, add_1_KeyValueToObjectOld},
                {add_2_ValueToArray, null, add_2_ValueToArrayNew, add_2_ValueToArrayOld},
                {add_3_ObjectToObject, null, add_3_ObjectToObjectNew, add_3_ObjectToObjectOld},
                {add_4_ObjectToArray, null, add_4_ObjectToArrayNew, add_4_ObjectToArrayOld},
                {add_5_ArrayToObject, null, add_5_ArrayToObjectNew, add_5_ArrayToObjectOld},
                {add_6_KeyValueToAnArrayObject, null, add_6_KeyValueToAnArrayObjectNew, add_6_KeyValueToAnArrayObjectOld},


                {remove_1_KeyValueToObject, null, add_1_KeyValueToObjectOld, add_1_KeyValueToObjectNew},
                {remove_2_ValueToArray, null, add_2_ValueToArrayOld, add_2_ValueToArrayNew},
                {remove_3_ObjectToObject, null, add_3_ObjectToObjectOld, add_3_ObjectToObjectNew},
                {remove_4_ObjectToArray, null, add_4_ObjectToArrayOld, add_4_ObjectToArrayNew},
                {remove_5_ArrayToObject, null, add_5_ArrayToObjectOld, add_5_ArrayToObjectNew},
                {remove_6_KeyValueToAnArrayObject, null, add_6_KeyValueToAnArrayObjectOld, add_6_KeyValueToAnArrayObjectNew}
        };
    }

    @DataProvider(name = "Replace Operations")
    public Object[][] replaceOperations() throws IOException {
        String replace_1_ObjectValue = "Change Value of a Key in an Object";
        JsonNode replace_1_ObjectValueNew = objectMapper.readTree(new File("src/test/resources/replace_1_ObjectValueNew.json"));
        JsonNode replace_1_ObjectValueOld = objectMapper.readTree(new File("src/test/resources/replace_1_ObjectValueOld.json"));

        String replace_1_ArrayObjectsValueWithMap = "Change Value of a Key in an Object";
        Map<JsonPointer, Set<String>> replace_1_Map = new HashMap<>();
        Set<String> replace_1_Set = new TreeSet<>();
        replace_1_Set.add("Name");
        JsonPointer p1 = JsonPointer.empty();
        replace_1_Map.put(p1, replace_1_Set);


        JsonNode replace_1_ArrayObjectsValueWithMapNew = objectMapper.readTree(new File("src/test/resources/replace_1_ArrayObjectsValueWithMapNew.json"));
        JsonNode replace_1_ArrayObjectsValueWithMapOld = objectMapper.readTree(new File("src/test/resources/replace_1_ArrayObjectsValueWithMapOld.json"));


        return new Object[][]{
                {replace_1_ObjectValue, null, replace_1_ObjectValueNew, replace_1_ObjectValueOld},
                {replace_1_ArrayObjectsValueWithMap, replace_1_Map, replace_1_ArrayObjectsValueWithMapNew, replace_1_ArrayObjectsValueWithMapOld}
        };
    }


}
