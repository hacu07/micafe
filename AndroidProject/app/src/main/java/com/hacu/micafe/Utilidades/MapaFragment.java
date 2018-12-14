package com.hacu.micafe.Utilidades;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hacu.micafe.R;


public class MapaFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap gMap;
    MapView mapView; //captura el layout
    View vista;

    public MapaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_mapa, container, false);
        return vista;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Referencias
        mapView = (MapView) vista.findViewById(R.id.mapa);//Mapa del layout
        if (mapView != null) {
            mapView.onCreate(null);//Crea Manualmente el mapview
            mapView.onResume();//Se llama cuando este fragment esta visible
            mapView.getMapAsync(this);//Implementa la interfaz OnMapReadyCallBack
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
