package com.hacu.micafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsuario,txtContrasenia;
    //RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUsuario = findViewById(R.id.log_cedula);
        txtContrasenia = findViewById(R.id.log_contrasenia);
    }

    public void mostrarRegistro(View view) {
        Intent  navegarRegistro = new Intent(LoginActivity.this,RegistroActivity.class);
        startActivity(navegarRegistro);
    }


    public void iniciarSesion(View view) {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=iniciarsesion&usuario="+txtUsuario.getText().toString()+
                "&contrasenia="+txtContrasenia.getText().toString();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Usuarios usuario = null;
                JSONArray json = response.optJSONArray("micafe");

                try{
                    for (int i = 0; i<json.length(); i++){//Recorre acada elemento del json
                        usuario = new Usuarios();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);
                        imprimirMensaje("Bienvenido: "+jsonObject.optString("nombre"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    imprimirMensaje("Usuario no encontrado \n Verifique su usuario y contraseña.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirMensaje("Error en webservice");
            }
        });
        //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
        //request.add(stringRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
    }
}
