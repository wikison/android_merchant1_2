package com.zemult.merchant.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunfusheng on 16/4/23.
 */
public class FilterData implements Serializable {
    private List<FilterEntity> sorts;
    private List<FilterEntity> type;
    private List<FilterEntity> sex;
    private List<FilterEntity> friend;

    public List<FilterEntity> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterEntity> sorts) {
        this.sorts = sorts;
    }

    public List<FilterEntity> getType() {
        return type;
    }

    public void setType(List<FilterEntity> type) {
        this.type = type;
    }

    public List<FilterEntity> getSex() {
        return sex;
    }

    public void setSex(List<FilterEntity> sex) {
        this.sex = sex;
    }

    public List<FilterEntity> getFriend() {
        return friend;
    }

    public void setFriend(List<FilterEntity> friend) {
        this.friend = friend;
    }
}
