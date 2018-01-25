package com.github.fnwib.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

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
        Optional<String> root = ValueUtil.substringBetween(null, null, null);
        Assert.assertFalse("1 --> {[text]['']['']} -> null", root.isPresent());

        Optional<String> root2 = ValueUtil.substringBetween("text", "", "");
        Assert.assertEquals("2 --> {[text]['']['']} -> [text]", "text", root2.get());

        Optional<String> root3 = ValueUtil.substringBetween("text", "", "t");
        Assert.assertEquals("3 --> {[text]['']['t']} -> [tex]", "tex", root3.get());

        Optional<String> root4 = ValueUtil.substringBetween("text", "t", "");
        Assert.assertEquals("4 --> {[text]['t']['']} -> [ext]", "ext", root4.get());

        Optional<String> root5 = ValueUtil.substringBetween("text", "t", "t");
        Assert.assertEquals("5 --> {[text]['']['']} -> [ex]", "ex", root5.get());

        Optional<String> root6 = ValueUtil.substringBetween("text", "ta", "tt");
        Assert.assertFalse("6 --> {[text]['']['']} -> null", root6.isPresent());

        Optional<String> root7 = ValueUtil.substringBetween("text", " ", "");
        Assert.assertFalse("6 --> {[text][' ']['']} -> null", root7.isPresent());

    }
}