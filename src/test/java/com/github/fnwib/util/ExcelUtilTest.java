package com.github.fnwib.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExcelUtilTest {

    @Test
    public void test() {
        test2("A");
        test2("AA");
        test2("AAAA");
    }

    public void test2(String aaa) {
        int colIndex = ExcelUtil.column2Num(aaa);
        String newColStr = ExcelUtil.num2Column(colIndex);
        Assert.assertEquals("AA", aaa, newColStr);
    }
}