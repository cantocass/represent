package com.familyfunctional.represent;

import android.os.Parcel;
import android.os.Parcelable;


//@Root(name = "result")
public class Member implements Parcelable {

    String name;
    String party;
    String state;
    String district;
    String phone;
    String office;
    String link;

    public Member() {
    }

    public Member(String name, String party, String state, String district, String phone, String office, String link) {
        this.name = name;
        this.party = party;
        this.state = state;
        this.district = district;
        this.phone = phone;
        this.office = office;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    protected Member(Parcel in) {
        name = in.readString();
        party = in.readString();
        state = in.readString();
        district = in.readString();
        phone = in.readString();
        office = in.readString();
        link = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(party);
        dest.writeString(state);
        dest.writeString(district);
        dest.writeString(phone);
        dest.writeString(office);
        dest.writeString(link);
    }

    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}