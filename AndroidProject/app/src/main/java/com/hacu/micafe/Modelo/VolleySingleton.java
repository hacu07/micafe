package com.hacu.micafe.Modelo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hacu1 on 04/11/2018.
 */

public class VolleySingleton {
    private static VolleySingleton intanciaVolley;//INSTANCIA UNICA
    private RequestQueue request; //OBJETO A REFERENCIAR
    private static Context contexto; //CONTEXTO DE LA APP

    private VolleySingleton(Context context) {
        contexto = context;
        request = getRequestQueue();
    }


    public static synchronized VolleySingleton getInstanciaVolley(Context context) {
        if (intanciaVolley == null) {
            intanciaVolley = new VolleySingleton(context);
        }

        return intanciaVolley;
    }

    public RequestQueue getRequestQueue() {
        if (request == null) {
            request = Volley.newRequestQueue(contexto.getApplicationContext());
        }

        return request;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
