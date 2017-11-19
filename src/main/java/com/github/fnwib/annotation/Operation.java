package com.github.fnwib.annotation;

/**
 * value == 0  不应该匹配到任务Excel列
 * value == 1  只能匹配到最多一列
 * value == 2  可以匹配到任意数量列
 */
public enum Operation {
    LINE_NUM(0),
    DEFAULT(1),
    REORDER(2);

    private int value;

    Operation(int value) {
        this.value = value;
    }

    /**
     * 匹配到的列是否是无效的
     *
     * @param titleNameSize
     * @return
     */
    public boolean isInvalid(int titleNameSize) {
        return (value == 0 || value == 1) && titleNameSize == value;
    }


}
