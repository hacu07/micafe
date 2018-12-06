package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListaOfertasAdapter;
import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CaficultorOfertaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private JsonObjectRequest jsonObjectRequest;
    RecyclerView recyclerOfertas;
    ArrayList<Oferta> listaOfertas;

    public CaficultorOfertaFragment(){}

    // TODO: Rename and change types and number of parameters
    public static CaficultorOfertaFragment newInstance(String param1, String param2) {
        CaficultorOfertaFragment fragment = new CaficultorOfertaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_caficultor_oferta, container, false);
        Button btnOferta = vista.findViewById(R.id.cafofe_btnOferta);
        listaOfertas = new ArrayList<>();
        recyclerOfertas = vista.findViewById(R.id.ofecaf_recyclerOfertas);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerOfertas.setHasFixedSize(true);
        cargarOfertas();

        btnOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment formularioOfertaFragment = new RegistroOfertaFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,formularioOfertaFragment).commit();
            }
        });
        return vista;
    }

    //Web service que obtiene las ofertas registradas por el caficultor
    private void cargarOfertas() {
        String id = String.valueOf(getSession().getInt("id",0));//Obtiene el ID de la persona que ha iniciado sesion
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarofertascaficultor&idadministrador="+id;
        Log.i("TAG",url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Oferta oferta =  null;
                //Obtiene el JSON de la respuesta
                JSONArray jsonArray = response.optJSONArray("micafe");

                //Se recorre cada elemento del json
                try{
                    for (int i = 0; i<jsonArray.length(); i++){
                        oferta = new Oferta();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        oferta.setId(jsonObject.optInt("id"));
                        oferta.setServicios(jsonObject.getString("nombre"));
                        oferta.setFechainicio(jsonObject.getString("fechainicio"));//cambiar por fecha de inicio
                        oferta.setVacantes(jsonObject.getInt("vacantes"));
                        listaOfertas.add(oferta);
                    }

                    ListaOfertasAdapter adapter = new ListaOfertasAdapter(listaOfertas);
                    recyclerOfertas.setAdapter(adapter);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Se obtiene el id de la oferta para consultar a detalle en el siguiente fragment
                            int idOfertaSeleccionado = listaOfertas.get(recyclerOfertas.getChildAdapterPosition(view)).getId();
                            String nombreFinca = listaOfertas.get(recyclerOfertas.getChildAdapterPosition(view)).getServicios();
                            imprimirMensaje("Ha seleccionado la oferta #"+idOfertaSeleccionado);
                            Bundle parametrosEnvio = new Bundle();
                            parametrosEnvio.putInt("idOferta",idOfertaSeleccionado);
                            parametrosEnvio.putString("nombreFinca",nombreFinca);

                            Fragment fragDetalleOferta = new DetalleOfertaCafFragment();
                            fragDetalleOferta.setArguments(parametrosEnvio);
                            getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragDetalleOferta).commit();
                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                    imprimirMensaje("Error al cargar listado de ofertas - RESPONSE ");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("Error al cargar listado de ofertas - WEBSERVICE");
                error.printStackTrace();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
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
