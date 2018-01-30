package com.github.fnwib.reflect;

import model.WriteModel;
import org.junit.Assert;
import org.junit.Test;

public class BeanResolverTest {

    @Test
    public void format() {
        WriteModel writeModel = new WriteModel();
        writeModel.setAaa("Text  t / a");
        writeModel.setSequence(1);
        writeModel.setString("a");

        WriteModel format = BeanResolver.INSTANCE.format(writeModel);
        Assert.assertEquals("copy value use @ReadValueHandler", "A/TEXT  T", format.getAaa());
        Assert.assertEquals("copy value use @ReadValueHandler", Integer.valueOf(1), format.getSequence());
        Assert.assertEquals("copy value use @ReadValueHandler", "a", format.getString());


    }
}