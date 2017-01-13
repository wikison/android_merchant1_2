package com.zemult.merchant.activity.city.entity;

public class City {
    private String name;
    private String pinyin;
    private String no;
    private String pno;

    public City() {
    }

    public City(String name, String pinyin, String no) {
        this.name = name;
        this.pinyin = pinyin;
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }
}
