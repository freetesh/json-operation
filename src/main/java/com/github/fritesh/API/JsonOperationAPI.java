package com.github.fritesh.API;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fritesh.common.DifferenceParamenter;
import com.github.fritesh.common.PatchParameter;
import com.github.fritesh.difference.DifferenceOperation;
import com.github.fritesh.difference.JsonDifference;
import com.github.fritesh.exception.DifferenceException;
import com.github.fritesh.exception.PatchException;
import com.github.fritesh.patch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JsonOperationAPI {

    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @RequestMapping(value = "/difference", method = RequestMethod.POST)
    private List<DifferenceOperation> difference(@RequestBody DifferenceParamenter differenceParamenter) throws DifferenceException {
        JsonDifference.setObjectMapper(objectMapper);
        return JsonDifference.findDifference(differenceParamenter.getNewJson(),differenceParamenter.getOldJson(),differenceParamenter.getKeyValueMap());
    }

    @RequestMapping(value= "/patch",method=RequestMethod.POST)
    private JsonNode patch(@RequestBody PatchParameter patchParameter) throws PatchException {
        JsonPatch.setObjectMapper(objectMapper);
        JsonPatch jsonPatch = JsonPatch.operations(patchParameter.getOperations());
        return jsonPatch.applyPatch(patchParameter.getOriginalJson());
    }

}
