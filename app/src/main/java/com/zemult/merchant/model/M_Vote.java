package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Vote implements Serializable{
    @Expose
   public  int	voteId			;	//	选项id
    @Expose
    public  String	voteNote			;	//	选项内容
    @Expose
    public  int	voteNum			;	//	选项票数
    @Expose
    public String voteDiscount;  //选项所占比例

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean selected;
}
