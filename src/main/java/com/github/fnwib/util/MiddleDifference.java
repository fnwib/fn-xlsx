package com.github.fnwib.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiddleDifference {


    public Between getMiddleDifference(List<String> values) {
        if (values.isEmpty() || values.size() == 1) {
            return new Between(0, 0);
        }
        List<Between> betweenList = new ArrayList<>();
        List<String> distinct = values.stream().distinct().collect(Collectors.toList());
        if (distinct.size() ==1){
            return new Between(0,0);
        }
        String first = null;
        for (String curr : distinct) {
            if (first != null) {
                Between between = middleDifference(first, curr);
                betweenList.add(between);
            }
            first = curr;
        }
        int positiveOffset = betweenList.stream().mapToInt(Between::getPositiveOffset).min().getAsInt();
        int flashbackOffSet = betweenList.stream().mapToInt(Between::getFlashbackOffSet).min().getAsInt();
        return new Between(positiveOffset, flashbackOffSet);
    }


    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Between {
        private int positiveOffset;
        private int flashbackOffSet;

    }

    public Between middleDifference(String value, String object) {
        if (value.equals(object)) {
            return null;
        }
        char[] chars = value.toCharArray();
        char[] objectChars = object.toCharArray();
        int positiveOffset = 0;

        for (int i = 0; i < Math.min(chars.length, objectChars.length); i++) {
            if (chars[i] != objectChars[i]) {
                break;
            } else {
                positiveOffset++;
            }
        }
        int flashbackOffSet = 0;
        for (int i = chars.length - 1, j = objectChars.length - 1;
             i >= 0 && j >= 0;
             i--, j--) {
            if (chars[i] != objectChars[j]) {
                break;
            }
            flashbackOffSet++;
        }
        return new Between(positiveOffset, flashbackOffSet);
    }


}
