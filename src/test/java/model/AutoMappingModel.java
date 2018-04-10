package model;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.databing.title.Sequence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AutoMappingModel extends BaseModel {
    @ReadValueHandler({ToUpperHandler.class})
    @CellType(title = "Text One")
    private String  text1;
    @CellType(title = "Text Two")
    private String  text2;

    @ReadValueHandler({RecordHandler.class})
    @CellType(title = "Text Reorder")
    private String text3;

    @CellType(title = "integer")
    private Integer    intNum;
    @CellType(title = "long")
    private Long       longNum;
    @CellType(title = "float")
    private Float      floatNum;
    @CellType(title = "double")
    private Double     doubleNum;
    @CellType(title = "bigDecimal")
    private BigDecimal bigDecimal;

    @CellType(title = "Number Null")
    private Integer intNumNull;

    @CellType(prefix = "LocalDate null",title = "\\d")
    private List<LocalDate> localDateHasNull;
    @CellType(prefix = "LocalDate", title = "\\d+")
    private List<LocalDate> localDateList;

    @CellType(prefix = "Map", title = "\\d+")
    private Map<Sequence, String> intKeyMap;

    @CellType(prefix = "Map", title = "[A-Z]")
    private Map<Integer, String> stringKeyMap;

    @CellType(prefix = "Map", title = "1", suffix = "(Chinese Name)")
    private Map<Integer, String> intKeyMap2;

    @CellType(prefix = "Map", title = "\\d+", suffix = "(Chinese Name)", exclude = "1")
    private Map<Integer, String> intKeyMap3;

    @CellType(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

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

    public List<LocalDate> getLocalDateHasNull() {
        return localDateHasNull;
    }

    public void setLocalDateHasNull(List<LocalDate> localDateHasNull) {
        this.localDateHasNull = localDateHasNull;
    }

    public List<LocalDate> getLocalDateList() {
        return localDateList;
    }

    public void setLocalDateList(List<LocalDate> localDateList) {
        this.localDateList = localDateList;
    }

    public Map<Sequence, String> getIntKeyMap() {
        return intKeyMap;
    }

    public void setIntKeyMap(Map<Sequence, String> intKeyMap) {
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
        return "AutoMappingModel{" +
                ", text1='" + text1 + '\'' +
                ", text2='" + text2 + '\'' +
                ", text3='" + text3 + '\'' +
                ", intNum=" + intNum +
                ", longNum=" + longNum +
                ", floatNum=" + floatNum +
                ", doubleNum=" + doubleNum +
                ", bigDecimal=" + bigDecimal +
                ", intNumNull=" + intNumNull +
                ", localDateHasNull=" + localDateHasNull +
                ", localDateList=" + localDateList +
                ", intKeyMap=" + intKeyMap +
                ", stringKeyMap=" + stringKeyMap +
                ", intKeyMap2=" + intKeyMap2 +
                ", intKeyMap3=" + intKeyMap3 +
                ", noMatchMap=" + noMatchMap +
                '}';
    }
}
