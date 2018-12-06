package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetalleFincaCaficultorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText nombre, departamento, municipio, corregimiento, vereda, hectareas, telefono;
    private ProgressDialog progreso;
    private StringRequest stringRequest;
    private JsonObjectRequest jsonObjectRequest;

    public DetalleFincaCaficultorFragment() {
        // Required empty public constructor
    }


    public static DetalleFincaCaficultorFragment newInstance(String param1, String param2) {
        DetalleFincaCaficultorFragment fragment = new DetalleFincaCaficultorFragment();
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
        View vista = inflater.inflate(R.layout.fragment_detalle_finca_caficultor, container, false);
        final int idFinca = getArguments() != null ? getArguments().getInt("idFinca") : 0;
        Button btnVolver =  vista.findViewById(R.id.detfincaf_btnVolver);
        Button btnActualizar =  vista.findViewById(R.id.detfincaf_btnActualizar);
        instanciarElementos(vista);
        consultarDetalleFinca(idFinca);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarInfoFinca(idFinca);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fincasFragment =  new CaficultorFincaFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fincasFragment).commit();
            }
        });

        return vista;
    }

    private void actualizarInfoFinca(final int idFinca) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Actualizando datos de la finca...");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Finca actualizada exitosamente.");

                        break;
                    case "Error":
                        imprimirMensaje("No se actualizo la finca, intente de nuevo. \n" + response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("No se actualizo\nError de conexion");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("opcion","actualizardatosfinca");
                parametros.put("idfinca",String.valueOf(idFinca));
                parametros.put("idadministrador",String.valueOf(getSession().getInt("id",0)));
                parametros.put("nombre",nombre.getText().toString());
                parametros.put("departamento",departamento.getText().toString());
                parametros.put("municipio",municipio.getText().toString());
                parametros.put("corregimiento",corregimiento.getText().toString());
                parametros.put("vereda",vereda.getText().toString());
                parametros.put("hectareas",hectareas.getText().toString());
                parametros.put("telefono",telefono.getText().toString());
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void consultarDetalleFinca(int idFinca) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Consultando detalle de la finca");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultardetallefinca&idfinca="+idFinca;
        jsonObjectRequest =  new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();
                JSONArray json = response.optJSONArray("micafe");
                try {
                    JSONObject jsonObject = json.getJSONObject(0);
                    nombre.setText(jsonObject.optString("nombre"));
                    departamento.setText(jsonObject.optString("departamento"));
                    municipio.setText(jsonObject.optString("municipio"));
                    corregimiento.setText(jsonObject.optString("corregimiento"));
                    vereda.setText(jsonObject.optString("vereda"));
                    hectareas.setText(String.valueOf(jsonObject.optInt("hectareas")));
                    telefono.setText(jsonObject.optString("telefono"));
                } catch (JSONException e) {
                    progreso.hide();
                    e.printStackTrace();
                    imprimirMensaje("Error en respuesta detalle finca");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("No se encontraron datos de la finca seleccionada - Error de conexion");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    //Obtiene la informacion del usuario que ha iniciado sesion
    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }

    private void instanciarElementos(View vista) {
        nombre = vista.findViewById(R.id.detcafin_nombre);
        departamento = vista.findViewById(R.id.detcafin_departamento);
        municipio = vista.findViewById(R.id.detcafin_municipio);
        corregimiento = vista.findViewById(R.id.detcafin_corregimiento);
        vereda = vista.findViewById(R.id.detcafin_vereda);
        hectareas = vista.findViewById(R.id.detcafin_hectareas);
        telefono = vista.findViewById(R.id.detcafin_telefono);
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
