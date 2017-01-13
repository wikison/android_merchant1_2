package com.zemult.merchant.bean;

import java.io.Serializable;
import java.util.List;

public class ExtraServerModel implements Serializable {

    //name
    private String name;

    //bg drawable
    private int icon;

    //bg color
    private int background;

    //tip
    private String tip;

    private int type;

    //font color of name
    private int nameColor;

    public ExtraServerModel(String name, int icon, int background) {
        this.name = name;
        this.icon = icon;
        this.background = background;
    }

    public ExtraServerModel(String name, int icon, int background, int nameColor) {
        this(name, icon, background);
        this.nameColor = nameColor;
    }

    public ExtraServerModel(String name, String tip, int icon, int background) {
        this(name, icon, background);
        this.tip = tip;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getNameColor() {
        return nameColor;
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
    }

    public static final ExtraServerModel getByName(
            List<ExtraServerModel> models, String typeName) {
        for (ExtraServerModel model : models) {
            if (typeName.equals(model.getName())) {
                return model;
            }
        }
        return null;
    }

}
