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
import com.hacu.micafe.Caficultor.Adaptadores.ListaOfertasAdapter;
import com.hacu.micafe.Caficultor.FragmentsCaf.DetalleOfertaCafFragment;
import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Adaptadores.ListaOfertasRecAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfertasRecolectorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfertasRecolectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfertasRecolectorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private JsonObjectRequest jsonObjectRequest;
    RecyclerView recyclerOfertas;
    ArrayList<Oferta> listaOfertas;

    private OnFragmentInteractionListener mListener;

    public OfertasRecolectorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfertasRecolectorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfertasRecolectorFragment newInstance(String param1, String param2) {
        OfertasRecolectorFragment fragment = new OfertasRecolectorFragment();
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
        View vista = inflater.inflate(R.layout.fragment_ofertas_recolector, container, false);
        listaOfertas = new ArrayList<>();
        recyclerOfertas = vista.findViewById(R.id.recOfertasRecolector);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerOfertas.setHasFixedSize(true);
        cargarOfertasRecolector();
        
        return vista;
    }

    private void cargarOfertasRecolector() {
        try {
            final ProgressDialog progreso = new ProgressDialog(getContext());
            progreso.setMessage("Buscando ofertas...");
            progreso.show();
            String id = String.valueOf(getSession().getInt("id", 0));//Obtiene el ID de la persona que ha iniciado sesion
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultarofertasrecolector";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.hide();
                    Oferta oferta = null;
                    //Obtiene el JSON de la respuesta
                    JSONArray jsonArray = response.optJSONArray("micafe");
                    //Se recorre cada elemento del json
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            oferta = new Oferta();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            oferta.setId(jsonObject.optInt("id"));
                            oferta.setServicios(jsonObject.getString("nombre"));
                            oferta.setFechainicio(jsonObject.getString("fechainicio"));//cambiar por fecha de inicio
                            oferta.setVacantes(jsonObject.getInt("vacantes"));
                            listaOfertas.add(oferta);
                        }

                        ListaOfertasRecAdapter adapter = new ListaOfertasRecAdapter(listaOfertas);
                        recyclerOfertas.setAdapter(adapter);
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Se obtiene el id de la oferta para consultar a detalle en el siguiente fragment
                                int idOfertaSeleccionado = listaOfertas.get(recyclerOfertas.getChildAdapterPosition(view)).getId();
                                String nombreFinca = listaOfertas.get(recyclerOfertas.getChildAdapterPosition(view)).getServicios();
                                Bundle parametrosEnvio = new Bundle();
                                parametrosEnvio.putInt("idOferta", idOfertaSeleccionado);
                                parametrosEnvio.putString("nombreFinca", nombreFinca);

                                Fragment fragDetalleOferta = new DetalleOfertaRecolectorFragment();
                                fragDetalleOferta.setArguments(parametrosEnvio);
                                getFragmentManager().beginTransaction().replace(R.id.content_recolector, fragDetalleOferta).commit();

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
                    progreso.hide();
                    imprimirMensaje("Error al cargar listado de ofertas - WEBSERVICE");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("TAG","Error al cargar las ofertas - Recolector");
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
