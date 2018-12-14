package com.hacu.micafe.Recolector.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetalleTrabajoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView idOferta,fechaInicio, modoPago, valorPago, vacantes, diasTrabajo, planta, servicios, nombreFinca, nombreAdmin,
            telefono, departamento, municipio, corregimiento, vereda;

    private JsonObjectRequest jsonObjectRequest;

    public DetalleTrabajoFragment() {
        // Required empty public constructor
    }


    public static DetalleTrabajoFragment newInstance(String param1, String param2) {
        DetalleTrabajoFragment fragment = new DetalleTrabajoFragment();
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
        View vista = inflater.inflate(R.layout.fragment_detalle_trabajo, container, false);
        final int idOferta = getArguments() != null ?getArguments().getInt("idOferta"): 0 ;
        String nombreFinca = getArguments() != null ? getArguments().getString("nombreFinca") : "---";
        Button btnVolver = vista.findViewById(R.id.dettrarec_btnvolver);
        Button btnPesadas = vista.findViewById(R.id.dettrarec_btnpesadas);

        instanciarElementos(vista,idOferta);
        consultarDetalleTrabajo(idOferta);

        btnPesadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFragmentListaPesadas(idOferta);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFragmentTrabajos();
            }
        });

        return vista;
    }

    private void mostrarFragmentListaPesadas(int idOferta) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta",idOferta);
        Fragment listaPesadasFragment =  new ListaPesadasFragment();
        listaPesadasFragment.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_recolector,listaPesadasFragment).commit();
    }

    private void mostrarFragmentTrabajos() {
        getFragmentManager().beginTransaction().replace(R.id.content_recolector,new ListaTrabajoFragment()).commit();
    }

    private void consultarDetalleTrabajo(int idOferta) {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando información...");
            progreso.show();
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultardetalletrabajorecolector&idoferta=" + idOferta;
            Log.i("TAG", url);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    Usuarios usuario = null;
                    JSONArray json = response.optJSONArray("micafe");

                    try {
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(0);

                        //Asignar Valores a Objeto para enviar por bundle
                        fechaInicio.setText(jsonObject.optString("fechainicio"));
                        modoPago.setText(jsonObject.optString("modo"));
                        valorPago.setText(String.valueOf(jsonObject.optInt("valorpago")));
                        vacantes.setText(String.valueOf(jsonObject.optInt("vacantes")));
                        diasTrabajo.setText(String.valueOf(jsonObject.optInt("diastrabajo")));
                        planta.setText(jsonObject.optString("planta"));
                        servicios.setText(jsonObject.optString("servicios"));
                        nombreFinca.setText(jsonObject.optString("nombrefinca"));
                        nombreAdmin.setText(jsonObject.optString("nombreadmin"));
                        telefono.setText(jsonObject.optString("telefono"));
                        departamento.setText(jsonObject.optString("departamento"));
                        municipio.setText(jsonObject.optString("municipio"));
                        corregimiento.setText(jsonObject.optString("corregimiento"));
                        vereda.setText(jsonObject.optString("vereda"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        imprimirMensaje("Error al cargar detalle de Oferta.");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    imprimirMensaje("Error webservice detalle oferta");
                }
            });
            //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
            //request.add(stringRequest);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Error en detalle trabajo - Recolector");
        }
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private void instanciarElementos(View vista, int idOferta) {
        this.idOferta = vista.findViewById(R.id.dettrarec_id);
        this.idOferta.setText(String.valueOf(idOferta));
        fechaInicio = vista.findViewById(R.id.dettrarec_fechainicio);
        modoPago = vista.findViewById(R.id.dettrarec_modopago);
        valorPago = vista.findViewById(R.id.dettrarec_valorpago);
        vacantes = vista.findViewById(R.id.dettrarec_vacantes);
        diasTrabajo = vista.findViewById(R.id.dettrarec_diastrabajo);
        planta = vista.findViewById(R.id.dettrarec_planta);
        servicios = vista.findViewById(R.id.dettrarec_servicios);
        nombreFinca = vista.findViewById(R.id.dettrarec_nombrefinca);
        nombreAdmin = vista.findViewById(R.id.dettrarec_nombreadmin);
        telefono = vista.findViewById(R.id.dettrarec_telefono);
        departamento = vista.findViewById(R.id.dettrarec_departamento);
        municipio = vista.findViewById(R.id.dettrarec_municipio);
        corregimiento = vista.findViewById(R.id.dettrarec_corregimiento);
        vereda = vista.findViewById(R.id.dettrarec_vereda);

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
