package com.faceidentification.vikas.v_tracking;

/**
 * Created by vikas on 31/3/18.
 */

public class vehicle {

    private String name,id,password,userRefKey;

    public vehicle() {
    }

    public vehicle(String name, String id, String password, String userRefKey) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.userRefKey = userRefKey;
    }

    public String getUserRefKey() {
        return userRefKey;
    }

    public void setUserRefKey(String userRefKey) {
        this.userRefKey = userRefKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
