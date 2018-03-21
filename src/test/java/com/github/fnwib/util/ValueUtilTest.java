package com.github.fnwib.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ValueUtilTest {

    @Test
    public void getCellValue() {

    }

    @Test
    public void getCellValue1() {
    }

    @Test
    public void getStringValue() {
    }

    @Test
    public void substringBetween() {
        Optional<String> root = ValueUtil.substringBetweenIgnoreCase(null, null, null);
        Assert.assertFalse("1 --> {[text]['']['']} -> null", root.isPresent());

        Optional<String> root2 = ValueUtil.substringBetweenIgnoreCase("text", "", "");
        Assert.assertEquals("2 --> {[text]['']['']} -> [text]", "text", root2.get());

        Optional<String> root3 = ValueUtil.substringBetweenIgnoreCase("text", "", "t");
        Assert.assertEquals("3 --> {[text]['']['t']} -> [tex]", "tex", root3.get());

        Optional<String> root4 = ValueUtil.substringBetweenIgnoreCase("text", "t", "");
        Assert.assertEquals("4 --> {[text]['t']['']} -> [ext]", "ext", root4.get());

        Optional<String> root5 = ValueUtil.substringBetweenIgnoreCase("text", "t", "t");
        Assert.assertEquals("5 --> {[text]['']['']} -> [ex]", "ex", root5.get());

        Optional<String> root6 = ValueUtil.substringBetweenIgnoreCase("text", "ta", "tt");
        Assert.assertFalse("6 --> {[text]['']['']} -> null", root6.isPresent());

        Optional<String> root7 = ValueUtil.substringBetweenIgnoreCase("text", " ", "");
        Assert.assertFalse("6 --> {[text][' ']['']} -> null", root7.isPresent());


        Optional<String> root21 = ValueUtil.substringBetweenIgnoreCase("Text", "", "");
        Assert.assertEquals("21 --> {[text]['']['']} -> [Text]", "text", root2.get());

        Optional<String> root31 = ValueUtil.substringBetweenIgnoreCase("Text", "", "t");
        Assert.assertEquals("31 --> {[text]['']['t']} -> [tex]", "tex", root3.get());

        Optional<String> root41 = ValueUtil.substringBetweenIgnoreCase("Text", "t", "");
        Assert.assertEquals("41 --> {[text]['t']['']} -> [ext]", "ext", root4.get());

        Optional<String> root51 = ValueUtil.substringBetweenIgnoreCase("Text", "t", "t");
        Assert.assertEquals("51 --> {[text]['']['']} -> [ex]", "ex", root5.get());

        Optional<String> root61 = ValueUtil.substringBetweenIgnoreCase("Text", "ta", "tt");
        Assert.assertFalse("61 --> {[text]['']['']} -> null", root6.isPresent());

        Optional<String> root71 = ValueUtil.substringBetweenIgnoreCase("Text", " ", "");
        Assert.assertFalse("71 --> {[text][' ']['']} -> null", root7.isPresent());

    }
}