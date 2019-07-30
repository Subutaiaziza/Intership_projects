package com.example.taxiapp.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.taxiapp.MainActivity;

public class Phone {
    private int REQUEST_CALL = 1;
    Context context;

    String numberToCall;
    String numberToSend;

    public Phone(Context mcontext) {
        context = mcontext;
    }

    public void sendMessage(String type) {
        numberToSend = type;
    }

    public void call(String contact) {
        numberToCall = contact;
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

        }else
            {
                String dial = "tel:" + numberToCall;
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
    }


}
