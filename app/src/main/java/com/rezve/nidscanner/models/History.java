package com.rezve.nidscanner.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.rezve.nidscanner.db.DateConverter;

import java.util.Date;

@Entity
public class History implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String details;
    @TypeConverters( DateConverter.class )
    private Date createdAt;

    public History(String name, String details, Date createdAt) {
        this.name = name;
        this.details = details;
        this.createdAt = createdAt;
    }

    public History(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.details = in.readString();
        this.createdAt = new Date(in.readLong());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public Date getCreatedAt() {
        return createdAt;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.details);
        dest.writeLong(this.createdAt.getTime());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
