package com.hacu.micafe.Recolector.Fragments;

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
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.Experiencia;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import java.util.HashMap;
import java.util.Map;


public class RegistroExperienciaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText empresa, cargo, funciones, tiempo, supervisor, contactoSupervisor;
    private ProgressDialog progreso;
    private StringRequest stringRequest;

    public RegistroExperienciaFragment() {
        // Required empty public constructor
    }

    public static RegistroExperienciaFragment newInstance(String param1, String param2) {
        RegistroExperienciaFragment fragment = new RegistroExperienciaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_registro_experiencia, container, false);
        instanciarElementos(vista);
        
        Button btnRegistrarExp = vista.findViewById(R.id.exprec_btnregistrar);
        btnRegistrarExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarExperiencia();
            }
        });
        return vista;
    }

    private void registrarExperiencia() {
        progreso =  new ProgressDialog(getContext());
        progreso.setMessage("Registrando experiencia...");
        progreso.show();
        final Experiencia experiencia = obtenerValores();
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Actualizacion completa");
                        limpiarCampos();
                        break;
                    case "Error":
                        imprimirMensaje("No se actualizo, intente de nuevo. \n" + response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("No se logro registrar la experiencia \n Intente de nuevo");
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("opcion","registrarexperienciarecolector");
                parametros.put("idrecolector",String.valueOf(experiencia.getIdRecolector()));
                parametros.put("empresa",experiencia.getEmpresa());
                parametros.put("cargo",experiencia.getCargo());
                parametros.put("funciones",experiencia.getFunciones());
                parametros.put("tiempo",String.valueOf(experiencia.getTiempo()));
                parametros.put("supervisor",experiencia.getSupervisor());
                parametros.put("contactosupervisor",experiencia.getContactosupervisor());
                return parametros;
            }
        };
        //limita el tiempo de actualizacion a dos segundos
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void limpiarCampos() {
        empresa.setText("");
        cargo.setText("");
        funciones.setText("");
        tiempo.setText("");
        supervisor.setText("");
        contactoSupervisor.setText("");
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    /*Retorna un objeto Experiencia con los datos obtenidos de los campos diligenciados por el usuario (recolector)*/
    private Experiencia obtenerValores() {
        Experiencia experiencia = new Experiencia();
        experiencia.setIdRecolector(getSession().getInt("id",0));
        experiencia.setEmpresa(empresa.getText().toString());
        experiencia.setCargo(cargo.getText().toString());
        experiencia.setFunciones(funciones.getText().toString());
        experiencia.setTiempo(Integer.parseInt(tiempo.getText().toString()));
        experiencia.setSupervisor(supervisor.getText().toString());
        experiencia.setContactosupervisor(contactoSupervisor.getText().toString());
        return experiencia;
    }

    //Obtiene la informacion del usuario que ha iniciado sesion
    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }

    private void instanciarElementos(View vista) {
        empresa = vista.findViewById(R.id.exprec_empresa);
        cargo = vista.findViewById(R.id.exprec_cargo);
        funciones = vista.findViewById(R.id.exprec_funciones);
        tiempo = vista.findViewById(R.id.exprec_tiempo);
        supervisor = vista.findViewById(R.id.exprec_supervisor);
        contactoSupervisor = vista.findViewById(R.id.exprec_contactosupervisor);
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
