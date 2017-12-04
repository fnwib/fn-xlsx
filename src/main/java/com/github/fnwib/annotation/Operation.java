package com.github.fnwib.annotation;

/**
 * value == 0  不应该匹配到任务Excel列
 * value == 1  只能匹配到最多一列
 * value == 2  可以匹配到任意数量列
 */
public enum Operation {
    LINE_NUM,
    DEFAULT,
    REORDER
}
