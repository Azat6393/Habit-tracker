package com.woynapp.wontto.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Country implements Parcelable {

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
    private String name;
    private String code;
    @SerializedName("dial_code")
    private String dCode;

    public Country(String code, String name, String dCode, String eur) {
        this.code = code;
        this.name = name;
        this.dCode = dCode;
    }

    private Country(Parcel in) {
        name = in.readString();
        code = in.readString();
        dCode = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDCode() {
        return dCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(dCode);
    }}