package com.github.fnwib.operation;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.util.MiddleDifference;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class Title {

    private static final Logger LOGGER = LoggerFactory.getLogger(Title.class);

    private final String          fieldName;
    private final CellType        cellType;
    private final List<TitleDesc> list;
    private final int             min;

    private ExcelConverter<?> converter;

    public Title(String fieldName, CellType cellType) {
        this.fieldName = fieldName;
        this.cellType = cellType;
        this.list = Collections.EMPTY_LIST;
        this.min = 0;
    }

    public Title(String fieldName, CellType cellType, List<TitleDesc> list) {
        this.fieldName = fieldName;
        this.cellType = cellType;
        this.list = list;
        OptionalInt min = list.stream().mapToInt(TitleDesc::getIndex).min();
        if (min.isPresent()){
            this.min = min.getAsInt();
        }else {
            this.min =0;
        }
    }

    public ExcelConverter<?> getConverter() {
        return converter;
    }

    public void setConverter(ExcelConverter<?> converter) {
        this.converter = converter;
    }

    public String getFieldName() {
        return fieldName;
    }

    public CellType getCellType() {
        return cellType;
    }

    public List<TitleDesc> getList() {
        return list;
    }

    public String getExcelTitles() {
        List<String> list = this.list.stream().map(TitleDesc::getTitle).collect(Collectors.toList());
        return Joiner.on(",").join(list);
    }

    public int getMinIndex() {
        return this.min;
    }

    public boolean isSerial() {
        if (this.list.size() <= 1) {
            return true;
        }
        List<String> titles = this.list.stream().map(TitleDesc::getTitle).collect(Collectors.toList());
        MiddleDifference difference = new MiddleDifference();
        MiddleDifference.Between between = difference.getMiddleDifference(titles);
        LOGGER.debug("---> fieldName is '{}', title is '{}' ", fieldName, titles);
        boolean parsable = titles.stream()
                .map(s -> s.substring(between.getPositiveOffset(), s.length() - 1 - between.getFlashbackOffSet()))
                .anyMatch(s -> NumberUtils.isParsable(s));
        List<TitleDesc> titleDescList;

        if (parsable) {
            for (TitleDesc desc : this.list) {
                String title = desc.getTitle();
                String seq = title.substring(between.getPositiveOffset(), title.length() - between.getFlashbackOffSet());
                desc.setSeq(NumberUtils.createInteger(seq));
            }

            titleDescList = this.list.stream().sorted(Comparator.comparing(TitleDesc::getSeq))
                    .collect(Collectors.toList());

            LOGGER.debug(" -> sort by is '{}' ", titleDescList.stream().map(TitleDesc::getSeq).collect(Collectors.toList()));
        } else {
            titleDescList = this.list.stream().sorted(Comparator.comparing(TitleDesc::getTitle))
                    .collect(Collectors.toList());
            LOGGER.debug("-> sort by is '{}' ", titleDescList.stream().map(TitleDesc::getTitle).collect(Collectors.toList()));
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        for (TitleDesc desc : titleDescList) {
            if (desc.getIndex() + 1 - min != atomicInteger.addAndGet(1)) {
                return false;
            }
        }
        return true;
    }


}
