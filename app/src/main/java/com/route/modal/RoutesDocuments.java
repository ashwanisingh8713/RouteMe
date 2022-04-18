package com.route.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutesDocuments implements Parcelable {

    public String id;
    public int ver;
    public String part;
    public String city;
    public Double lat;
    public Double longX;
    public String loc;
    public String spt;
    public String ept;
    public String said;
    public String eaid;
    public Boolean pub;
    public String cat;
    public int len;
    public int tu;
    public int td;
    public Double ud;
    public List<Double> pts;
    public String _rid;
    public String _self;
    public String _etag;
    public String _attachments;
    public int _ts;
    public int cc;
    public int lc;
    public int SomethingElse;
    public int Misleading;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutesDocuments)) return false;
        RoutesDocuments that = (RoutesDocuments) o;
        return that.id.equals(id);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.ver);
        dest.writeString(this.part);
        dest.writeString(this.city);
        dest.writeValue(this.lat);
        dest.writeValue(this.longX);
        dest.writeString(this.loc);
        dest.writeString(this.spt);
        dest.writeString(this.ept);
        dest.writeString(this.said);
        dest.writeString(this.eaid);
        dest.writeValue(this.pub);
        dest.writeString(this.cat);
        dest.writeInt(this.len);
        dest.writeInt(this.tu);
        dest.writeInt(this.td);
        dest.writeValue(this.ud);
        dest.writeList(this.pts);
        dest.writeString(this._rid);
        dest.writeString(this._self);
        dest.writeString(this._etag);
        dest.writeString(this._attachments);
        dest.writeInt(this._ts);
        dest.writeInt(this.cc);
        dest.writeInt(this.lc);
        dest.writeInt(this.SomethingElse);
        dest.writeInt(this.Misleading);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.ver = source.readInt();
        this.part = source.readString();
        this.city = source.readString();
        this.lat = (Double) source.readValue(Double.class.getClassLoader());
        this.longX = (Double) source.readValue(Double.class.getClassLoader());
        this.loc = source.readString();
        this.spt = source.readString();
        this.ept = source.readString();
        this.said = source.readString();
        this.eaid = source.readString();
        this.pub = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.cat = source.readString();
        this.len = source.readInt();
        this.tu = source.readInt();
        this.td = source.readInt();
        this.ud = (Double) source.readValue(Double.class.getClassLoader());
        this.pts = new ArrayList<Double>();
        source.readList(this.pts, Double.class.getClassLoader());
        this._rid = source.readString();
        this._self = source.readString();
        this._etag = source.readString();
        this._attachments = source.readString();
        this._ts = source.readInt();
        this.cc = source.readInt();
        this.lc = source.readInt();
        this.SomethingElse = source.readInt();
        this.Misleading = source.readInt();
    }

    public RoutesDocuments() {
    }

    protected RoutesDocuments(Parcel in) {
        this.id = in.readString();
        this.ver = in.readInt();
        this.part = in.readString();
        this.city = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.longX = (Double) in.readValue(Double.class.getClassLoader());
        this.loc = in.readString();
        this.spt = in.readString();
        this.ept = in.readString();
        this.said = in.readString();
        this.eaid = in.readString();
        this.pub = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.cat = in.readString();
        this.len = in.readInt();
        this.tu = in.readInt();
        this.td = in.readInt();
        this.ud = (Double) in.readValue(Double.class.getClassLoader());
        this.pts = new ArrayList<Double>();
        in.readList(this.pts, Double.class.getClassLoader());
        this._rid = in.readString();
        this._self = in.readString();
        this._etag = in.readString();
        this._attachments = in.readString();
        this._ts = in.readInt();
        this.cc = in.readInt();
        this.lc = in.readInt();
        this.SomethingElse = in.readInt();
        this.Misleading = in.readInt();
    }

    public static final Parcelable.Creator<RoutesDocuments> CREATOR = new Parcelable.Creator<RoutesDocuments>() {
        @Override
        public RoutesDocuments createFromParcel(Parcel source) {
            return new RoutesDocuments(source);
        }

        @Override
        public RoutesDocuments[] newArray(int size) {
            return new RoutesDocuments[size];
        }
    };
}
