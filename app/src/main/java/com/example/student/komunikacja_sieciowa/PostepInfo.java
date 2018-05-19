package com.example.student.komunikacja_sieciowa;


import android.os.Parcel;
import android.os.Parcelable;

public class PostepInfo implements Parcelable {
    public int mPobranychBajtow;
    public int mRozmiar;
    public int mWynik;

    public PostepInfo() {
        mPobranychBajtow=-1;
        mWynik = -1;
    }

    public PostepInfo(Parcel paczka){
        mPobranychBajtow = paczka.readInt();
        mRozmiar = paczka.readInt();
        mWynik = paczka.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPobranychBajtow);
        dest.writeInt(mRozmiar);
        dest.writeInt(mWynik);
    }

    public static final Parcelable.Creator<PostepInfo> CREATOR = new Parcelable.Creator<PostepInfo>(){
        @Override
        public PostepInfo createFromParcel(Parcel source) {
            return  new PostepInfo(source);
        }

        @Override
        public PostepInfo[] newArray(int size) {
            return new PostepInfo[size];
        }
    };
}
