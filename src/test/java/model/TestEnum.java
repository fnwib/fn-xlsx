package model;

public enum TestEnum {

    One("aa","b"),
    TWO("aa","b");

    private String a;
    private String b;

    TestEnum(String a, String b) {
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
