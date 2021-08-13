package com.myappcompany.rajan.zeta.model;

public class DependentItem {

    private String mName;
    private String mEmail;

    public DependentItem(String name, String email) {
        mName = name;
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}
