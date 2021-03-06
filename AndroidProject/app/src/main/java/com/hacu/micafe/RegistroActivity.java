package com.hacu.micafe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.Rol;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.Utilidades.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RegistroActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener{
    private Spinner comboRoles;
    private RadioButton radCC,radCE,radTI;
    private Button btnRegistro;
    private EditText txtNombre,txtCedula,txtContrasenia,txtCelular;
    private ArrayList<String> listaRolesSpi;//Lista para insertar en el spiner
    private ArrayList<Rol> listaRolesBd;//Lista con los datos de la BD
    private String rolSeleccionado;
    //RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progreso;

    //Usado para el registro en la BD
    private StringRequest stringRequest;
    private static final String DIALOG_DATE = "RegistroActivity.DateDialog";

    //PRUEBA - BORRAR ESTO
    Timer timer = new Timer();
    final Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        instanciarElementos();
        consultarRoles();
        //llamar();
    }


    //ELIMINAR ESTE METODO - PRUEBA
    /*
    public void llamar(){

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AsyncTask mytask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {

                        new Handler (Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //imprimirMensaje("hola cada 5 segundos");
                                //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.clic);
                                //mp.start();
                            }
                        });

                        return null;
                    }
                };
                mytask.execute();
            }
        };
        timer.schedule(task,0,10000);
    }
    */


    //Encargado de enviar los datos a la API para registrar a la BD con php
    public void registrarUsuario(View view) {
        btnRegistro.setEnabled(false);
        if (validarRegistro()==true){

            validarRegistroUsuario();
        }
        btnRegistro.setEnabled(true);
    }

    private void validarRegistroUsuario(){

        final Usuarios usuarios = new Usuarios();
        usuarios.setNombre(txtNombre.getText().toString().toUpperCase());
        usuarios.setTipodocumento(obtenerTipoDocumentoSeleccionado());
        usuarios.setCedula(txtCedula.getText().toString().trim());
        usuarios.setContrasenia(txtContrasenia.getText().toString().trim());
        usuarios.setCelular(txtCelular.getText().toString().trim());

        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=validarinsercionreporte&nombre="+usuarios.getNombre()
                +"&contrasenia="+usuarios.getContrasenia()
                +"&tipodocumento="+usuarios.getTipodocumento()
                +"&cedula="+usuarios.getCedula()
                +"&celular="+usuarios.getCelular()
                +"&rol="+rolSeleccionado;

        url = url.replace(" ","%20");

        Log.i("TAG",url);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("TAG",response.toString());
                JSONArray json = response.optJSONArray("micafe");//Obtiene el json con la respuesta

                //El key de la respuesta es "mensaje" se debe obtener este y publicar el mensaje
                try {
                    JSONObject jsonObject = json.getJSONObject(0);//toma la primera fila de la respuesta
                    switch (jsonObject.optString("existen")){
                        case "0"://Si es 0 es porque no encontro registros en la BD con ese numero de documento
                            imprimirMensaje("Registro completo");
                            limpiarCampos();
                            mostrarLogin();
                            break;
                        case "1"://Encontro un registro en la BD con ese numero de documento de identidad
                            imprimirMensaje("El numero de documento ya se encuentra registrado. \n No se puede registrar.");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("TAG",error.toString());
                imprimirMensaje(" Error en el webservice validarRegistroUsuario "+error.toString());
            }
        });
        //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
        //request.add(stringRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void consultarRoles() {
        //Valida la conexion a internet
        ConnectivityManager con = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();//objeto a utilizar para la validacion

        if (networkInfo != null && networkInfo.isConnected()){
            imprimirMensaje("Conexión a internet exitosa");
            cargarWebServiceRoles();//Ejecuta el webservice enviado la opcion para consultar y traer los ROLES
        }else {
           imprimirMensaje("NO TIENE CONEXION A INTERNET");
        }
    }



    private void cargarWebServiceRoles() {

        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=consultaroles";
        Log.i("TAG",url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Rol rol = null;
                listaRolesBd = new ArrayList<>();
                JSONArray json = response.optJSONArray("micafe");

                try{
                    for (int i = 0; i<json.length(); i++){//Recorre acada elemento del json
                        rol = new Rol();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);
                        rol.setId(jsonObject.optInt("id"));
                        rol.setRol(jsonObject.getString("rol"));
                        listaRolesBd.add(rol);
                    }
                    obtenerLista();
                    ArrayAdapter<String> adapter =  new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,listaRolesSpi);
                    comboRoles.setAdapter(adapter);
                    comboRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                            imprimirMensaje("Seleccionado: "+parent.getItemAtPosition(position).toString() );
                            ((TextView) parent.getChildAt(0)).setTextColor(getColor(R.color.amarillo));
                            rolSeleccionado = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("TAG",error.toString());
                imprimirMensaje("Error al conectar a la API");
                Log.i("TAG","NO SE CONECTO A LA API");
            }
        });
        //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
        //request.add(stringRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void instanciarElementos() {
        comboRoles =  findViewById(R.id.reg_spiRoles);
        txtNombre = findViewById(R.id.reg_nombre);
        txtCedula = findViewById(R.id.reg_cedula);
        txtContrasenia = findViewById(R.id.reg_contrasenia);
        txtCelular = findViewById(R.id.reg_celular);
        radCC = findViewById(R.id.radCC);
        radCE = findViewById(R.id.radCE);
        radTI = findViewById(R.id.radTI);
        btnRegistro = findViewById(R.id.reg_btnRegistrar);
    }

    //lista a mostrar en el combo
    private void obtenerLista() {
        listaRolesSpi =  new ArrayList<String>();
        listaRolesSpi.add("Seleccione aquí");
        rolSeleccionado = "Seleccione aquí";
        for (int i = 1; i<listaRolesBd.size(); i++){
            //obtiene de la lista el id y nombre
            listaRolesSpi.add(listaRolesBd.get(i).getRol());
        }
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
    }

    //Metodo para desplegar el DateChooser
    public void cargarFecha(View view) {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(),DIALOG_DATE);
    }

    @Override
    public void onFinishDialog(Date date) {
        //Asigna la fecha seleccionada en el DatePicker a la etiqueta de fecha de nacimiento
        //lblFechaNac.setText(formatDate(date));
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hireDate = sdf.format(date);
        return hireDate;
    }

    //Carga la actividad login
    private void mostrarLogin() {
        Intent miIntent = new Intent(RegistroActivity.this,LoginActivity.class);
        startActivity(miIntent);
        finish();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCedula.setText("");
        txtContrasenia.setText("");
        txtCelular.setText("");
        btnRegistro.setEnabled(true);
    }

    private Boolean validarRegistro(){

        if (rolSeleccionado.equals("Seleccione aquí")){
            imprimirMensaje("Debe Seleccionar si es Caficultor, Recolector o Comerciante de café.");
            return false;
        }
        //Evalua si los campos Nombre, Contrasenia, Cedula, Celular, Direccion, Departamento, Municipio estan vacios
        if (txtNombre.getText().toString() == "" || txtContrasenia.getText().toString().isEmpty() || txtCedula.getText().toString().isEmpty() || txtCelular.getText()
                .toString().isEmpty()){
            imprimirMensaje("Faltan campos por llenar");
            return false;
        }

        if (txtContrasenia.getText().toString().trim().length() <= 5 ){
            imprimirMensaje("La contraseña debe tener minimo 6 digitos");
            return false;
        }

        if (txtCedula.getText().toString().trim().length() <= 5 ){
            imprimirMensaje("Faltan digitos en el numero de documento");
            return false;
        }

        return true;
    }

    private String obtenerTipoDocumentoSeleccionado() {
        String tipoSeleccionado = null;
        if (radCC.isChecked()){
            tipoSeleccionado = "Cedula Ciudadania";
        }
        if (radCE.isChecked()){
            tipoSeleccionado = "Cedula Extranjeria";
        }
        if (radTI.isChecked()){
            tipoSeleccionado = "Tarjeta Identidad";
        }
        return tipoSeleccionado;
    }
}
