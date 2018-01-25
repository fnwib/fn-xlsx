package com.github.fnwib.parse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
@Deprecated
@Getter
@ToString
@EqualsAndHashCode
public final class TitleDesc {
    private final String  title;
    private final Integer index;
    private       Integer seq;

    public TitleDesc(Integer index) {
        this(null, index);
    }

    public TitleDesc(String title) {
        this(title, null);
    }

    public TitleDesc(String title, Integer index) {
        this.title = title;
        this.index = index;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


}
