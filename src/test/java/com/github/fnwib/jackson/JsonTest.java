package com.github.fnwib.jackson;

import model.AutoMappingModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonTest {

    @Test
    public void convertValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("lineNum", 11);
        AutoMappingModel autoMappingModel = Json.Mapper.convertValue(map, AutoMappingModel.class);
        Assert.assertSame("Optional support", 11, autoMappingModel.getLineNum());
    }

    @Test
    public void convertValue2() {
        Map<String, Object> map = new HashMap<>();
        map.put("text1", Optional.of("text1"));
        AutoMappingModel autoMappingModel = Json.Mapper.convertValue(map, AutoMappingModel.class);
        Assert.assertEquals("Optional support", "text1", autoMappingModel.getText1());
    }

    @Test
    public void convertValue3() {
        Map<String, Object> map = new HashMap<>();
        map.put("text1", "text1");
        AutoMappingModel autoMappingModel = Json.Mapper.convertValue(map, AutoMappingModel.class);
        Assert.assertEquals("Optional support", "text1", autoMappingModel.getText1());
    }
}