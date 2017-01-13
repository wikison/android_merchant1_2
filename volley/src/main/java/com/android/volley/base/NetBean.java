//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.volley.base;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class NetBean implements Serializable {
    @Expose
    int s;
    @Expose
    String response;
    @Expose
    String error_info;

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }
//{"response":"null";"error_info":"null";"s":0}
    public String toJsonString(){
        return "{\"response\":\""+ getResponse()+"\",\"error_info\":\""+error_info+"\",\"s\":"+s+"}";
    }

}
