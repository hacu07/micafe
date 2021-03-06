package com.hacu.micafe.Caficultor.FragmentsCaf;

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
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetalleOfertaCafFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetalleOfertaCafFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleOfertaCafFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView idOferta, nombreFinca, fechaInicio, modoPago, valorPago, vacantes, diasTrabajo, planta, servicios;

    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progreso;

    public DetalleOfertaCafFragment() {
        // Required empty public constructor
    }


    public static DetalleOfertaCafFragment newInstance(String param1, String param2) {
        DetalleOfertaCafFragment fragment = new DetalleOfertaCafFragment();
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
        View vista = inflater.inflate(R.layout.fragment_detalle_oferta_caf, container, false);
        instanciarElementos(vista);
        final int id = getArguments() != null ? getArguments().getInt("idOferta") : 0;
        final String nombreFinca = getArguments() != null ? getArguments().getString("nombreFinca") : "---";
        consultarDetalleOferta(id,nombreFinca);

        Button btnPostulados = vista.findViewById(R.id.detOfecaf_btnpostulados);
        Button btnAceptados = vista.findViewById(R.id.detOfecaf_btnaceptados);
        Button btnCostos =  vista.findViewById(R.id.detofecaf_btncosto);
        Button btnResporte = vista.findViewById(R.id.detofecaf_btnreportes);

        btnPostulados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametrosEnvio = new Bundle();
                parametrosEnvio.putInt("idOferta",id);
                parametrosEnvio.putString("nombreFinca",nombreFinca);

                Fragment fragPostulados = new PostuladosOfertasCafFragment();
                fragPostulados.setArguments(parametrosEnvio);
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragPostulados).commit();
            }
        });

        btnAceptados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametrosEnvio = new Bundle();
                parametrosEnvio.putInt("idOferta",id);
                parametrosEnvio.putString("nombreFinca",nombreFinca);

                Fragment fragPostulados = new AceptadosOfertaCafFragment();
                fragPostulados.setArguments(parametrosEnvio);
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragPostulados).commit();
            }
        });

        btnCostos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametrosEnvio = new Bundle();
                parametrosEnvio.putInt("idOferta",id);
                parametrosEnvio.putString("nombreFinca",nombreFinca);

                Fragment fragCostos = new CostosOfertaFragment();
                fragCostos.setArguments(parametrosEnvio);
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragCostos).commit();
            }
        });

        btnResporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle parametrosEnvio = new Bundle();
                parametrosEnvio.putInt("idOferta",id);
                parametrosEnvio.putString("nombreFinca",nombreFinca);

                Fragment fragCostos = new ReportesOfertasCaficultorFragment();
                fragCostos.setArguments(parametrosEnvio);
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragCostos).commit();
            }
        });

        return vista;
    }

    private void instanciarElementos(View vista) {
        idOferta = vista.findViewById(R.id.detOfecaf_idoferta);
        nombreFinca = vista.findViewById(R.id.detOfecaf_nombrefinca);
        fechaInicio = vista.findViewById(R.id.detOfecaf_fechainicio);
        modoPago = vista.findViewById(R.id.detOfecaf_modopago);
        valorPago = vista.findViewById(R.id.detOfecaf_valorpago);
        vacantes = vista.findViewById(R.id.detOfecaf_vacantes);
        diasTrabajo = vista.findViewById(R.id.detOfecaf_diastrabajo);
        planta = vista.findViewById(R.id.detOfecaf_planta);
        servicios = vista.findViewById(R.id.detOfecaf_servicios);
    }

    private void consultarDetalleOferta(final int id, final String finca) {
        try {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Consultando detalle de la oferta");
            progreso.show();
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=cafconsultardetalleoferta&idoferta=" + id;
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    JSONArray json = response.optJSONArray("micafe");
                    try {
                        JSONObject jsonObject = json.getJSONObject(0);
                        //asignar respuestas a elementos de la vista
                        idOferta.setText(String.valueOf(id));
                        nombreFinca.setText(finca);
                        fechaInicio.setText(jsonObject.optString("fechainicio"));
                        modoPago.setText(jsonObject.optString("modopago"));
                        valorPago.setText(String.valueOf(jsonObject.optInt("valorpago")));
                        vacantes.setText(String.valueOf(jsonObject.optInt("vacantes")));
                        diasTrabajo.setText(String.valueOf(jsonObject.optInt("diastrabajo")));
                        planta.setText(jsonObject.optString("planta"));
                        servicios.setText(jsonObject.optString("servicios"));
                    } catch (JSONException e) {
                        imprimirMensaje("Error en respuesta oferta detalle - caficultor");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    imprimirMensaje("Error webservice detalle oferta - caficultor");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Error consultando detalle de la oferta\n"+e.toString());
        }
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
