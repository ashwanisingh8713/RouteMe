package com.route.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UpdatedPoints implements Parcelable {


    double[] points;

    public UpdatedPoints(int size){
        points = new double[size];
    };

    protected UpdatedPoints(Parcel in) {
//        this.newArrowPoints = in.createTypedArrayList(MeBean.CREATOR);
        this.points = in.createDoubleArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(this.points);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpdatedPoints> CREATOR = new Creator<UpdatedPoints>() {
        @Override
        public UpdatedPoints createFromParcel(Parcel in) {
            return new UpdatedPoints(in);
        }

        @Override
        public UpdatedPoints[] newArray(int size) {
            return new UpdatedPoints[size];
        }
    };

    public double[] getPoints() {
        return points;
    }

    public void addPoints(int index, double point) {
        this.points[index] = point;
    }
}
