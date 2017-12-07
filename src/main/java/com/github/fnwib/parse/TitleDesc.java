package com.github.fnwib.parse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class TitleDesc {
    private final String title;
    private final int    index;
    private       int    seq;

    public TitleDesc(int index) {
        this.title = null;
        this.index = index;
    }

    public TitleDesc(String title, int index) {
        this.title = title;
        this.index = index;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


}
