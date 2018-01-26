package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public enum SupportType {

    Integer(java.lang.Integer.class),
    CellTitle(com.github.fnwib.databing.title.CellTitle.class),
    TitleDesc(com.github.fnwib.parse.TitleDesc.class);

    private JavaType type;

    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    SupportType(Class<?> clazz) {
        this.type = typeFactory.constructType(clazz);
    }

    public JavaType getType() {
        return type;
    }

    public static boolean support(JavaType javaType) {
        SupportType[] values = SupportType.values();
        for (SupportType value : values) {
            if (value.type.equals(javaType)) {
                return true;
            }
        }
        return false;
    }
}
