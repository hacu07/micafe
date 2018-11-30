package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import java.util.HashMap;
import java.util.Map;


public class RegistroFincaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegistroFincaFragment() {
        // Required empty public constructor
    }


    /*VARIABLES FRAGMENT*/
    private EditText nombreFinca, departamento, municipio, corregimiento, vereda, hectareas, telefono;
    private Button btnRegistrar;
    //Usado para el registro en la BD
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progreso;
    private StringRequest stringRequest;



    // TODO: Rename and change types and number of parameters
    public static RegistroFincaFragment newInstance(String param1, String param2) {
        RegistroFincaFragment fragment = new RegistroFincaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_registro_finca, container, false);;
        instanciarElementos(vista);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarFinca();
            }
        });
        return vista;
    }

    //Obtiene los datos ingresados en el formulario y los envia a  la api para ser cargados en la BD
    private void registrarFinca() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando finca...");
        progreso.show();
        final Finca finca = new Finca();

        finca.setNombre(nombreFinca.getText().toString());
        finca.setDepartamento(departamento.getText().toString());
        finca.setMunicipio(municipio.getText().toString());
        finca.setCorregimiento(corregimiento.getText().toString());
        finca.setVereda(vereda.getText().toString());
        finca.setHectareas(Integer.parseInt(hectareas.getText().toString()));
        finca.setTelefono(telefono.getText().toString());

        String url = getString(R.string.ip_servidor)+"apimicafe.php";


        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                Log.i("TAG",response.toString());
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Registro completo");
                        limpiarCampos();
                        break;
                    case "Error":
                        imprimirMensaje("No se registro, intente de nuevo. \n" + response);
                        Log.i("TAG",response.toString());
                        break;
                    default:
                            imprimirMensaje(response);
                            Log.i("TAG",response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("Error en el webservice");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros =  new HashMap<>();
                String opcion = "registrofinca";
                parametros.put("opcion",opcion);
                parametros.put("idadministrador",String.valueOf(getSession().getInt("id",0)));
                parametros.put("nombrefinca",finca.getNombre());
                parametros.put("departamento",finca.getDepartamento());
                parametros.put("municipio",finca.getMunicipio());
                parametros.put("corregimiento",finca.getCorregimiento());
                parametros.put("vereda",finca.getVereda());
                parametros.put("hectareas",String.valueOf(finca.getHectareas()));
                parametros.put("telefono",finca.getTelefono());
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void limpiarCampos() {
        nombreFinca.setText("");
        departamento.setText("");
        municipio.setText("");
        corregimiento.setText("");
        vereda.setText("");
        hectareas.setText("");
        telefono.setText("");
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private void instanciarElementos(View vista) {
        nombreFinca = vista.findViewById(R.id.regfin_nombre);
        departamento = vista.findViewById(R.id.regfin_departamento);
        municipio = vista.findViewById(R.id.regfin_municipio);
        corregimiento = vista.findViewById(R.id.regfin_corregimiento);
        vereda = vista.findViewById(R.id.regfin_vereda);
        hectareas = vista.findViewById(R.id.regfin_hectareas);
        telefono = vista.findViewById(R.id.regfin_telefono);
        btnRegistrar = vista.findViewById(R.id.regfin_btnRegistrar);
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

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }
}
