package com.example.taxiapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng position;
    private String title;
    private String icon;
    private String contact;

    public String getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    private String type;


    public MyItem(double lat, double lon)
    {
        this.position = new LatLng(lat, lon);
    }

    public MyItem(LatLng position, String title, String type, String contact) {
        this.position = position;
        this.title = title;
        this.type = type;
        this.contact = contact;
    }

    @Override
    public LatLng getPosition()
    {
        return position;
    }

  @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return "sms: "+type+"\n phone: "+contact;
    }

    public String getIcon() {
        return icon;
    }
}
