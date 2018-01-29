package com.github.fnwib.parse;

import com.google.common.base.Objects;

@Deprecated
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

    public String getTitle() {
        return title;
    }

    public Integer getIndex() {
        return index;
    }

    public Integer getSeq() {
        return seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleDesc titleDesc = (TitleDesc) o;
        return Objects.equal(title, titleDesc.title) &&
                Objects.equal(index, titleDesc.index) &&
                Objects.equal(seq, titleDesc.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, index, seq);
    }

    @Override
    public String toString() {
        return "TitleDesc{" +
                "title='" + title + '\'' +
                ", index=" + index +
                ", seq=" + seq +
                '}';
    }
}
