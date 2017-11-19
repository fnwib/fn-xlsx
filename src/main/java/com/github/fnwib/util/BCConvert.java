package com.github.fnwib.util;

public class BCConvert {

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     */
    static final char DBC_CHAR_START = 33; // 半角!

    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     */
    static final char DBC_CHAR_END = 126; // 半角~

    /**
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
     */
    static final char SBC_CHAR_START = 65281; // 全角！

    /**
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
     */
    static final char SBC_CHAR_END = 65374; // 全角～

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     */
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    static final char SBC_SPACE = 12288; // 全角空格 12288

    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    static final char DBC_SPACE = ' '; // 半角空格


    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     * </PRE>
     */
    private static String toMultiByte(String src) {
        if (src == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char aCa : ca) {
            if (aCa == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((aCa >= DBC_CHAR_START) && (aCa <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (aCa + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(aCa);
            }
        }
        return buf.toString();
    }

    /**
     * <PRE>
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
     * </PRE>
     */
    public static String toSingleByte(String src) {
        if (src == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char c : ca) {
            if (c >= SBC_CHAR_START && c <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
                buf.append((char) (c - CONVERT_STEP));
            } else if (c == SBC_SPACE) { // 如果是全角空格
                buf.append(DBC_SPACE);
            } else if (c == '\u00A0') { //char 160
                buf.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(Quote.convert(c));
            }
        }
        return buf.toString();
    }

    enum Quote {
        LeftDblQuote('“', '\"'),
        RightDblQuote('”', '\"'),
        LeftSingleQuote('‘', '\''),
        RightSingleQuote('’', '\'');
        private final char zh;
        private final char en;

        Quote(char zh, char en) {
            this.zh = zh;
            this.en = en;
        }

        public static char convert(char c) {
            if (c == LeftDblQuote.getZh()) {
                return LeftDblQuote.getEn();
            } else if (c == RightDblQuote.getZh()) {
                return RightDblQuote.getEn();
            } else if (c == LeftSingleQuote.getZh()) {
                return LeftSingleQuote.getEn();
            } else if (c == RightSingleQuote.getZh()) {
                return RightSingleQuote.getEn();
            } else {
                return c;
            }
        }

        public char getZh() {
            return zh;
        }

        public char getEn() {
            return en;
        }
    }
}
