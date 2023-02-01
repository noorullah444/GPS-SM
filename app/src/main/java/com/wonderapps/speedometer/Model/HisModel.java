package com.wonderapps.speedometer.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class HisModel implements Parcelable {
    private String name;
    private String time;
    private String distance;
    private String timeStamp;
    private byte[] image;
    private Double longitude;
    private Double latitude;
    private Double currentLat;
    private Double currentLon;
    private ArrayList<GraphModel> coordinatesList;

    public HisModel(String name, String time, String distance, String timeStamp, byte[] image, Double longitude, Double latitude, Double currentLat, Double currentLon, ArrayList<GraphModel> coordinatesList) {
        this.name = name;
        this.time = time;
        this.distance = distance;
        this.timeStamp = timeStamp;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
        this.coordinatesList = coordinatesList;
    }

    protected HisModel(Parcel in) {
        name = in.readString();
        time = in.readString();
        distance = in.readString();
        timeStamp = in.readString();
        image = in.createByteArray();
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            currentLat = null;
        } else {
            currentLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            currentLon = null;
        } else {
            currentLon = in.readDouble();
        }
    }

    public static final Creator<HisModel> CREATOR = new Creator<HisModel>() {
        @Override
        public HisModel createFromParcel(Parcel in) {
            return new HisModel(in);
        }

        @Override
        public HisModel[] newArray(int size) {
            return new HisModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLon() {
        return currentLon;
    }

    public void setCurrentLon(Double currentLon) {
        this.currentLon = currentLon;
    }

    public ArrayList<GraphModel> getCoordinatesList() {
        return coordinatesList;
    }

    public void setCoordinatesList(ArrayList<GraphModel> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(time);
        parcel.writeString(distance);
        parcel.writeString(timeStamp);
        parcel.writeByteArray(image);
        if (longitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(longitude);
        }
        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(latitude);
        }
        if (currentLat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(currentLat);
        }
        if (currentLon == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(currentLon);
        }
    }
}
