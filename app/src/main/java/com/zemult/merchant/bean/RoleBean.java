package com.zemult.merchant.bean;

import java.util.List;

/**
 * Created by admin on 2016/4/15.
 */
public class RoleBean {

    /**
     * status : 1
     * info : ok
     * industrys : [{"id":2,"name":"早餐送达员","icon":"http://www.inroids.com/getfile/image/service/2016-03-24/c8e8ef1027bb4ae9b2dd12b8e7850144.jpg","tag":"送货上门","note":"按约定将早餐送到家的人","revenus":200000},{"id":3,"name":"美食管家","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/d3b90903a9ba459d89db5efc0a3afd69.jpg","tag":"饮食,管家","note":"为客户提供全方位餐饮服务的人（非美食不报）","revenus":220000},{"id":4,"name":"西餐达人","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/48d8b35e526b41009a5ff89b953f63b2.jpg","tag":"西餐","note":"为喜欢牛排的人群提供周到服务的人","revenus":160000},{"id":5,"name":"料理达人","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/89ef40d12f2c4131a66eeb0a3cbc024e.jpg","tag":"日韩料理","note":"为喜欢日韩料理的人群提供周到服务的人","revenus":100000},{"id":6,"name":"卤菜品评师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/9762d5e9dd044824a6716cdfdb7bfdfb.jpg","tag":"卤菜,特色","note":"各家卤菜的特色产品","revenus":168000},{"id":7,"name":"饮料品荐师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/2641a639005c4a988501f79d8d5378ac.jpg","tag":"饮料,特色","note":"告知你饮料的甜度，成分，口感","revenus":165000},{"id":8,"name":"创意烘焙师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/675f07ac17e34adaad2f5b043ce0bc35.jpg","tag":"烘焙,点心","note":"懂烘焙，会烘焙，有烘焙资源的人","revenus":150000},{"id":9,"name":"秘制火锅达人","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/5073fb6b322d440f87bb396eefd28a3b.jpg","tag":"火锅,秘制底料","note":"让你停不下筷子的火锅制作人","revenus":155000},{"id":10,"name":"私厨指导师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/18bd31412b8d4708bc32f0e091ebb521.jpg","tag":"私厨,做菜","note":"手把手教做菜的大厨","revenus":86000},{"id":11,"name":"食材选购师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/4378226de3dd415692f6969dd28a5fcc.jpg","tag":"食材,选购","note":"要想买到货真价实的东西，性价比高的食材就得找他","revenus":100000},{"id":12,"name":"营养配餐师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/62e56e4d64484193aa70d556d5ada7f7.jpg","tag":"营养,科学饮食","note":"以营养学为基础，教你科学饮食的人","revenus":80000},{"id":13,"name":"菜品搭配师（点菜师）","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/bcbca7ef081e4693934be7e861117910.jpg","tag":"搭配,营养","note":"点菜有门道","revenus":120000},{"id":14,"name":"食品雕花师","icon":"http://www.inroids.com/getfile/image/service/2016-03-25/8f4e01808f2043138244c4643a3412fe.jpg","tag":"食品雕花","note":"可以将胡萝卜雕成花的人，让你家餐桌增添一抹亮色的人","revenus":90000}]
     */

    private int status;
    private String info;
    /**
     * id : 2
     * name : 早餐送达员
     * icon : http://www.inroids.com/getfile/image/service/2016-03-24/c8e8ef1027bb4ae9b2dd12b8e7850144.jpg
     * tag : 送货上门
     * note : 按约定将早餐送到家的人
     * revenus : 200000
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
        private String tag;
        private String note;
        private int revenus;

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

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getRevenus() {
            return revenus;
        }

        public void setRevenus(int revenus) {
            this.revenus = revenus;
        }
    }
}

