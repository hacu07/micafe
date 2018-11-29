package com.hacu.micafe.Caficultor.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Pesada;
import com.hacu.micafe.R;

import java.util.ArrayList;

/**
 * Created by hacu1 on 27/11/2018.
 */

public class ListadoPesadasAdapter extends RecyclerView.Adapter<ListadoPesadasAdapter.ListadoPesadasHolder> {

    ArrayList<Pesada> listPesadas;

    public ListadoPesadasAdapter(ArrayList<Pesada> listPesadas) {
        this.listPesadas = listPesadas;
    }

    @NonNull
    @Override
    public ListadoPesadasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pesadas_aceptado_caf,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListadoPesadasHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoPesadasHolder holder, int position) {
        holder.fecha.setText(listPesadas.get(position).getFecha());
        holder.kilos.setText(String.valueOf(listPesadas.get(position).getKilos()));
        holder.valorKilo.setText(String.valueOf(listPesadas.get(position).getIdoferta()));
        holder.valorPesada.setText(String.valueOf(listPesadas.get(position).getIdrecolector()));
        Log.i("url", String.valueOf(listPesadas.get(position).getKilos()));
    }

    @Override
    public int getItemCount() {
        return listPesadas.size();
    }

    public class ListadoPesadasHolder extends RecyclerView.ViewHolder {

        TextView fecha,kilos,valorKilo,valorPesada;

        public ListadoPesadasHolder(View vista) {
            super(vista);
            fecha = vista.findViewById(R.id.lispesace_fecha);
            kilos = vista.findViewById(R.id.lispesace_kilos);
            valorKilo = vista.findViewById(R.id.lispesace_valorkilo);
            valorPesada = vista.findViewById(R.id.lispesace_total);
        }
    }
}
