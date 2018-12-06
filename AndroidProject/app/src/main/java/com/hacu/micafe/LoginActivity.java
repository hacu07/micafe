package com.hacu.micafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.micafe.Caficultor.CaficultorActivity;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.Recolector.RecolectorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private EditText txtUsuario,txtContrasenia;
    //RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private TextToSpeech tts; //convierte texto a voz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tts = new TextToSpeech(getApplicationContext(),this);
        txtUsuario = findViewById(R.id.log_cedula);
        txtContrasenia = findViewById(R.id.log_contrasenia);
    }

    public void mostrarRegistro(View view) {
        Intent  navegarRegistro = new Intent(LoginActivity.this,RegistroActivity.class);
        startActivity(navegarRegistro);
    }


    public void iniciarSesion(View view) {
        if (validarCamposRegistro()){
            String url = getString(R.string.ip_servidor)+"apimicafe.php?opcion=iniciarsesion&usuario="+txtUsuario.getText().toString().trim()+
                    "&contrasenia="+txtContrasenia.getText().toString().trim();
            Log.i("TAG",url);
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

                            //Asignar Valores a Objeto para enviar por bundle
                            usuario.setId(jsonObject.optInt("id"));
                            usuario.setNombre(jsonObject.optString("nombre"));
                            usuario.setCorreo(jsonObject.optString("correo"));
                            usuario.setContrasenia(jsonObject.optString("contrasenia"));
                            usuario.setTipodocumento(jsonObject.optString("tipodocumento"));
                            usuario.setCedula(jsonObject.optString("cedula"));
                            usuario.setCelular(jsonObject.optString("celular"));
                            usuario.setFechanacimiento(jsonObject.optString("fechanacimiento"));
                            usuario.setDireccion(jsonObject.optString("direccion"));
                            usuario.setDepartamento(jsonObject.optString("departamento"));
                            usuario.setMunicipio(jsonObject.optString("municipio"));
                            usuario.setUrlimagen(jsonObject.optString("urlimagen"));
                            usuario.setIdrol(jsonObject.optInt("idrol"));

                            ejecutarMensajes(usuario);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                        imprimirMensaje("Usuario no encontrado \n Verifique su usuario y contraseña.");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    imprimirMensaje("Usuario no encontrado");
                }
            });
            //IMPORTANTE ESTA LINEA PARA EJECUTAR EL WEBSERVICE
            //request.add(stringRequest);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void ejecutarMensajes(final Usuarios usuario) {
        imprimirMensaje("Hola "+ usuario.getNombre());
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.clic);
        mp.start();
        reproducirAudio("Bienvenido a mi cafe");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarModulo(usuario);
            }
        },2000);
    }

    /* Segun el rol del usuario que ha iniciado sesion se llevara a su modulo correspondiente */
    private void mostrarModulo(Usuarios usuario) {
        Intent miIntent = null;

        switch (usuario.getIdrol()){
            case 2://intent para Caficultor
                miIntent = new Intent(LoginActivity.this, CaficultorActivity.class);
                break;

            case 3://intent para Recolector
                miIntent = new Intent(LoginActivity.this, RecolectorActivity.class);
                break;
            case 4://intent para Comerciante
                break;
        }

        //Guardamos los datos del usuario que ha iniciado sesion en un sharedpreferences para usar desde otra clase de la app
        guardarDatosUsuarioSesion(usuario);

        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario",usuario);
        miIntent.putExtras(bundle);
        startActivity(miIntent);



        //obtiene bundle para enviar parametros
        //inicia actividad

        finish();
    }

    //Guarda los datos en sharedpreferences para acceder desde alli por otra parte del codigo de la app
    private void guardarDatosUsuarioSesion(Usuarios usuario) {
        //MODO PRIVADO PARA SOLO ACCEDER DESDE LA APP -RECOMENDADO
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        //se asigna los valores usando el editor
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("login",1);//Si es 1 es porque hay una sesion iniciada
        editor.putInt("id",usuario.getId());
        editor.putString("nombre",usuario.getNombre());
        editor.putString("correo",usuario.getCorreo());
        editor.putString("tipodocumento",usuario.getTipodocumento());
        editor.putString("cedula",usuario.getCedula());//
        editor.putString("celular",usuario.getCelular());//
        editor.putString("fechanacimiento",usuario.getFechanacimiento());//
        editor.putString("direccion",usuario.getDireccion());//
        editor.putString("departamento",usuario.getDepartamento());//
        editor.putString("municipio",usuario.getMunicipio());//
        editor.putString("urlimagen",usuario.getUrlimagen());//
        editor.putInt("idrol",usuario.getIdrol());//
        editor.commit();//crea archivo y
    }

    //Retorna un boolean true si todo esta bien, si no devuelve un false
    private Boolean validarCamposRegistro(){
        if (txtContrasenia.getText().toString().isEmpty() || txtUsuario.getText().toString().isEmpty()){
            imprimirMensaje("Debe completar los campos usuario y contraseña para iniciar sesión");
            return false;
        }

        if (txtContrasenia.getText().toString().trim().length() <= 5 ){
            imprimirMensaje("La contraseña debe tener minimo 6 digitos");
            return false;
        }

        if (txtUsuario.getText().toString().trim().length() <= 5 ){
            imprimirMensaje("Faltan digitos en el numero de documento");
            return false;
        }

        return true;
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){

            int result = tts.setLanguage(Locale.getDefault());//Toma el lenguaje predeterminado por el emulador del dispositivo
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //reproducirAudio("Bienvenido a mi café ");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void reproducirAudio(String texto) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null,"id1");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onStop();

    }
}
