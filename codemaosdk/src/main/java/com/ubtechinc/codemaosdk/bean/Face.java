package com.ubtechinc.codemaosdk.bean;

/**
 * @Deseription 人脸信息
 * @Author tanghongyu
 * @Time 2018/4/12 22:03
 */

public class Face {
    private String id;
    private String name;
    private int age;
    private int gender;
    private int width;
    private int height;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
