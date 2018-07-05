package com.gglabs.myapplication;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by GG on 29/11/2017.
 */

public class Contact implements Serializable {

    private boolean isSelected = false;

    private int position;
    private String name;
    private String phone;
    private int image;
    private int color;

    public Contact(int position, String name, String phone, @DrawableRes int image, @ColorRes int textColor) {
        this.position = position;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.color = textColor;
    }

    public Contact(String name, String phone, @DrawableRes int image, @ColorRes int textColor) {
        this(-1, name, phone, image, textColor);
    }

    @Override
    public String toString() {
        return "Contact:\n Name: " + name + "\nPhone: " + phone + "\nImageRes: " + image + "\nColorRes: " + color;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
