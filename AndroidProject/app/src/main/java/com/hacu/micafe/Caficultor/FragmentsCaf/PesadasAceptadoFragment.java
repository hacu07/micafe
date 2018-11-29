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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoPesadasAdapter;
import com.hacu.micafe.Modelo.Pesada;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PesadasAceptadoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText kilos;
    private RecyclerView recyclerPesadas;
    private ArrayList<Pesada> listaPesadas;
    private ProgressDialog progreso;

    private StringRequest stringRequest;
    private JsonObjectRequest jsonObjectRequest;
    private ListadoPesadasAdapter adapter;

    public PesadasAceptadoFragment() {
        // Required empty public constructor
    }

    public static PesadasAceptadoFragment newInstance(String param1, String param2) {
        PesadasAceptadoFragment fragment = new PesadasAceptadoFragment();
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
        View vista = inflater.inflate(R.layout.fragment_pesadas_aceptado, container, false);
        final String idOferta = getArguments() != null ? getArguments().getString("idoferta") : "0";
        final String cedula = getArguments() != null ? getArguments().getString("cedula") : "0";
        listaPesadas = new ArrayList<>();
        instanciarElementos(vista);
        consultarListadoPesadas(idOferta,cedula);

        Button btnRegistrarPesada = vista.findViewById(R.id.pesace_btnregistrarpesada);
        Button btnVolver = vista.findViewById(R.id.pesace_btnvolver);

        btnRegistrarPesada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPesadaRecolector(cedula,idOferta);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volverDetalleAceptado(idOferta,cedula);
            }
        });

        return vista;
    }

    private void volverDetalleAceptado(String idOferta,String cedula) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putString("idoferta", idOferta);
        parametrosEnvio.putString("cedula", cedula);

        Fragment fragAceptados = new DetalleAceptadoOfertaFragment();
        fragAceptados.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragAceptados).commit();

    }

    private void consultarListadoPesadas(String idOferta, String cedula) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarlistadopesadasrecolector&idoferta="+idOferta+"&cedula="+cedula;
        Log.i("url",url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("micafe");
                Pesada pesada = null;
                try {
                    for (int i = 0; i<json.length(); i++){
                        pesada = new Pesada();
                        JSONObject jsonObject = json.getJSONObject(i);
                        pesada.setFecha(jsonObject.getString("fecha"));
                        pesada.setKilos(jsonObject.getInt("kilos"));
                        pesada.setIdoferta(jsonObject.getInt("valorkilo"));//valor del kilo en idoferta
                        pesada.setIdrecolector(jsonObject.getInt("valorpesada"));//valor del kilo en idoferta
                        listaPesadas.add(pesada);
                    }
                    adapter = new ListadoPesadasAdapter(listaPesadas);
                    recyclerPesadas.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    imprimirMensaje("Error catch pesadas");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se encontro listado de pesadas para este recolector - NO CONEXION");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void registrarPesadaRecolector(final String cedula, final String idOferta) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando Pesada...");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Pesada registrada exitosamente.");
                        kilos.setText("");
                        listaPesadas.clear();
                        consultarListadoPesadas(idOferta,cedula);
                        break;
                    case "Error":
                        imprimirMensaje("No se registro la pesada, intente de nuevo. \n" + response);
                        break;
                    default:
                        imprimirMensaje("No se registro la pesada, intente de nuevo. \n" + response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("Error de conexion. No se registro la pesada");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametrosEnviar = new HashMap<>();

                parametrosEnviar.put("opcion","registrarpesadarecolector");
                parametrosEnviar.put("idoferta",idOferta);
                parametrosEnviar.put("cedula",cedula);
                parametrosEnviar.put("kilos",kilos.getText().toString());
                return parametrosEnviar;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private void instanciarElementos(View vista) {
        kilos = vista.findViewById(R.id.pesace_kilos);
        recyclerPesadas = vista.findViewById(R.id.recyclerPesadasAceptado);
        recyclerPesadas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPesadas.setHasFixedSize(true);
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
