package com.example.messengertestlayout.Room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messagesItem")
public class TableMessageItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "message")
    String message;

    @ColumnInfo(name = "isSender")
    Boolean isSender;

    @ColumnInfo(name = "timeStamp")
    Long timeStamp;

    public TableMessageItem(int id, String message, Boolean isSender, Long timeStamp) {
        this.id = id;
        this.message = message;
        this.isSender = isSender;
        this.timeStamp = timeStamp;
    }

    protected TableMessageItem(Parcel in) {
        id = in.readInt();
        message = in.readString();
        byte tmpIsSender = in.readByte();
        isSender = tmpIsSender == 0 ? null : tmpIsSender == 1;
        if (in.readByte() == 0) {
            timeStamp = null;
        } else {
            timeStamp = in.readLong();
        }
    }

    public static final Creator<TableMessageItem> CREATOR = new Creator<TableMessageItem>() {
        @Override
        public TableMessageItem createFromParcel(Parcel in) {
            return new TableMessageItem(in);
        }

        @Override
        public TableMessageItem[] newArray(int size) {
            return new TableMessageItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(message);
        dest.writeByte((byte) (isSender == null ? 0 : isSender ? 1 : 2));
        if (timeStamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timeStamp);
        }
    }
}

