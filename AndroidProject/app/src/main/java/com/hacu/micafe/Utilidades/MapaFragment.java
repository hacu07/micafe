package com.hacu.micafe.Utilidades;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hacu.micafe.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.content.Context.LOCATION_SERVICE;


public class MapaFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    public static final int COD_LOCATION = 100;//CODIGO DE PERMISO DE ACCESO A UBICACION
    private View vista;
    private GoogleMap gMap;
    private MapView mapView; //Captura el layout
    //Action Button
    private FloatingActionButton fab;

    private LocationManager locationManager;
    private Location currentLocation;
    private Marker marker;
    private CameraPosition cameraZoom;

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
        FloatingActionButton fab = vista.findViewById(R.id.fab_mapa);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicionActual();
            }
        });
        return vista;
    }

    /*Encargado de mostrar el layout del mapa*/
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
        MapsInitializer.initialize(getContext());
        //Obtenemos al parametro del metodo
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);


        //Escucha de clic en los puntos de interes
        //gMap.setOnPoiClickListener(this);

        //PROBAR PARA PERMISOS
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //gMap.setMyLocationEnabled(true);
            //gMap.getUiSettings().setMyLocationButtonEnabled(false);//Quita el boton cuanto encuentra la location
            /*  Cuando se actualice la señal toma 1 provider - el que se encuentre disponible  */
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER //Proveedor de location
                    , 0   //Minimo de Tiempo de actualizacion - milisegundos (Para tema de bateria consume mucha energia al hacer peticiones constantemente).
                    , 0    //Minimo de distancia en actualizarse - metros
                    , this);//Pide las actualizaciones de la localizacion
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    this);//Pide las actualizaciones de la localizacion

        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, LOCATION_HARDWARE}, COD_LOCATION);
        }
    }

    private void obtenerPosicionActual() {
        //Cuando haga clic en el FloatingActionButton lanzamos el metodo
        //SOLICITA GPS
        if (!this.validarGPS()) {//Si el gps no esta habilitado
            mostrarInformacionAlerta();
        } else {

            //Activar el boton para dar la localizacion
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//Obtiene la ubicacion del GPS del dispositivo
            if (location == null){ //Si no funciona el proveedor de gps
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//Obtiene la ubicacion del GPS de la red/wifi
            }
            currentLocation = location;

            if (currentLocation != null){//Si no es nulo tomamos el marcador si existe y lo actualizamos, si no lo creamos
                createOrUpdateMarkerByLocation(location);
                zoomToLocation(location);
            }
        }
    }


    //Verifica si esta activado el GPS (Ubicacion)
    private boolean validarGPS() {
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            //Si no tiene señal = 0
            if (gpsSignal == 0) {
                //EL GPS NO ESTA ACTIVADO
                return false;
            } else {
                return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == COD_LOCATION) {
            if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // gMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER //Proveedor de location
                        , 0   //Minimo de Tiempo de actualizacion - milisegundos (Para tema de bateria consume mucha energia al hacer peticiones constantemente).
                        , 0    //Minimo de distancia en actualizarse - metros
                        , this);//Pide las actualizaciones de la localizacion
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);//Pide las actualizaciones de la localizacion

            } else {
                Toast.makeText(getContext(), "Permiso no aceptado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Cuadro de dialogo que menciona activar señal de gps
    private void mostrarInformacionAlerta() {
        new AlertDialog.Builder(getContext())
                .setTitle("Señal GPS")
                .setMessage("No tienes señal de GPS. Deseas activar la señal ahora?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent que nos lleva a una ventanada para activar el setting
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createOrUpdateMarkerByLocation(Location location){
        if (marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).draggable(true));//Añadimos el marcador cada vez que actualice
            //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sininternet));
            marker.setTitle("Aquí Estoy!");
            marker.setSnippet("Busca que hay alrededor");
        }else{
            marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        //dibujarRadio(location);
    }

    private void zoomToLocation(Location location){
        cameraZoom = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(15) //limit --> 21
                .bearing(0) // 0 --> 365º
                .tilt(30)   //limit 90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraZoom));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
