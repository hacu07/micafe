package com.hacu.micafe.Caficultor.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Finca;
import com.hacu.micafe.R;

import java.util.ArrayList;

/**
 * Created by hacu1 on 03/12/2018.
 */

public class ListaFincasAdapter extends RecyclerView.Adapter<ListaFincasAdapter.ListaFincasHolder> implements View.OnClickListener {

    private ArrayList<Finca> listaFincas;
    private View.OnClickListener listener;

    public ListaFincasAdapter(ArrayList<Finca> listaFincas) {
        this.listaFincas = listaFincas;
    }

    @NonNull
    @Override
    public ListaFincasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_fincas_caficultor,parent,false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        vista.setOnClickListener(this);//Escucha de evento
        return new ListaFincasHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaFincasHolder holder, int position) {
        holder.codigo.setText(String.valueOf(listaFincas.get(position).getId()));
        holder.nombre.setText(listaFincas.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return listaFincas.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener!=null){
            listener.onClick(view);
        }
    }

    public class ListaFincasHolder extends RecyclerView.ViewHolder {
        private TextView codigo,nombre;
        public ListaFincasHolder(View vista) {
            super(vista);
            codigo = vista.findViewById(R.id.lisfincaf_id);
            nombre = vista.findViewById(R.id.lisfincaf_nombre);
        }
    }
}
