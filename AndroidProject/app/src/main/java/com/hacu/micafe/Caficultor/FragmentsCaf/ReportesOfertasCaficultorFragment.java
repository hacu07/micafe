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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListaCostosOfertaAdapter;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoPesadasAdapter;
import com.hacu.micafe.Caficultor.Adaptadores.ListadoReportePesadasAdapter;
import com.hacu.micafe.Modelo.Costo;
import com.hacu.micafe.Modelo.Pesada;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ReportesOfertasCaficultorFragment extends Fragment {
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
    private ArrayList<Costo> listaCostos;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progeso;
    private ListaCostosOfertaAdapter adapterCostos;
    private RecyclerView recyclerCostos;
    private TextView totalCostos, totalPesadas;

    public ReportesOfertasCaficultorFragment() {
        // Required empty public constructor
    }


    public static ReportesOfertasCaficultorFragment newInstance(String param1, String param2) {
        ReportesOfertasCaficultorFragment fragment = new ReportesOfertasCaficultorFragment();
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
        View vista = inflater.inflate(R.layout.fragment_reportes_ofertas_caficultor, container, false);
        final int idOferta = getArguments() != null ? getArguments().getInt("idOferta") : 0;
        final String nombreFinca = getArguments() != null ? getArguments().getString("nombreFinca") : "null";
        instanciarElementos(vista);
        consultarCostosOferta(idOferta);
        consultarListadoPesadas(idOferta);
        Button btnVolver = vista.findViewById(R.id.repofecaf_btnvolver);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarFragmentDetalleOferta(idOferta,nombreFinca);
            }
        });
        return vista;
    }

    private void consultarListadoPesadas(int idOferta) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarlistadoreportepesadasrecolector&idoferta="+idOferta;
        Log.i("url",url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("micafe");
                Pesada pesada = null;
                int totalPesada = 0;
                try {
                    for (int i = 0; i<json.length(); i++){
                        pesada = new Pesada();
                        JSONObject jsonObject = json.getJSONObject(i);
                        pesada.setCedula(jsonObject.getString("cedula"));
                        pesada.setNombre(jsonObject.getString("nombre"));
                        pesada.setFecha(jsonObject.getString("fecha"));
                        pesada.setKilos(jsonObject.getInt("kilos"));
                        pesada.setIdoferta(jsonObject.getInt("valorkilo"));//valor del kilo en idoferta
                        pesada.setIdrecolector(jsonObject.getInt("valorpesada"));//valor del kilo en idoferta
                        totalPesada = totalPesada + jsonObject.getInt("valorpesada");
                        listaPesadas.add(pesada);
                    }
                    ListadoReportePesadasAdapter adapter = new ListadoReportePesadasAdapter(listaPesadas);
                    recyclerPesadas.setAdapter(adapter);
                    totalPesadas.setText(String.valueOf(totalPesada));
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

    private void consultarCostosOferta(int idOferta) {

        progeso = new ProgressDialog(getContext());
        progeso.setMessage("Consultando costos de la oferta...");
        progeso.show();

        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarcostosoferta&idoferta="+idOferta;
        Log.i("TAG",url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progeso.hide();
                JSONArray json = response.optJSONArray("micafe");
                Costo costo = null;
                int totalCosto = 0;
                try {
                    for (int i = 0; i<json.length(); i++){
                        costo = new Costo();
                        JSONObject jsonObject = json.getJSONObject(i);
                        costo.setTitulo(jsonObject.getString("titulo"));
                        costo.setValor(jsonObject.getInt("valor"));
                        totalCosto = totalCosto + jsonObject.getInt("valor");
                        costo.setDescripcion(jsonObject.getString("descripcion"));
                        costo.setFecha(jsonObject.getString("fecha"));
                        listaCostos.add(costo);
                    }
                    adapterCostos = new ListaCostosOfertaAdapter(listaCostos);
                    recyclerCostos.setAdapter(adapterCostos);
                    totalCostos.setText(String.valueOf(totalCosto));
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

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private void instanciarElementos(View vista) {
        listaPesadas = new ArrayList<>();
        listaCostos = new ArrayList<>();
        totalCostos = vista.findViewById(R.id.repofecaf_totalcostos);
        totalPesadas = vista.findViewById(R.id.repofecaf_totalpesadas);
        recyclerCostos = vista.findViewById(R.id.recyclercostosreporte);
        recyclerCostos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerCostos.setHasFixedSize(true);
        recyclerPesadas = vista.findViewById(R.id.recyclercostospesadas);
        recyclerPesadas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPesadas.setHasFixedSize(true);
    }

    private void cargarFragmentDetalleOferta(int id, String nombreFinca) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta",id);
        parametrosEnvio.putString("nombreFinca",nombreFinca);

        Fragment fragDetalleOferta = new DetalleOfertaCafFragment();
        fragDetalleOferta.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragDetalleOferta).commit();
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
