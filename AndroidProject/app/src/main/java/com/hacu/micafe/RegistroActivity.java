package com.hacu.micafe;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.Roles;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.Utilidades.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener{
    private Spinner comboRoles;
    private TextView lblFechaNac;
    private RadioButton radCC,radCE,radTI;
    private EditText txtNombre,txtCedula,txtCorreo,txtContrasenia,txtCelular,txtDireccion,txtDepartamento,txtMunicipio;
    private ArrayList<String> listaRolesSpi;//Lista para insertar en el spiner
    private ArrayList<Roles> listaRolesBd;//Lista con los datos de la BD
    private String rolSeleccionado;
    //RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    //Usado para el registro en la BD
    StringRequest stringRequest;


    private static final String DIALOG_DATE = "RegistroActivity.DateDialog";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        instanciarElementos();
        consultarRoles();
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
                Roles rol = null;
                listaRolesBd = new ArrayList<>();
                JSONArray json = response.optJSONArray("micafe");

                try{
                    for (int i = 0; i<json.length(); i++){//Recorre acada elemento del json
                        rol = new Roles();
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
        lblFechaNac = findViewById(R.id.reg_fechanacimiento);
        txtNombre = findViewById(R.id.reg_nombre);
        txtCedula = findViewById(R.id.reg_cedula);
        txtCorreo = findViewById(R.id.reg_contrasenia);
        txtContrasenia = findViewById(R.id.reg_contrasenia);
        txtCelular = findViewById(R.id.reg_celular);
        txtDireccion = findViewById(R.id.reg_direccion);
        txtDepartamento = findViewById(R.id.reg_departamento);
        txtMunicipio = findViewById(R.id.reg_municipio);
        radCC = findViewById(R.id.radCC);
        radCE = findViewById(R.id.radCE);
        radTI = findViewById(R.id.radTI);
    }

    //lista a mostrar en el combo
    private void obtenerLista() {
        listaRolesSpi =  new ArrayList<String>();
        listaRolesSpi.add("Seleccione");

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
        lblFechaNac.setText(formatDate(date));
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hireDate = sdf.format(date);
        return hireDate;
    }

    //Encargado de enviar los datos a la API para registrar a la BD con php
    public void registrarUsuario(View view) {
        //Se obtienen los datos a enviar
        final Usuarios usuarios = new Usuarios();
        usuarios.setNombre(txtNombre.getText().toString());
        usuarios.setTipodocumento(obtenerTipoDocumentoSeleccionado());
        usuarios.setCedula(txtCedula.getText().toString());
        usuarios.setCorreo(txtCorreo.getText().toString());
        usuarios.setContrasenia(txtContrasenia.getText().toString());
        usuarios.setCelular(txtCelular.getText().toString());
        usuarios.setFechanacimiento(lblFechaNac.getText().toString());
        usuarios.setDireccion(txtDireccion.getText().toString());
        usuarios.setDepartamento(txtDepartamento.getText().toString());
        usuarios.setMunicipio(txtMunicipio.getText().toString());

        String url = getString(R.string.ip_servidor)+"apimicafe.php";
        Log.i("TAG",url);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensaje(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros =  new HashMap<>();
                String opcion = "registrousuario";
                parametros.put("opcion",opcion);
                parametros.put("rol",rolSeleccionado);
                parametros.put("nombre",usuarios.getNombre());
                parametros.put("tipodocumento",usuarios.getTipodocumento());
                parametros.put("cedula",usuarios.getCedula());
                parametros.put("correo",usuarios.getCorreo());
                parametros.put("contrasenia",usuarios.getContrasenia());
                parametros.put("celular",usuarios.getContrasenia());
                parametros.put("fechanacimiento",usuarios.getFechanacimiento());
                parametros.put("direccion",usuarios.getDireccion());
                parametros.put("departamento",usuarios.getDepartamento());
                parametros.put("municipio",usuarios.getMunicipio());
                return parametros;
            }
        };
        //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
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
