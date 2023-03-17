package com.example.messengertestlayout;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MessagesItem implements Parcelable {
    Integer id;
    String message;
    Long timestamp;
    Boolean isSender;

    public MessagesItem(String message, Long timestamp, Boolean isSender, int id) {
        this.message = message;
        this.timestamp = timestamp;
        this.isSender = isSender;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    protected MessagesItem(Parcel in) {
        message = in.readString();
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
        byte tmpIsSender = in.readByte();
        isSender = tmpIsSender == 0 ? null : tmpIsSender == 1;
    }

    public static final Creator<MessagesItem> CREATOR = new Creator<MessagesItem>() {
        @Override
        public MessagesItem createFromParcel(Parcel in) {
            return new MessagesItem(in);
        }

        @Override
        public MessagesItem[] newArray(int size) {
            return new MessagesItem[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(message);
        if (timestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timestamp);
        }
        dest.writeByte((byte) (isSender == null ? 0 : isSender ? 1 : 2));
    }
}
