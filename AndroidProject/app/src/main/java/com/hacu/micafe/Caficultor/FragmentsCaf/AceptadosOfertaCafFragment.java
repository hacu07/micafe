package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListaAceptadosAdapter;
import com.hacu.micafe.Caficultor.Adaptadores.ListaPostuladosAdapter;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AceptadosOfertaCafFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView idoferta;

    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<Usuarios> listaUsuarios;
    private RecyclerView recyclerView;


    public AceptadosOfertaCafFragment() {
        // Required empty public constructor
    }

    public static AceptadosOfertaCafFragment newInstance(String param1, String param2) {
        AceptadosOfertaCafFragment fragment = new AceptadosOfertaCafFragment();
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
        View vista = inflater.inflate(R.layout.fragment_aceptados_oferta_caf, container, false);
        idoferta = vista.findViewById(R.id.aceofecaf_id);
        listaUsuarios = new ArrayList<>();
        recyclerView = vista.findViewById(R.id.recyclerAceptadosOfertaCaf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        int id = getArguments() != null ? getArguments().getInt("idOferta") : 0;
        idoferta.setText(String.valueOf(id));
        cargarRecyclerAceptados(id);
        return vista;
    }

    private void cargarRecyclerAceptados(int idOferta) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultaraceptadosofertacaficultor&idoferta="+idOferta;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Usuarios usuario = null;
                //Obtiene el JSON de la respuesta
                JSONArray json = response.optJSONArray("micafe");

                //recorre cada elemento del json
                try{
                    for (int i = 0; i<json.length(); i++){
                        usuario = new Usuarios();
                        JSONObject jsonObject =  json.getJSONObject(i);
                        usuario.setCedula(jsonObject.optString("cedula"));
                        usuario.setNombre(jsonObject.optString("nombre"));
                        usuario.setFechanacimiento(jsonObject.optString("fechanacimiento"));
                        usuario.setUrlimagen(jsonObject.optString("urlimagen"));
                        //FALTA AGREGAR EL DE LA CALIFICACION
                        listaUsuarios.add(usuario);
                    }
                    ListaAceptadosAdapter adapter = new ListaAceptadosAdapter(listaUsuarios, getContext(), getString(R.string.ip_servidor));
                    recyclerView.setAdapter(adapter);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String cedula = listaUsuarios.get(recyclerView.getChildAdapterPosition(view)).getCedula();
                            String fechaNacimiento = listaUsuarios.get(recyclerView.getChildAdapterPosition(view)).getFechanacimiento();

                            Bundle parametrosEnvio = new Bundle();
                            parametrosEnvio.putString("cedula",cedula);
                            parametrosEnvio.putString("fechanacimiento",fechaNacimiento);
                            parametrosEnvio.putString("idoferta",idoferta.getText().toString());

                            Fragment detalleAceptadoFragment = new DetalleAceptadoOfertaFragment();
                            detalleAceptadoFragment.setArguments(parametrosEnvio);
                            getFragmentManager().beginTransaction().replace(R.id.content_caficultor,detalleAceptadoFragment).commit();
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                    imprimirMensaje("Error en respuesta - aceptados cafe JSON");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("Error de conexion - aceptados caficultor");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
