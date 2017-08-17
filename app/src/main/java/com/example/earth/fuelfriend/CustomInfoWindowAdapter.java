package com.example.earth.fuelfriend;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.example.earth.fuelfriend.Constants.TRANSPORT_CAR;
import static com.example.earth.fuelfriend.GeneralHelper.getTransportIcon;

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

        ImageView ivTransport = (ImageView) view.findViewById(R.id.iv_transport);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        TextView tvSavingStatus = (TextView) view.findViewById(R.id.tv_fuelstate);

        String transport = marker.getTitle();
        transport = transport.substring(transport.indexOf("|") + 1, transport.length()); // Anchoring the Transport method onto the marker title. Such a hack..
        String savingStatus = (transport.equals(TRANSPORT_CAR)) ? "Used" : "Saved";
        
        ivTransport.setImageResource(getTransportIcon(transport));
        tvTitle.setText(marker.getTitle().substring(0, marker.getTitle().indexOf("|"))); // Removing the transport method before displaying title of marker
        tvSubTitle.setText(marker.getSnippet());
        tvSavingStatus.setText(savingStatus);
        return view;
    }
}
