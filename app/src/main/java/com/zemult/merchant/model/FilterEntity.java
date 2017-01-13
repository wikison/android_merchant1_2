package com.zemult.merchant.model;

import java.io.Serializable;

/**
 * 只能排序用entity
 */
public class FilterEntity implements Serializable {

    private String key;
    private int intValue;
    private boolean isSelected;
    private int mipmap;
    private int mipmapSelected;

    public FilterEntity() {
    }


    public FilterEntity(String key, int intValue) {
        this.key = key;
        this.intValue = intValue;
    }

    public FilterEntity(String key, int intValue, boolean isSelected, int mipmap) {
        this.key = key;
        this.intValue = intValue;
        this.isSelected = isSelected;
        this.mipmap = mipmap;
    }

    public FilterEntity(String key, int intValue, boolean isSelected, int mipmapDefault, int mipmapSelected) {
        this.key = key;
        this.intValue = intValue;
        this.isSelected = isSelected;
        this.mipmap = mipmapDefault;
        this.mipmapSelected = mipmapSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public int getMipmap() {
        return mipmap;
    }

    public void setMipmap(int mipmap) {
        this.mipmap = mipmap;
    }

    public int getMipmapSelected() {
        return mipmapSelected;
    }

    public void setMipmapSelected(int mipmapSelected) {
        this.mipmapSelected = mipmapSelected;
    }
}
