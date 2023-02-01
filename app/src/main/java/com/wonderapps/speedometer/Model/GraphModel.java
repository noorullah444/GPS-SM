package com.wonderapps.speedometer.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GraphModel implements Parcelable {
    float x;
    float y;

    public GraphModel(float x, float y) {
        this.x = x;
        this.y = y;
    }

    protected GraphModel(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GraphModel> CREATOR = new Creator<GraphModel>() {
        @Override
        public GraphModel createFromParcel(Parcel in) {
            return new GraphModel(in);
        }

        @Override
        public GraphModel[] newArray(int size) {
            return new GraphModel[size];
        }
    };

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
