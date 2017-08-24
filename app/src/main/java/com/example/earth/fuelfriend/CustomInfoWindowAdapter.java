package com.example.earth.fuelfriend;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.GeneralHelper.getTransportColor;

/**
 * Created by EARTH on 6/08/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{


    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        View view = context.getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        RelativeLayout rlBackground = (RelativeLayout) view.findViewById(R.id.rl_info_background);
        //ImageView ivTransport = (ImageView) view.findViewById(R.id.iv_transport);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        TextView tvSavingStatus = (TextView) view.findViewById(R.id.tv_fuelstate);

        String transport = marker.getTitle();
        transport = transport.substring(transport.indexOf("|") + 1, transport.length()); // Anchoring the Transport method onto the marker title. Such a hack..
        String savingStatus = (transport.equals(TRANSPORT_CAR)) ? "Used" : "Saved";

        Drawable circle = view.getResources().getDrawable(R.drawable.circle);
        circle.setColorFilter(view.getResources().getColor(R.color.colorBikeLine), PorterDuff.Mode.MULTIPLY);

        GradientDrawable bgDrawable = (GradientDrawable) rlBackground.getBackground();
        bgDrawable.setColor(getTransportColor(transport, context));

        String subtitle = marker.getSnippet();
        /*String vehicleData = subtitle.substring(subtitle.indexOf("|"), subtitle.length() - 1);
        String[] vehicleArray = vehicleData.split(",");*/ //Used for later enhancements, maybe toast info onclick

        try {
            subtitle = subtitle.substring(0, subtitle.indexOf("|"));
        } catch (Exception e) {
            System.err.println(e);
            System.out.println("Not CAR transport");
        }


        //ivTransport.setImageResource(getTransportIcon(transport));
        tvTitle.setText(marker.getTitle().substring(0, marker.getTitle().indexOf("|"))); // Removing the transport method before displaying title of marker
        tvSubTitle.setText(subtitle);
        tvSavingStatus.setText(savingStatus);
        //Changes the colour
        //tvSavingStatus.setTextColor(savingStatusColour(savingStatus, view));
        return view;
    }


    private int savingStatusColour(String status, View view) {
        return status.equals("Used") ? view.getResources().getColor(R.color.colorCarLine) : view.getResources().getColor(R.color.colorBikeLine);
    }

}
