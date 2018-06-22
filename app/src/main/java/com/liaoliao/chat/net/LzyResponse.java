package com.liaoliao.chat.net;

import java.io.Serializable;
import java.util.List;

public class LzyResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public String code = "";
    public String message = "";
    public Object model;
    public String token = "";
    public String randomKey = "";
    public String status = "";
    public String timestamp = "";
    public String error = "";
    public String exception = "";
    public String path = "";
    public String userType = "";
    public  String sign= "" ;
}