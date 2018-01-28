package com.github.fnwib.databing.title;

import java.util.List;

/**
 * 单字段匹配到多列数据title顺序检查
 * <p>
 * 可以抛出异常 自定义错误信息
 * <p>
 * titles 是原集合的deep clone对象
 */
public interface TitleValidator {

    boolean validate(List<CellTitle> titles);

}
