package com.github.fnwib.databing.title.match;

import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.title.CellTitle;

import java.util.List;

@Deprecated
public interface TitleMatcher {

    List<CellTitle> match(List<CellTitle> titles);

    Operation getOperation();
}
