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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Modelo.Experiencia;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Adaptadores.ListaExperienciasAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class ExperienciaRecolectorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private JsonObjectRequest jsonObjectRequest;
    private RecyclerView recyclerExperiencias;
    private ArrayList<Experiencia> listaExperiencias;


    public ExperienciaRecolectorFragment() {
        // Required empty public constructor
    }

    public static ExperienciaRecolectorFragment newInstance(String param1, String param2) {
        ExperienciaRecolectorFragment fragment = new ExperienciaRecolectorFragment();
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
        View vista = inflater.inflate(R.layout.fragment_experiencia_recolector, container, false);
        Button btnCrearExperiencia = vista.findViewById(R.id.exprec_btncrear);
        instanciarElementos(vista);
        consultarExperienciasRecolector();

        btnCrearExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragExperienciaRec = new RegistroExperienciaFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_recolector,fragExperienciaRec).commit();
            }
        });
        return vista;
    }

    private void consultarExperienciasRecolector() {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando experiencias");
            progreso.show();
            String idRecolector = String.valueOf(getSession().getInt("id", 0));
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultarexperienciasrecolector&idrecolector=" + idRecolector;
            Log.i("TAG", url);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    Experiencia experiencia = null;
                    JSONArray jsonArray = response.optJSONArray("micafe");
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            experiencia = new Experiencia();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            experiencia.setEmpresa(jsonObject.optString("empresa"));
                            experiencia.setCargo(jsonObject.getString("cargo"));
                            experiencia.setFunciones(jsonObject.getString("funciones"));
                            experiencia.setTiempo(jsonObject.optInt("tiempo"));
                            experiencia.setSupervisor(jsonObject.getString("supervisor"));
                            experiencia.setContactosupervisor(jsonObject.getString("contactosupervisor"));
                            listaExperiencias.add(experiencia);
                        }
                        ListaExperienciasAdapter adapter = new ListaExperienciasAdapter(listaExperiencias);
                        recyclerExperiencias.setAdapter(adapter);
                    } catch (JSONException e) {
                        imprimirMensaje("Error en respuesta de experiencias");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    imprimirMensaje("No se encontraron experiencias");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Consultar experiencia recolector - Recolector");
        }
    }

    private void instanciarElementos(View vista) {
        listaExperiencias = new ArrayList<>();
        recyclerExperiencias = vista.findViewById(R.id.recyclerExperienciasRec);
        recyclerExperiencias.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerExperiencias.setHasFixedSize(true);
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
