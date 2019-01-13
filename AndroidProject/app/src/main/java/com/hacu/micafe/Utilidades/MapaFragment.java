package com.hacu.micafe.Utilidades;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroFincaFragment;
import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Fragments.DetalleOfertaRecolectorFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        obtenerPosicionActual();
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
                Location puntoDestino = new Location("puntoDestino");
                puntoDestino.setLatitude(latitud);
                puntoDestino.setLongitude(longitud);

                float distancia = currentLocation.distanceTo(puntoDestino);

                Marker markerFinca = gMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)));
                markerFinca.setTitle(nombreFinca);
                markerFinca.setSnippet(/*"Finca de la oferta " + idOferta + */"Distancia "+String.valueOf(distancia/1000)+" km");
                markerFinca.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                zoomToLocationLatLon(latitud, longitud);
                Polyline polyline = gMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(
                                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),//inicio
                                new LatLng(latitud, longitud))
                        .color(Color.BLACK));//destino

                try{
                    webServiceObtenerRuta(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()),String.valueOf(latitud),String.valueOf(longitud));
                    dibujarRuta();
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

    private void dibujarRuta() {
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for(int i=0;i<UtilidadesMapa.routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = UtilidadesMapa.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(2);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        gMap.addPolyline(lineOptions);

        LatLng origen = new LatLng(UtilidadesMapa.coordenadas.getLatitudInicial(), UtilidadesMapa.coordenadas.getLongitudInicial());
        gMap.addMarker(new MarkerOptions().position(origen).title("Lat: "+UtilidadesMapa.coordenadas.getLatitudInicial()+" - Long: "+UtilidadesMapa.coordenadas.getLongitudInicial()));

        LatLng destino = new LatLng(UtilidadesMapa.coordenadas.getLatitudFinal(), UtilidadesMapa.coordenadas.getLongitudFinal());
        gMap.addMarker(new MarkerOptions().position(destino).title("Lat: "+UtilidadesMapa.coordenadas.getLatitudFinal()+" - Long: "+UtilidadesMapa.coordenadas.getLongitudFinal()));

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
    }

    private void devolverPosicionFinca(final Serializable finca) {
        btnVolver.setVisibility(View.VISIBLE);//Muestra el Boton de volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker!=null || currentLocation != null){
                    Fragment regFinFrag = new RegistroFincaFragment();
                    //asigna los parametros para que sean capturados por el registro
                    Bundle bundle = new Bundle();
                    Finca objFinca = (Finca) finca;//Instancia un objeto de Finca desde el Serializable
                    if (marker!=null){
                        objFinca.setLatitud(marker.getPosition().latitude);//Agrega los campos de lat y lon
                        objFinca.setLongitud(marker.getPosition().longitude);
                    }else{
                        objFinca.setLatitud(currentLocation.getLatitude());//Agrega los campos de lat y lon
                        objFinca.setLongitud(currentLocation.getLongitude());
                    }
                    bundle.putSerializable("finca",objFinca);
                    regFinFrag.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_caficultor, regFinFrag).commit();
                }else{
                    imprimirMensaje("No se ha establecido ubicación GPS de la finca\nDebe establecer la posicion para registrar la finca.");
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

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

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
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.logoicon1);
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).draggable(true));//Añadimos el marcador cada vez que actualice
            marker.setIcon(bitmap);
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

    /*
    * Dibujar Ruta
    * */

    private void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {

        String llave = "AIzaSyBlCtbljLE8mkuHKpMlxGy9ZAybOYQ9vzc";
        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&key="+llave;
        Log.i("TAG",url);

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            UtilidadesMapa.routes.add(path);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se puede conectar para dibujar ruta");
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    /*
    * Fin de Dibujar Ruta
    * */

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
