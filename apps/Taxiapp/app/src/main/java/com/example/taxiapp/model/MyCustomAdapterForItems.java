package com.example.taxiapp.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxiapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
    private int REQUEST_CALL = 1;
    Context context;
    private ClusterManager<MyItem> mClusterManager;

    public MyCustomAdapterForItems(Context cnt, ClusterManager<MyItem> ClusterManager) {
        context = cnt;
        mClusterManager = ClusterManager;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return  null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        final View view = ((Activity)context).getLayoutInflater().inflate(R.layout.infowindow, null);

        TextView title = view.findViewById(R.id.txtTitle);
        title.setText(marker.getTitle());

        TextView infos = view.findViewById(R.id.snipped);
        infos.setText(marker.getSnippet());

        final Phone phone = new Phone(context);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(final MyItem myItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(marker.getTitle());

                final String [] list = new String[]{"Call"};

                builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(i == 1)
                        {
                            Toast.makeText(context, "Calling..."+ myItem.getContact(), Toast.LENGTH_SHORT).show();
                            phone.call(myItem.getContact());
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
        return view;
    }
}
