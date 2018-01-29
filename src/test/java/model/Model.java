package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Model {
    @CellType(operation = Operation.LINE_NUM)
    private Integer lineNum;
    @CellType(title = "Text One")
    private String text1;
    @CellType(title = "Text Two")
    private String text2;

    @CellType(title = "Text Reorder", operation = Operation.REORDER)
    private String text3;

    @CellType(title = "Number")
    private Integer    intNum;
    @CellType(title = "Number")
    private Long       longNum;
    @CellType(title = "Number")
    private Float      floatNum;
    @CellType(title = "Number")
    private Double     doubleNum;
    @CellType(title = "Number")
    private BigDecimal bigDecimal;

    @CellType(title = "Number Null")
    private Integer intNumNull;

    @CellType(title = "LocalDate1")
    private LocalDate localDate1;
    @CellType(title = "LocalDate2")
    private LocalDate localDate2;
    @CellType(title = "LocalDate3")
    private LocalDate localDate3;


    @CellType(title = "yyyy-MM-dd")
    private LocalDate localDate4;
    @CellType(title = "yyyy/MM/dd")
    private LocalDate localDate5;
    @CellType(title = "yyyy/MM/dd2")
    private LocalDate localDate8;
    @CellType(title = "yyyy\\\\MM\\\\dd")
    private LocalDate localDate6;
    @CellType(title = "yyyyMMdd")
    private LocalDate localDate7;

    @CellType(title = "Map \\d+")
    private Map<Integer, String> intKeyMap;

    @CellType(title = "Map [A-Z]")
    private Map<Integer, String> stringKeyMap;

    @CellType(title = "Map 1 \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap2;

    @CellType(title = "Map \\d+ \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap3;

    @CellType(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public Integer getIntNum() {
        return intNum;
    }

    public void setIntNum(Integer intNum) {
        this.intNum = intNum;
    }

    public Long getLongNum() {
        return longNum;
    }

    public void setLongNum(Long longNum) {
        this.longNum = longNum;
    }

    public Float getFloatNum() {
        return floatNum;
    }

    public void setFloatNum(Float floatNum) {
        this.floatNum = floatNum;
    }

    public Double getDoubleNum() {
        return doubleNum;
    }

    public void setDoubleNum(Double doubleNum) {
        this.doubleNum = doubleNum;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Integer getIntNumNull() {
        return intNumNull;
    }

    public void setIntNumNull(Integer intNumNull) {
        this.intNumNull = intNumNull;
    }

    public LocalDate getLocalDate1() {
        return localDate1;
    }

    public void setLocalDate1(LocalDate localDate1) {
        this.localDate1 = localDate1;
    }

    public LocalDate getLocalDate2() {
        return localDate2;
    }

    public void setLocalDate2(LocalDate localDate2) {
        this.localDate2 = localDate2;
    }

    public LocalDate getLocalDate3() {
        return localDate3;
    }

    public void setLocalDate3(LocalDate localDate3) {
        this.localDate3 = localDate3;
    }

    public LocalDate getLocalDate4() {
        return localDate4;
    }

    public void setLocalDate4(LocalDate localDate4) {
        this.localDate4 = localDate4;
    }

    public LocalDate getLocalDate5() {
        return localDate5;
    }

    public void setLocalDate5(LocalDate localDate5) {
        this.localDate5 = localDate5;
    }

    public LocalDate getLocalDate8() {
        return localDate8;
    }

    public void setLocalDate8(LocalDate localDate8) {
        this.localDate8 = localDate8;
    }

    public LocalDate getLocalDate6() {
        return localDate6;
    }

    public void setLocalDate6(LocalDate localDate6) {
        this.localDate6 = localDate6;
    }

    public LocalDate getLocalDate7() {
        return localDate7;
    }

    public void setLocalDate7(LocalDate localDate7) {
        this.localDate7 = localDate7;
    }

    public Map<Integer, String> getIntKeyMap() {
        return intKeyMap;
    }

    public void setIntKeyMap(Map<Integer, String> intKeyMap) {
        this.intKeyMap = intKeyMap;
    }

    public Map<Integer, String> getStringKeyMap() {
        return stringKeyMap;
    }

    public void setStringKeyMap(Map<Integer, String> stringKeyMap) {
        this.stringKeyMap = stringKeyMap;
    }

    public Map<Integer, String> getIntKeyMap2() {
        return intKeyMap2;
    }

    public void setIntKeyMap2(Map<Integer, String> intKeyMap2) {
        this.intKeyMap2 = intKeyMap2;
    }

    public Map<Integer, String> getIntKeyMap3() {
        return intKeyMap3;
    }

    public void setIntKeyMap3(Map<Integer, String> intKeyMap3) {
        this.intKeyMap3 = intKeyMap3;
    }

    public Map<Integer, String> getNoMatchMap() {
        return noMatchMap;
    }

    public void setNoMatchMap(Map<Integer, String> noMatchMap) {
        this.noMatchMap = noMatchMap;
    }

    @Override
    public String toString() {
        return "Model{" +
                "lineNum=" + lineNum +
                ", text1='" + text1 + '\'' +
                ", text2='" + text2 + '\'' +
                ", text3='" + text3 + '\'' +
                ", intNum=" + intNum +
                ", longNum=" + longNum +
                ", floatNum=" + floatNum +
                ", doubleNum=" + doubleNum +
                ", bigDecimal=" + bigDecimal +
                ", intNumNull=" + intNumNull +
                ", localDate1=" + localDate1 +
                ", localDate2=" + localDate2 +
                ", localDate3=" + localDate3 +
                ", localDate4=" + localDate4 +
                ", localDate5=" + localDate5 +
                ", localDate8=" + localDate8 +
                ", localDate6=" + localDate6 +
                ", localDate7=" + localDate7 +
                ", intKeyMap=" + intKeyMap +
                ", stringKeyMap=" + stringKeyMap +
                ", intKeyMap2=" + intKeyMap2 +
                ", intKeyMap3=" + intKeyMap3 +
                ", noMatchMap=" + noMatchMap +
                '}';
    }
}
