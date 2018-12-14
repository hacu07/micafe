package com.hacu.micafe.Recolector.Fragments;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Adaptadores.ListaOfertasRecAdapter;
import com.hacu.micafe.Recolector.Adaptadores.ListaTrabajosRecAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListaTrabajoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private JsonObjectRequest jsonObjectRequest;
    RecyclerView recyclerTrabajo;
    ArrayList<Oferta> listaTrabajo;


    public ListaTrabajoFragment() {
        // Required empty public constructor
    }


    public static ListaTrabajoFragment newInstance(String param1, String param2) {
        ListaTrabajoFragment fragment = new ListaTrabajoFragment();
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
        View vista =  inflater.inflate(R.layout.fragment_lista_trabajo, container, false);
        listaTrabajo = new ArrayList<>();
        recyclerTrabajo = vista.findViewById(R.id.recyclerTrabajoRec);
        recyclerTrabajo.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerTrabajo.setHasFixedSize(true);
        cargarTrabajosRecolector();
        return vista;
    }

    private void cargarTrabajosRecolector() {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando información");
            progreso.show();
            String id = String.valueOf(getSession().getInt("id", 0));//Obtiene el ID de la persona que ha iniciado sesion
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultartrabajosrecolector&idrecolector=" + id;
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    Oferta oferta = null;
                    //Obtiene el JSON de la respuesta
                    JSONArray jsonArray = response.optJSONArray("micafe");

                    //Se recorre cada elemento del json
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            oferta = new Oferta();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            oferta.setId(jsonObject.optInt("idoferta"));
                            oferta.setServicios(jsonObject.getString("nombre"));
                            oferta.setFechainicio(jsonObject.getString("fechainicio"));//cambiar por fecha de inicio
                            oferta.setVacantes(jsonObject.getInt("vacantes"));
                            listaTrabajo.add(oferta);
                        }

                        ListaTrabajosRecAdapter adapter = new ListaTrabajosRecAdapter(listaTrabajo);
                        recyclerTrabajo.setAdapter(adapter);
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Se obtiene el id de la oferta para consultar a detalle en el siguiente fragment
                                int idOfertaSeleccionado = listaTrabajo.get(recyclerTrabajo.getChildAdapterPosition(view)).getId();
                                String nombreFinca = listaTrabajo.get(recyclerTrabajo.getChildAdapterPosition(view)).getServicios();
                                imprimirMensaje("Finca Seleccionada " + nombreFinca);
                                Bundle parametrosEnvio = new Bundle();
                                parametrosEnvio.putInt("idOferta", idOfertaSeleccionado);
                                parametrosEnvio.putString("nombreFinca", nombreFinca);
                                Fragment detalleTrabajoFragment = new DetalleTrabajoFragment();
                                detalleTrabajoFragment.setArguments(parametrosEnvio);
                                getFragmentManager().beginTransaction().replace(R.id.content_recolector, detalleTrabajoFragment).commit();

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        imprimirMensaje("Error al cargar listado de ofertas - RESPONSE ");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    imprimirMensaje("Error al cargar listado de ofertas - WEBSERVICE");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Error al cargar trabajos - Recolector");
        }
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    //Obtiene la informacion del usuario que ha iniciado sesion
    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
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
