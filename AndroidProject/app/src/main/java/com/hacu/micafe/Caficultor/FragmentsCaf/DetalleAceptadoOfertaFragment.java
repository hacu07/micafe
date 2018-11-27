package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetalleAceptadoOfertaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView imagen;
    private TextView nombre,correo,cedula,celular,fechaNacimiento,direccion,departamento,municipio;
    private JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;
    private ProgressDialog progreso;

    public DetalleAceptadoOfertaFragment() {
        // Required empty public constructor
    }


    public static DetalleAceptadoOfertaFragment newInstance(String param1, String param2) {
        DetalleAceptadoOfertaFragment fragment = new DetalleAceptadoOfertaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_detalle_aceptado_oferta, container, false);
        instanciarElementos(vista);
        final String idOferta = getArguments() != null ? getArguments().getString("idoferta") : "0";
        final String cedula = getArguments() != null ? getArguments().getString("cedula") : "0";
        consultarDatosPostulado(cedula);

        Button btnVolverListaPostulados = vista.findViewById(R.id.detace_btnVolver);
        Button btnRegistrarPesada = vista.findViewById(R.id.detace_btnPesada);

        btnRegistrarPesada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registrarPesada(idOferta,cedula);
                imprimirMensaje("REGISTRAR PESADA");
            }
        });

        btnVolverListaPostulados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Vuelve al listado de los postulados de la oferta seleccionada anteriormente
                volverListadoAceptados(idOferta);
            }
        });
        return vista;
    }

    private void volverListadoAceptados(String idOferta) {
        Bundle parametrosEnvio = new Bundle();
        parametrosEnvio.putInt("idOferta", Integer.parseInt(idOferta));

        Fragment fragAceptados = new AceptadosOfertaCafFragment();
        fragAceptados.setArguments(parametrosEnvio);
        getFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragAceptados).commit();

    }

    private void consultarDatosPostulado(final String cedulaRecolector) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Consultando información del postulado...");
        progreso.show();
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultardatospostulado&cedulapostulado="+cedulaRecolector;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();
                JSONArray json = response.optJSONArray("micafe");
                try{
                    JSONObject jsonObject = json.getJSONObject(0);

                    cargarImagenAceptado(jsonObject.optString("urlimagen"));
                    nombre.setText(jsonObject.optString("nombre"));
                    cedula.setText(cedulaRecolector);
                    correo.setText(jsonObject.optString("correo"));
                    celular.setText(jsonObject.optString("celular"));
                    fechaNacimiento.setText(jsonObject.optString("fechanacimiento"));
                    direccion.setText(jsonObject.optString("direccion"));
                    departamento.setText(jsonObject.optString("departamento"));
                    municipio.setText(jsonObject.optString("municipio"));
                }catch (JSONException e){
                    imprimirMensaje("No se logro consultar los datos del postulado a la oferta");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                imprimirMensaje("Error de conexion");
                error.printStackTrace();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void cargarImagenAceptado(String urlimagen) {
        if (urlimagen != null){
            //ASIGNACION DE FOTO DE PERFIL CON LIBRERIA GLIDE (IMPORTADA EN APP)
            String url = getString(R.string.ip_servidor)+urlimagen;
            Log.i("url",url);
            Glide.with(getContext()).load(url).into(imagen);

        }
    }

    private void instanciarElementos(View vista) {
        imagen = vista.findViewById(R.id.detace_img);
        nombre = vista.findViewById(R.id.detace_nombre);
        correo = vista.findViewById(R.id.detace_correo);
        cedula = vista.findViewById(R.id.detace_cedula);
        celular = vista.findViewById(R.id.detace_celular);
        fechaNacimiento = vista.findViewById(R.id.detace_fechanacimiento);
        direccion = vista.findViewById(R.id.detace_direccion);
        departamento = vista.findViewById(R.id.detace_departamento);
        municipio = vista.findViewById(R.id.detace_municipio);
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
