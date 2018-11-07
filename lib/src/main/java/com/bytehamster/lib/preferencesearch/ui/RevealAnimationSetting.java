package com.bytehamster.lib.preferencesearch.ui;

import android.os.Parcel;
import android.os.Parcelable;

public class RevealAnimationSetting implements Parcelable {
    private int centerX, centerY, width, height, color;

    public RevealAnimationSetting(int centerX, int centerY, int width, int height, int color) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    private RevealAnimationSetting(Parcel in) {
        centerX = in.readInt();
        centerY = in.readInt();
        width = in.readInt();
        height = in.readInt();
        color = in.readInt();
    }

    public static final Creator<RevealAnimationSetting> CREATOR = new Creator<RevealAnimationSetting>() {
        @Override
        public RevealAnimationSetting createFromParcel(Parcel in) {
            return new RevealAnimationSetting(in);
        }

        @Override
        public RevealAnimationSetting[] newArray(int size) {
            return new RevealAnimationSetting[size];
        }
    };

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColor() {
        return color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(centerX);
        dest.writeInt(centerY);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(color);
    }
}
