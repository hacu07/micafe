package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoCalificacionPostuladosAdapter;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoPesadasAdapter;
import com.hacu.micafe.Modelo.Comentario;
import com.hacu.micafe.Modelo.Experiencia;
import com.hacu.micafe.Modelo.Pesada;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Adaptadores.ListaExperienciasAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DetallePostuladoOfertaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Comentario> listComentarios;
    private ArrayList<Experiencia> listExperiencias;
    private RecyclerView recyclerComentarios;
    private RecyclerView recyclerExperiencias;
    private ImageView imagen;
    private TextView nombre,correo,cedula,celular,fechaNacimiento,direccion,departamento,municipio;
    private JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;
    private ProgressDialog progreso;

    public DetallePostuladoOfertaFragment() {
        // Required empty public constructor
    }

    public static DetallePostuladoOfertaFragment newInstance(String param1, String param2) {
        DetallePostuladoOfertaFragment fragment = new DetallePostuladoOfertaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_detalle_postulado_oferta, container, false);
        instanciarElementos(vista);
        final String idOferta = getArguments() != null ? getArguments().getString("idoferta") : "0";
        final String cedula = getArguments() != null ? getArguments().getString("cedula") : "0";
        consultarDatosPostulado(cedula);
        consultarCalificaciones(cedula);
        consultarExperiencias(cedula);

        Button btnVolverListaPostulados = vista.findViewById(R.id.detpos_btnVolver);
        Button btnAceptarPostulado = vista.findViewById(R.id.detpos_btnAceptar);

        btnAceptarPostulado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarEstadoPostulado(idOferta,cedula);
            }
        });

        btnVolverListaPostulados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Vuelve al listado de los postulados de la oferta seleccionada anteriormente
                volverListadoPostulados(idOferta);
            }
        });

        return vista;
    }

    private void consultarExperiencias(String cedula) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarexperienciaspostulado&cedulaPostulado="+cedula;
        jsonObjectRequest =  new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Experiencia experiencia = null;
                JSONArray jsonArray = response.optJSONArray("micafe");
                try{
                    for (int i = 0; i< jsonArray.length(); i++){
                        experiencia = new Experiencia();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        experiencia.setEmpresa(jsonObject.optString("empresa"));
                        experiencia.setCargo(jsonObject.getString("cargo"));
                        experiencia.setFunciones(jsonObject.getString("funciones"));
                        experiencia.setTiempo(jsonObject.optInt("tiempo"));
                        experiencia.setSupervisor(jsonObject.getString("supervisor"));
                        experiencia.setContactosupervisor(jsonObject.getString("contactosupervisor"));
                        listExperiencias.add(experiencia);
                    }
                    ListaExperienciasAdapter adapter = new ListaExperienciasAdapter(listExperiencias);
                    recyclerExperiencias.setAdapter(adapter);
                }catch (JSONException e){
                    imprimirMensaje("Error en respuesta de experiencias");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se encontraron experiencias");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void consultarCalificaciones(String cedula) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarcalificacionpostulado&cedularecolector="+cedula;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONArray json = response.optJSONArray("micafe");
                Comentario comentario = null;
                try {
                    for (int i = 0; i<json.length(); i++){
                        comentario = new Comentario();
                        JSONObject jsonObject = json.getJSONObject(i);
                        comentario.setNombreAdmin(jsonObject.getString("nombre"));
                        comentario.setCelular(jsonObject.getString("celular"));
                        comentario.setCalificacion(jsonObject.getInt("calificacion"));//valor del kilo en idoferta
                        comentario.setComentario(jsonObject.getString("comentario"));//valor del kilo en idoferta
                        listComentarios.add(comentario);
                    }
                    ListadoCalificacionPostuladosAdapter adapter = new ListadoCalificacionPostuladosAdapter(listComentarios);
                    recyclerComentarios.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    imprimirMensaje("Error catch comentario");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se encontro listado de comentarios para este recolector - NO CONEXION");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void volverListadoPostulados(String idOferta){
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta", Integer.parseInt(idOferta));

        Fragment fragPostulados = new PostuladosOfertasCafFragment();
        fragPostulados.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragPostulados).commit();

    }

    private void cambiarEstadoPostulado(final String idOferta, final String cedula) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("error",response);
                switch (response){
                    case "Actualizo":
                        volverListadoPostulados(idOferta);
                        imprimirMensaje("Recolector aceptado en la oferta.");
                        break;
                    case "Error":
                        imprimirMensaje("No se registro el recolector, intente de nuevo. \n");
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se logro conectar al servidor - detalle postulado");
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametrosEnvio = new HashMap<>();
                parametrosEnvio.put("opcion","cambiarestadopostulado");
                parametrosEnvio.put("idoferta",idOferta);
                parametrosEnvio.put("cedula",cedula);
                return parametrosEnvio;
            }
        };
        //limita el tiempo de actualizacion a dos segundos
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void consultarDatosPostulado(final String cedulaRecolector) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Consultando información del postulado...");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultardatospostulado&cedulapostulado="+cedulaRecolector;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();
                JSONArray json = response.optJSONArray("micafe");
                try{
                    JSONObject jsonObject = json.getJSONObject(0);

                    cargarImagenPostulado(jsonObject.optString("urlimagen"));
                    nombre.setText(jsonObject.optString("nombre"));
                    cedula.setText(cedulaRecolector);
                    correo.setText(jsonObject.optString("correo"));
                    celular.setText(jsonObject.optString("celular"));
                    fechaNacimiento.setText(jsonObject.optString("fechanacimiento"));
                    direccion.setText(jsonObject.optString("direccion"));
                    departamento.setText(jsonObject.optString("departamento"));
                    municipio.setText(jsonObject.optString("municipio"));
                }catch (JSONException e){
                    imprimirMensaje("No se logro consultar los datos del postulado a la oferta");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("Error de conexion");
                error.printStackTrace();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void cargarImagenPostulado(String urlimagen) {
        if (urlimagen != null){
            //ASIGNACION DE FOTO DE PERFIL CON LIBRERIA GLIDE (IMPORTADA EN APP)
            String url = getString(R.string.ip_servidor)+urlimagen;
            Log.i("url",url);
            Glide.with(getContext()).load(url).into(imagen);

        }
    }

    private void instanciarElementos(View vista) {
        imagen = vista.findViewById(R.id.detpos_img);
        nombre = vista.findViewById(R.id.detpos_nombre);
        correo = vista.findViewById(R.id.detpos_correo);
        cedula = vista.findViewById(R.id.detpos_cedula);
        celular = vista.findViewById(R.id.detpos_celular);
        fechaNacimiento = vista.findViewById(R.id.detpos_fechanacimiento);
        direccion = vista.findViewById(R.id.detpos_direccion);
        departamento = vista.findViewById(R.id.detpos_departamento);
        municipio = vista.findViewById(R.id.detpos_municipio);
        recyclerComentarios = vista.findViewById(R.id.detpos_recyclercalificacion);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerComentarios.setHasFixedSize(true);
        recyclerExperiencias = vista.findViewById(R.id.detpos_recyclerexperiencia);
        recyclerExperiencias.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerExperiencias.setHasFixedSize(true);
        listComentarios = new ArrayList<>();
        listExperiencias = new ArrayList<>();
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
