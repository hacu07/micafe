package com.hacu.micafe.Utilidades;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroFincaFragment;
import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Fragments.DetalleOfertaRecolectorFragment;

import java.io.Serializable;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.content.Context.LOCATION_SERVICE;


public class MapaFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnPoiClickListener {
    public static final int COD_LOCATION = 100;//CODIGO DE PERMISO DE ACCESO A UBICACION
    private View vista;
    private GoogleMap gMap;
    private MapView mapView; //Captura el layout
    //Action Button
    private FloatingActionButton fab;
    private Button btnVolver;

    private LocationManager locationManager;
    private Location currentLocation;
    private Marker marker;
    private CameraPosition cameraZoom;


    JsonObjectRequest jsonObjectRequest;



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
        instanciarElementos(vista);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicionActual();
            }
        });

        return vista;
    }

    private void instanciarElementos(View vista) {
        btnVolver = vista.findViewById(R.id.mapa_btnvolver);
    }

    /**
     * Segun el parametro obtenido ejecuta la instruccion en el switch
     * Los parametros son enviados desde los distintos fragments de la app
     */
    private void obtenerParametrosArguments(Bundle arguments) {
        String opcion = getArguments().getString("opcion");

        if (opcion!=null){
            switch (opcion){
                case "registrarFinca":
                    devolverPosicionFinca(arguments.getSerializable("finca"));
                    break;
                case "ubicacionOfertaRecolector":
                    if (currentLocation == null){
                        imprimirMensaje("Debe activar la ubicacion GPS para ver la ruta hacia la finca de la oferta.");
                    }else{
                        posicionFincaOfertaRecolector(arguments.getInt("idOferta"),arguments.getString("nombreFinca"),
                                arguments.getDouble("latitud"),arguments.getDouble("longitud"));
                    }

                    break;
            }
        }


    }

    private void posicionFincaOfertaRecolector(final int idOferta, final String nombreFinca, final double latitud, final double longitud) {
        //crea el marcador
        if (latitud == 0.0 || longitud == 0.0){
            imprimirMensaje("Coordenadas no encontradas");
        }else {
            //gMap es null por eso arroja error
            if (gMap!=null) {
                Marker markerFinca = gMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)));
                markerFinca.setTitle(nombreFinca);
                markerFinca.setSnippet("Finca de la oferta " + idOferta);
                zoomToLocationLatLon(latitud, longitud);

                //Linea recta hasta la finca de la oferta
                Polyline polyline = gMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(
                                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),//inicio
                                new LatLng(latitud, longitud))
                        .color(Color.BLACK));//destino

                try{
                    //VER VIDEO DE CRISTIAN HENAO! SE VOLVIO A UNA VERSION ANTERIOR POR LO QUE SE HA ELIMINADO ESTOS PROCESOS
                    //webServiceObtenerRuta(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()),String.valueOf(latitud),String.valueOf(longitud));
                    //dibujarRuta();
                }catch (Exception e){
                    imprimirMensaje("Actualmente no se puede dibujar la ruta");
                    e.printStackTrace();
                }


            }
        }
        btnVolver.setVisibility(View.VISIBLE);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment detOfeRecFrag = new DetalleOfertaRecolectorFragment();
                Bundle bundle = new Bundle();
                bundle.putString("nombreFinca",nombreFinca);
                bundle.putInt("idOferta",idOferta);
                detOfeRecFrag.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_recolector,detOfeRecFrag).commit();
            }
        });
    }

    private void devolverPosicionFinca(final Serializable finca) {
        btnVolver.setVisibility(View.VISIBLE);//Muestra el Boton de volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation!=null){//Si se ha establecido una posicion con el marcador
                    Fragment regFinFrag = new RegistroFincaFragment();
                    //asigna los parametros para que sean capturados por el registro
                    Bundle bundle = new Bundle();
                    Finca objFinca = (Finca) finca;//Instancia un objeto de Finca desde el Serializable
                    objFinca.setLatitud(currentLocation.getLatitude());//Agrega los campos de lat y lon
                    objFinca.setLongitud(currentLocation.getLongitude());
                    bundle.putSerializable("finca",objFinca);
                    regFinFrag.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_caficultor, regFinFrag).commit();
                }else{
                    imprimirMensaje("No se ha establecido ubicación GPS de la finca");
                }
            }
        });
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
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.setTrafficEnabled(true);//Habilita la opcion de ver como se encuentra el trafico en la ZONA
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);


        //Escucha de clic en los puntos de interes
        gMap.setOnPoiClickListener(this);

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

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//Obtiene la ubicacion del GPS del dispositivo
            if (location == null){ //Si no funciona el proveedor de gps
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//Obtiene la ubicacion del GPS de la red/wifi
            }
            currentLocation = location;

        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, LOCATION_HARDWARE}, COD_LOCATION);
        }

        obtenerPosicionActual();



        if (getArguments()!=null){
            obtenerParametrosArguments(getArguments());
        }

    }

    private void obtenerPosicionActual() {
        try{
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
        }catch (Exception e){

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
            marker.setSnippet("Gracias por usar MiCafé");
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
        gMap.setMinZoomPreference(6.0f);
        gMap.setMaxZoomPreference(18.0f);
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraZoom));
    }


    private void zoomToLocationLatLon(double latitude, double longitude) {
        cameraZoom = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15) //limit --> 21
                .bearing(0) // 0 --> 365º
                .tilt(30)   //limit 90
                .build();
        gMap.setMinZoomPreference(6.0f);
        gMap.setMaxZoomPreference(18.0f);
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

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        imprimirMensaje(pointOfInterest.name);
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }


}
