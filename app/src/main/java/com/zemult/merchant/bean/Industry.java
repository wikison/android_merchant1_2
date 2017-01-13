package com.zemult.merchant.bean;

import java.util.List;

/**
 * Created by admin on 2016/4/1.
 */
public class Industry {

    /**
     * status : 1
     * info : ok
     * industrys : [{"id":1,"name":"餐饮","icon":"x.jpg"},{"id":4,"name":"旅游","icon":"x.jpg"}]
     */

    private int status;
    private String info;
    /**
     * id : 1
     * name : 餐饮
     * icon : x.jpg
     */

    private List<IndustrysEntity> industrys;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<IndustrysEntity> getIndustrys() {
        return industrys;
    }

    public void setIndustrys(List<IndustrysEntity> industrys) {
        this.industrys = industrys;
    }

    public static class IndustrysEntity {
        private int id;
        private String name;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}

