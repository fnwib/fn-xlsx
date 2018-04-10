package model;


import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.ReadValueHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class WriteModel extends BaseModel{
    @AutoMapping("序号")
    private Integer      sequence;
    @AutoMapping("字符串")
    private String       string;
    @AutoMapping("数字Int")
    private Integer      intNum;
    @AutoMapping("数字Long")
    private Long         longNum;
    @AutoMapping("日期")
    private LocalDate    localDate;
    @AutoMapping(value = "MAP \\d+")
    private List<String> listNumber;
    @CellType(title = "MAP [A-Z]")
    private List<String> listString;

    @AutoMapping("MAP [A-Z] null")
    @CellType(title = "MAP [A-Z] null")
    private Map<Integer, String> mapNull;

    @AutoMapping("Excel no match")
    @CellType(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

    @AutoMapping("AAA")
    @ReadValueHandler({ToUpperHandler.class,RecordHandler.class})
    private String aaa;

    @AutoMapping("enumType")
    private EnumType enumType;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
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

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public List<String> getListNumber() {
        return listNumber;
    }

    public void setListNumber(List<String> listNumber) {
        this.listNumber = listNumber;
    }

    public List<String> getListString() {
        return listString;
    }

    public void setListString(List<String> listString) {
        this.listString = listString;
    }

    public Map<Integer, String> getMapNull() {
        return mapNull;
    }

    public void setMapNull(Map<Integer, String> mapNull) {
        this.mapNull = mapNull;
    }

    public Map<Integer, String> getNoMatchMap() {
        return noMatchMap;
    }

    public void setNoMatchMap(Map<Integer, String> noMatchMap) {
        this.noMatchMap = noMatchMap;
    }

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public EnumType getEnumType() {
        return enumType;
    }

    public void setEnumType(EnumType enumType) {
        this.enumType = enumType;
    }



}
