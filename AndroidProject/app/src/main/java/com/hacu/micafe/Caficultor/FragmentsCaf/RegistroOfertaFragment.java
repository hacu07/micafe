package com.hacu.micafe.Caficultor.FragmentsCaf;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;
import com.hacu.micafe.Utilidades.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RegistroOfertaFragment extends Fragment implements DatePickerFragment.DateDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /* OBJETOS DEL FRAGMENT */
    private Spinner comboFincas;
    private RadioButton radKilos, radJornal;
    private TextView fechainicio;
    private EditText valorPago, vacantes, diasTrabajo, planta, servicios;
    private ArrayList<String> listaFincasSpi;//Lista para insertar en el spiner
    private ArrayList<Finca> listaFincaBd;//Lista con los datos de la BD
    private String fincaSeleccionada;
    //RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progreso;

    //Usado para el registro en la BD
    private StringRequest stringRequest;

    private static final String DIALOG_DATE = "RegistroOfertaFragment.DateDialog";

    private OnFragmentInteractionListener mListener;

    public RegistroOfertaFragment() {
        // Required empty public constructor
    }


    public static RegistroOfertaFragment newInstance(String param1, String param2) {
        RegistroOfertaFragment fragment = new RegistroOfertaFragment();
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
        final View vista = inflater.inflate(R.layout.fragment_registro_oferta, container, false);
        instanciarElementos(vista);
        cargarComboFincas();

        Button btnRegistrarOferta = vista.findViewById(R.id.regofe_btnRegistrarOferta);
        ImageView btnCalendario =  vista.findViewById(R.id.regofe_btnfecha);

        fechainicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarFecha();
            }
        });
        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarFecha();
            }
        });
        btnRegistrarOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarOferta();
            }
        });

        return vista;
    }


    private void registrarOferta() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando oferta...");
        progreso.show();
        //Objeto con los datos a registrar
        final Oferta oferta = new Oferta();
        oferta.setIdModoPago(obtenerModoPago());
        oferta.setValorPago(Integer.parseInt(valorPago.getText().toString()));
        oferta.setVacantes(Integer.parseInt(vacantes.getText().toString()));
        oferta.setDiasTrabajo(Integer.parseInt(diasTrabajo.getText().toString()));
        oferta.setPlanta(planta.getText().toString());
        oferta.setServicios(servicios.getText().toString());
        oferta.setFechainicio(fechainicio.getText().toString());

        String url = getString(R.string.ip_servidor)+"apimicafe.php";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Registro de OFERTA completo");
                        //limpiarCampos();
                        break;
                    case "Error":
                        imprimirMensaje("No se registro, intente de nuevo. \n" + response);
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
                imprimirMensaje("Error en el webservice - REGISTRAR OFERTA");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros =  new HashMap<>();

                String opcion = "registroferta";
                parametros.put("opcion",opcion);
                parametros.put("idadministrador",String.valueOf(getSession().getInt("id",0)));
                parametros.put("nombrefinca",fincaSeleccionada);
                parametros.put("idmodopago",String.valueOf(oferta.getIdModoPago()));
                parametros.put("valorpago",String.valueOf(oferta.getValorPago()));
                parametros.put("vacantes",String.valueOf(oferta.getVacantes()));
                parametros.put("diastrabajo",String.valueOf(oferta.getDiasTrabajo()));
                parametros.put("planta",oferta.getPlanta());
                parametros.put("servicios",oferta.getServicios());
                parametros.put("fechainicio",oferta.getFechainicio());
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    //Se usa el operador ternario para retornar el id del modo de pago seleccionado
    private int obtenerModoPago() {
        return radKilos.isChecked()?1:2;
    }

    //Realiza la peticion a la API de las fincas registradas por el usuario-caficultor que ha iniciado sesion
    private void cargarComboFincas() {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultarfincas&caficultor="+String.valueOf(getSession().getInt("id",0));
        Log.i("TAG",url);
        jsonObjectRequest =  new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                imprimirMensaje("inicio on response");
                Finca finca = null;
                listaFincaBd = new ArrayList<>();
                JSONArray json = response.optJSONArray("micafe");

                try{
                    for (int i = 0; i<json.length(); i++){
                        finca = new Finca();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);
                        finca.setId(jsonObject.optInt("id"));
                        finca.setNombre(jsonObject.optString("nombre"));
                        listaFincaBd.add(finca);
                        Log.i("TAG",String.valueOf(jsonObject.optInt("id")));
                        Log.i("TAG",jsonObject.optString("nombre"));
                    }
                    obtenerListaFinca();
                    ArrayAdapter<String> adapterFincas =  new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listaFincasSpi);
                    comboFincas.setAdapter(adapterFincas);
                    comboFincas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                            imprimirMensaje("Finca Seleccionada: "+parent.getItemAtPosition(position).toString().toUpperCase() );
                            ((TextView) parent.getChildAt(0)).setTextColor(getActivity().getResources().getColor(R.color.amarillo));
                            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                            fincaSeleccionada = parent.getItemAtPosition(position).toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }catch (JSONException e){
                    imprimirMensaje("Error en respuesta al cargar las fincas registradas por el usuario");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("Error al cargar las fincas - webservice \n DEBE REGISTRAR SU FINCA ANTES.");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void obtenerListaFinca() {
        listaFincasSpi =  new ArrayList<String>();
        listaFincasSpi.add("Seleccione Finca");
        fincaSeleccionada = "Seleccione Finca";
        for (int i = 0; i< listaFincaBd.size(); i++){
            //obtiene de la lista el id y nombre
            listaFincasSpi.add(listaFincaBd.get(i).getNombre());
        }
    }

    private void instanciarElementos(View vista) {
        radJornal = vista.findViewById(R.id.radJornal);
        radKilos = vista.findViewById(R.id.radKilos);
        comboFincas = vista.findViewById(R.id.regofe_comboFincas);
        valorPago = vista.findViewById(R.id.regofe_valorpago);
        vacantes = vista.findViewById(R.id.regofe_vacantes);
        diasTrabajo = vista.findViewById(R.id.regofe_diastrabajo);
        planta = vista.findViewById(R.id.regofe_planta);
        servicios = vista.findViewById(R.id.regofe_servicios);
        fechainicio = vista.findViewById(R.id.regofe_fechaninicio);
    }

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

    //Metodo para desplegar el DateChooser
    /*
    public void cargarFecha() {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getActivity().getFragmentManager(),DIALOG_DATE);
    }*/


    private void cargarFecha(){
        final Calendar calendario = Calendar.getInstance();
        int yy = calendario.get(Calendar.YEAR);
        int mm = calendario.get(Calendar.MONTH);
        int dd = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String fecha = String.valueOf(year) +"-"+String.valueOf(monthOfYear) +"-"+String.valueOf(dayOfMonth);
                fechainicio.setText(fecha);
            }
        }, yy, mm, dd);

        datePicker.show();
    }

    @Override
    public void onFinishDialog(Date date) {
        //Asigna la fecha seleccionada en el DatePicker a la etiqueta de fecha de nacimiento
        fechainicio.setText(formatDate(date));
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hireDate = sdf.format(date);
        return hireDate;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
