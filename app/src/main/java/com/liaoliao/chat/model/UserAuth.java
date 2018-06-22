package com.liaoliao.chat.model;

import java.io.Serializable;


import java.io.Serializable;

/**
 * <p>
 * 授权表
 * </p>
 *
 * @author stylefeng123
 * @since 2018-06-17
 */

public class UserAuth implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;

    private String headImageUrl;
    private String token;

    private String userId;
    private String name;
    private String gender;
    private String type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "UserAuth{" +
        "id=" + id +
        ", headImageUrl=" + headImageUrl +
        ", token=" + token +
        ", userId=" + userId +
        ", name=" + name +
        ", gender=" + gender +
        ", type=" + type +
        "}";
    }
}
