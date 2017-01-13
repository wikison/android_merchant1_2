package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_AppInfo implements Serializable {
   @Expose
   public String	version			;	//	最新的app版本号
    @Expose
    public  String	icon			;	//	app小图标地址
    @Expose
    public  String	path			;	//	app下载地址
    @Expose
    public  String	fileSize			;	//	app大小
    @Expose
    public  String	note			;	//	版本更新说明
    @Expose
    public int	isForce			;	//	是否强制更新(0:否,1:是)
    @Expose
    public   String	createtime			;	//	发布日期(格式"yyyy-MM-dd HH:mm:ss")


    public String getVersion() {
     return version;
    }

    public void setVersion(String version) {
     this.version = version;
    }

    public String getIcon() {
     return icon;
    }

    public void setIcon(String icon) {
     this.icon = icon;
    }

    public String getPath() {
     return path;
    }

    public void setPath(String path) {
     this.path = path;
    }

    public String getFileSize() {
     return fileSize;
    }

    public void setFileSize(String fileSize) {
     this.fileSize = fileSize;
    }

    public String getNote() {
     return note;
    }

    public void setNote(String note) {
     this.note = note;
    }

    public int getIsForce() {
     return isForce;
    }

    public void setIsForce(int isForce) {
     this.isForce = isForce;
    }

    public String getCreatetime() {
     return createtime;
    }

    public void setCreatetime(String createtime) {
     this.createtime = createtime;
    }


}
