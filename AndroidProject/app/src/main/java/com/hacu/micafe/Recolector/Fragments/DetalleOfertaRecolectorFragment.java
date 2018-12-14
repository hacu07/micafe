package com.hacu.micafe.Recolector.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetalleOfertaRecolectorFragment extends Fragment {
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
    private StringRequest stringRequest;

    public DetalleOfertaRecolectorFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetalleOfertaRecolectorFragment newInstance(String param1, String param2) {
        DetalleOfertaRecolectorFragment fragment = new DetalleOfertaRecolectorFragment();
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
        View vista = inflater.inflate(R.layout.fragment_detalle_oferta_recolector, container, false);
        instanciarElementos(vista);

        int id = getArguments() != null ? getArguments().getInt("idOferta") : 0;
        String nombreFinca = getArguments() != null ? getArguments().getString("nombreFinca") : "---";
        idOferta.setText(String.valueOf(id));
        consultarDetalleOferta(id);

        Button btnAplicarOferta = vista.findViewById(R.id.detoferec_btnaplicar);
        btnAplicarOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPostulacionRecolector();
            }
        });
        return vista;
    }

    private void registrarPostulacionRecolector() {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Espere un momento...");
            progreso.show();
            String url = getString(R.string.ip_servidor) + "apimicafe.php";
            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progreso.hide();
                    switch (response) {
                        case "Actualizo":
                            imprimirMensaje("Registro de OFERTA completo");
                            Fragment fragmentOfertasRecolector = new OfertasRecolectorFragment();
                            getFragmentManager().beginTransaction().replace(R.id.content_recolector, fragmentOfertasRecolector).commit();
                            break;
                        case "Error":
                            imprimirMensaje("Ya se ha postulado a esta oferta. \n" + response);
                            break;
                        default:
                            imprimirMensaje(response);
                            Log.i("TAG", response);
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.hide();
                    imprimirMensaje("Error en el webservice - REGISTRAR OFERTA");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();

                    parametros.put("opcion", "postularrecolector");
                    parametros.put("idoferta", idOferta.getText().toString());
                    parametros.put("idrecolector", String.valueOf(getSession().getInt("id", 0)));
                    return parametros;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
        }catch (Exception e){
            Log.i("TAG","Error al postular recolector a oferta - Recolector");
        }
    }

    private void consultarDetalleOferta(int id) {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.show();
            progreso.setMessage("Cargando oferta...");
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultardetalleofertarecolector&idoferta=" + id;
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
                        modoPago.setText(obtenerModoPago(jsonObject.optInt("idmodopago")));
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
                    /*
                    telefono.setText("No disponible hasta ser aceptado por el caficultor");
                    nombreAdmin.setText("No disponible hasta ser aceptado por el caficultor");
                    */

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
            Log.i("TAG","Error al cargar detalle de oferta -  Recolector");
        }
    }

    private String obtenerModoPago(int idmodopago) {
        return idmodopago == 1? "Por Kilos": "Jornal";
    }

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }


    private void instanciarElementos(View vista) {
        idOferta = vista.findViewById(R.id.detoferec_id);
        fechaInicio = vista.findViewById(R.id.detoferec_fechainicio);
        modoPago = vista.findViewById(R.id.detoferec_modopago);
        valorPago = vista.findViewById(R.id.detoferec_valorpago);
        vacantes = vista.findViewById(R.id.detoferec_vacantes);
        diasTrabajo = vista.findViewById(R.id.detoferec_diastrabajo);
        planta = vista.findViewById(R.id.detoferec_planta);
        servicios = vista.findViewById(R.id.detoferec_servicios);
        nombreFinca = vista.findViewById(R.id.detoferec_nombrefinca);
        nombreAdmin = vista.findViewById(R.id.detoferec_nombreadmin);
        telefono = vista.findViewById(R.id.detoferec_telefono);
        departamento = vista.findViewById(R.id.detoferec_departamento);
        municipio = vista.findViewById(R.id.detoferec_municipio);
        corregimiento = vista.findViewById(R.id.detoferec_corregimiento);
        vereda = vista.findViewById(R.id.detoferec_vereda);
    }

    private void imprimirMensaje(String mensaje) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
