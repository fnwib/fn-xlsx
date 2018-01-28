package com.github.fnwib.databing.title.impl;

import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.title.TitleValidator;
import com.github.fnwib.exception.SettingException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MultiTitleIntValidator implements TitleValidator {

    @Override
    public boolean validate(List<CellTitle> titles) {
        try {
            int reduce = titles.stream()
                    .map(CellTitle::getValue)
                    .mapToInt(Integer::parseInt).reduce(0, (l, r) -> {
                        if (l >= r) {
                            return -1;
                        }
                        return 0;
                    });
            return reduce == 0;
        } catch (Exception e) {
            log.error("string can't parse to integer", titles);
            throw new SettingException(e);
        }
    }

}
