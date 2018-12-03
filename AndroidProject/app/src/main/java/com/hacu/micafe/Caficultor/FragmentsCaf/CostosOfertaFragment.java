package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hacu.micafe.Caficultor.Adaptadores.ListaCostosOfertaAdapter;
import com.hacu.micafe.Modelo.Costo;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CostosOfertaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText titulo, valor, descripcion;
    private RecyclerView recyclerCostos;

    private ArrayList<Costo> listaCostos;
    private StringRequest stringRequest;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progeso;
    private ListaCostosOfertaAdapter adapterCostos;

    public CostosOfertaFragment() {
        // Required empty public constructor
    }


    public static CostosOfertaFragment newInstance(String param1, String param2) {
        CostosOfertaFragment fragment = new CostosOfertaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_costos_oferta, container, false);
        instanciarElementos(vista);
        final int idOferta = getArguments() != null ? getArguments().getInt("idOferta") : 0;
        final String nombreFinca = getArguments() != null ? getArguments().getString("nombreFinca") : "null";
        consultarCostosOferta(idOferta);

        //Botones
        Button btnRegistrarCosto = vista.findViewById(R.id.cosofecaf_btncosto);
        Button btnVolver = vista.findViewById(R.id.cosofecaf_btnvolver);

        btnRegistrarCosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarCosto(idOferta);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarFragmentDetalleOferta(idOferta,nombreFinca);
            }
        });

        return vista;
    }

    private void consultarCostosOferta(int idOferta) {

        progeso = new ProgressDialog(getContext());
        progeso.setMessage("Consultando costos de la oferta...");
        progeso.show();

        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarcostosoferta&idoferta="+idOferta;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progeso.hide();
                JSONArray json = response.optJSONArray("micafe");
                Costo costo = null;
                try {
                    for (int i = 0; i<json.length(); i++){
                        costo = new Costo();
                        JSONObject jsonObject = json.getJSONObject(i);
                        costo.setTitulo(jsonObject.getString("titulo"));
                        costo.setValor(jsonObject.getInt("valor"));
                        costo.setDescripcion(jsonObject.getString("descripcion"));
                        costo.setFecha(jsonObject.getString("fecha"));
                        listaCostos.add(costo);
                    }
                    adapterCostos = new ListaCostosOfertaAdapter(listaCostos);
                    recyclerCostos.setAdapter(adapterCostos);
                } catch (JSONException e) {
                    e.printStackTrace();
                    imprimirMensaje("Error catch pesadas");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progeso.hide();
                imprimirMensaje("No se encontro listado de pesadas para este recolector - NO CONEXION");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void cargarFragmentDetalleOferta(int id, String nombreFinca) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta",id);
        parametrosEnvio.putString("nombreFinca",nombreFinca);

        Fragment fragDetalleOferta = new DetalleOfertaCafFragment();
        fragDetalleOferta.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragDetalleOferta).commit();
    }

    private void registrarCosto(final int idOferta) {
        progeso = new ProgressDialog(getContext());
        progeso.setMessage("Registrando costo...");
        progeso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progeso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Costo registrado exitosamente.");
                        limpiarCampos();
                        listaCostos.clear();
                        consultarCostosOferta(idOferta);
                        break;
                    case "Error":
                        imprimirMensaje("No se registro el costo, intente de nuevo. \n" + response);
                        break;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progeso.hide();
                imprimirMensaje("No se registro - error conexion registro costo");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametrosEnvio =  new HashMap<>();
                parametrosEnvio.put("opcion","registrarcostooferta");
                parametrosEnvio.put("idoferta",String.valueOf(idOferta));
                parametrosEnvio.put("titulo",titulo.getText().toString());
                parametrosEnvio.put("valor",valor.getText().toString());
                    parametrosEnvio.put("descripcion",descripcion.getText().toString());
                return  parametrosEnvio;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void limpiarCampos() {
        titulo.setText("");
        valor.setText("");
        descripcion.setText("");
    }

    private void instanciarElementos(View vista) {
        listaCostos = new ArrayList<>();
        titulo = vista.findViewById(R.id.cosofecaf_titulo);
        valor = vista.findViewById(R.id.cosofecaf_valor);
        descripcion = vista.findViewById(R.id.cosofecaf_descripcion);
        recyclerCostos = vista.findViewById(R.id.cosofecaf_recycler);
        recyclerCostos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerCostos.setHasFixedSize(true);
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
