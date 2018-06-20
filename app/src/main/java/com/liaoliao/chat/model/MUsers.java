package com.liaoliao.chat.model;

import java.io.Serializable;

import java.util.Date;


/**
 * <p>
 * 用户表
 * </p>
 *
 * @author stylefeng123
 * @since 2018-06-16
 */

public class MUsers  {

    private static final long serialVersionUID = 1L;

    private String id;

    private String loginName;

    private String userPass;
    /**
     * 用户姓名
     */

    private String userName;
    private String mobile;
    private String token;

    private String createTime;
    private String sex;
    /**
     * 登录时间
     */

    private String loginTime;
    /**
     * 上次登录时间
     */

    private String lastLoginTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }



    @Override
    public String toString() {
        return "MUsers{" +
        "id=" + id +
        ", loginName=" + loginName +
        ", userPass=" + userPass +
        ", userName=" + userName +
        ", mobile=" + mobile +
        ", token=" + token +
        ", createTime=" + createTime +
        ", sex=" + sex +
        ", loginTime=" + loginTime +
        ", lastLoginTime=" + lastLoginTime +
        "}";
    }
}
