package com.example.mytest.bean;

public class UserBean {
    private String userName;
    private int age;

    public UserBean(String name, int age) {
        this.userName = name;
        this.age = age;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
