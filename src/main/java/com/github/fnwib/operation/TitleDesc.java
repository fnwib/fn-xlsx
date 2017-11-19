package com.github.fnwib.operation;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class TitleDesc {
    private final String title;
    private final int    index;
    private       int    seq;

    public TitleDesc(String title, int index) {
        this.title = title;
        this.index = index;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


}
