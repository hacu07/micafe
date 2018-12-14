package com.hacu.micafe.Recolector.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoPesadasAdapter;
import com.hacu.micafe.Modelo.Pesada;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListaPesadasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerPesadas;
    private ArrayList<Pesada> listaPesadas;
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    private ListadoPesadasAdapter adapter;

    public ListaPesadasFragment() {
        // Required empty public constructor
    }


    public static ListaPesadasFragment newInstance(String param1, String param2) {
        ListaPesadasFragment fragment = new ListaPesadasFragment();
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
        View vista = inflater.inflate(R.layout.fragment_lista_pesadas, container, false);
        final int idOferta = getArguments() != null? getArguments().getInt("idOferta") : 0;
        Button btnVolver = vista.findViewById(R.id.lispesrec_btnvolver);
        instanciarElementos(vista);
        consultarListadoPesadas(idOferta,getSession().getInt("id",0));
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFragmentDetalleTrabajo(idOferta);
            }
        });
        return vista;
    }



    private void mostrarFragmentDetalleTrabajo(int idOferta) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta",idOferta);
        Fragment fragmentDetalleTrabajo = new DetalleTrabajoFragment();
        fragmentDetalleTrabajo.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_recolector,fragmentDetalleTrabajo).commit();
    }


    private void consultarListadoPesadas(int idOferta, int idRecolector) {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando listado de pesadas");
            progreso.show();
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultarlistadopesadasmodulorecolector&idoferta=" + idOferta + "&idrecolector=" + idRecolector;
            Log.i("url", url);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    JSONArray json = response.optJSONArray("micafe");
                    Pesada pesada = null;
                    try {
                        for (int i = 0; i < json.length(); i++) {
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
                    progreso.dismiss();
                    imprimirMensaje("No se encontro listado de pesadas para este recolector - NO CONEXION");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Error consultando el listado de las pesadas - Recolector");
        }
    }

    private void instanciarElementos(View vista) {
        listaPesadas = new ArrayList<>();
        recyclerPesadas = vista.findViewById(R.id.lispesrec_recycler);
        recyclerPesadas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPesadas.setHasFixedSize(true);
    }

    //Obtiene la informacion del usuario que ha iniciado sesion
    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
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
