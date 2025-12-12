package com.nm.famlink.models;
public class UserModel {
    public String uid;
    public String phone;
    public String familyId;
    public UserModel() {}
    public UserModel(String uid, String phone, String familyId) {
        this.uid = uid; this.phone = phone; this.familyId = familyId;
    }
}
