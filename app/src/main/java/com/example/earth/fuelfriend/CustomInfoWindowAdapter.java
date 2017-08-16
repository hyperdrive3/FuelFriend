package com.example.earth.fuelfriend;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.example.earth.fuelfriend.GeneralHelper.getIcon;

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

        String transport = marker.getTitle();
        transport = transport.substring(transport.indexOf("|") + 1, transport.length());

        ivTransport.setImageResource(getIcon(transport));
        tvTitle.setText(marker.getTitle().substring(0, marker.getTitle().indexOf("|")));
        tvSubTitle.setText(marker.getSnippet());

        return view;
    }
}
