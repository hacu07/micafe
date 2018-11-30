package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import java.util.HashMap;
import java.util.Map;


public class CalificarAceptadoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SeekBar barra;
    private TextView textoBarra;
    private EditText comentario;
    private RecyclerView recyclerComentarios;
    private int calificacion = 3;

    private ProgressDialog progreso;

    private StringRequest stringRequest;


    public CalificarAceptadoFragment() {
        // Required empty public constructor
    }


    public static CalificarAceptadoFragment newInstance(String param1, String param2) {
        CalificarAceptadoFragment fragment = new CalificarAceptadoFragment();
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
       View vista = inflater.inflate(R.layout.fragment_calificar_aceptado, container, false);
       instanciarElementos(vista);
       final String idOferta = getArguments() != null ? getArguments().getString("idoferta") : "0";
       final String cedula = getArguments() != null ? getArguments().getString("cedula") : "0";
       Button btnVolver = vista.findViewById(R.id.calacecaf_btnVolver);
       Button btnRegistrarCalificacion = vista.findViewById(R.id.calacecaf_btnRegistrar);

       btnRegistrarCalificacion.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                registrarComentario(cedula);
           }
       });

       //Evento cuando cambia la eleccion de la barra
       barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               calificacion = i;
               switch (i){
                   case 0:
                       textoBarra.setText("Malo");
                       calificacion = 1;
                       break;
                   case 1:
                       textoBarra.setText("Malo");
                       break;
                   case 2:
                       textoBarra.setText("Regular");
                       break;
                   case 3:
                       textoBarra.setText("Bueno");
                       break;
                   case 4:
                       textoBarra.setText("Muy Bueno");
                       break;
                   case 5:
                       textoBarra.setText("Excelente");
                       break;
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });


        return vista;
    }

    private void registrarComentario(final String cedulaRecolector) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando comentario...");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Registo de comentario completo");
                        limpiarCampos();
                        break;
                    case "Error":
                        imprimirMensaje("No se registro, intente de nuevo. \n" + response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("No se registro - Error de conexion calificacion de recolector");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametrosEnvio = new HashMap<>();
                parametrosEnvio.put("opcion","registrarcalificacion");
                parametrosEnvio.put("idadmon",String.valueOf(getSession().getInt("id",0)));
                parametrosEnvio.put("cedularecolector",cedulaRecolector);
                parametrosEnvio.put("comentario",comentario.getText().toString());
                parametrosEnvio.put("calificacion",String.valueOf(calificacion));
                return parametrosEnvio;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void limpiarCampos() {
        comentario.setText("");
        barra.setProgress(3);
        textoBarra.setText("Bueno");
    }

    private void imprimirMensaje(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }

    private void instanciarElementos(View vista) {
        barra = vista.findViewById(R.id.calacecaf_calificacion);
        textoBarra = vista.findViewById(R.id.calacecaf_textocalificacion);
        comentario = vista.findViewById(R.id.calacecaf_comentario);
        recyclerComentarios = vista.findViewById(R.id.calacecaf_recycler);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerComentarios.setHasFixedSize(true);
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
