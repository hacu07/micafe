package com.hacu.micafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hacu.micafe.Caficultor.CaficultorActivity;
import com.hacu.micafe.Recolector.RecolectorActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //HILO QUE EJECUTA PARA MOSTRAR EL SPLASH
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                Intent intent = null;
                Log.i("TAG",String.valueOf(getSession().getInt("login",0)));
                if (getSession().getInt("login",0) == 1){//si hay una sesion iniciada
                    switch (getSession().getInt("idrol",0)){
                        case 2://Id Rol de caficultor
                            intent = new Intent(SplashActivity.this, CaficultorActivity.class);
                            break;
                        case 3://Id rol de recolector
                            intent = new Intent(SplashActivity.this, RecolectorActivity.class);
                            break;
                    }
                }else{//no hay sesion iniciada
                    //envia a pantalla de login
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);//ejecuta el intent con la actividad seleccionada
                finish();//elimina la actividad actual
            }
        },2000);

    }

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }
}
