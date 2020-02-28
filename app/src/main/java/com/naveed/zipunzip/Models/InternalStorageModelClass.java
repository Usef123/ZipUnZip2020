package com.naveed.zipunzip.Models;

import android.graphics.drawable.Drawable;

public class InternalStorageModelClass {
    String name;
    String location;
    String type;
    Drawable drawable;

    public InternalStorageModelClass(String name, String location, String type , Drawable drawable) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.drawable =drawable;

    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public InternalStorageModelClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
