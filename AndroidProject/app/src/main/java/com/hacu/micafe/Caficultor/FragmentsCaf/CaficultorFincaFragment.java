package com.hacu.micafe.Caficultor.FragmentsCaf;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Caficultor.Adaptadores.ListaFincasAdapter;
import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CaficultorFincaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog progreso;
    private RecyclerView recyclerFincas;
    private ArrayList<Finca> listaFincas;
    private JsonObjectRequest jsonObjectRequest;

    public CaficultorFincaFragment() {
        // Required empty public constructor
    }


    public static CaficultorFincaFragment newInstance(String param1, String param2) {
        CaficultorFincaFragment fragment = new CaficultorFincaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_caficultor_finca, container, false);
        instanciarElementos(vista);
        Button btn_crearFinca = vista.findViewById(R.id.caffin_btncrearfinca);
        consultarFincasCaficultor();

        btn_crearFinca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reemplaza el fragment en el contenedor que se indica
                Fragment formularioFincaFragment = new RegistroFincaFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,formularioFincaFragment).commit();
            }
        });

        return vista;
    }

    private void consultarFincasCaficultor() {
        try {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Consultando Fincas...");
            progreso.show();
            String url = getString(R.string.ip_servidor) + "apimicafe.php?opcion=consultarfincascaficultor&idcaficultor=" + getSession().getInt("id", 0);
            Log.i("TAG", url);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    JSONArray jsonArray = response.optJSONArray("micafe");
                    Finca finca = null;
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            finca = new Finca();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            finca.setId(jsonObject.optInt("id"));
                            finca.setNombre(jsonObject.optString("nombre"));
                            listaFincas.add(finca);
                        }
                        ListaFincasAdapter adapter = new ListaFincasAdapter(listaFincas);
                        recyclerFincas.setAdapter(adapter);
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int codFinca = listaFincas.get(recyclerFincas.getChildAdapterPosition(view)).getId();
                                Bundle bundle = new Bundle();
                                bundle.putInt("idFinca", codFinca);
                                Fragment detalleFincaFrag = new DetalleFincaCaficultorFragment();
                                detalleFincaFrag.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.content_caficultor, detalleFincaFrag).commit();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        imprimirMensaje("Error catch pesadas");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    imprimirMensaje("No se encontraron fincas registras - No Conexion");
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e){
            Log.i("Tag","Error cargando fincas caficultor\n"+e.toString());
        }
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void instanciarElementos(View vista) {
        listaFincas = new ArrayList<>();
        recyclerFincas = vista.findViewById(R.id.caffin_recyclerfinca);
        recyclerFincas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerFincas.setHasFixedSize(true);
    }

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
