package com.hacu.micafe.Caficultor.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.R;

import java.util.List;

/**
 * Created by hacu1 on 15/11/2018.
 */

public class ListaOfertasAdapter extends RecyclerView.Adapter<ListaOfertasAdapter.ListaOfertasHolder> {

    List<Oferta> listOfertas;

    public ListaOfertasAdapter(List<Oferta> listOfertas) {
        this.listOfertas = listOfertas;
    }

    @NonNull
    @Override
    public ListaOfertasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ofetas_caficultor,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListaOfertasHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaOfertasHolder holder, int position) {
        holder.id.setText(String.valueOf(listOfertas.get(position).getId()));
        holder.fechainicio.setText(listOfertas.get(position).getFechacreacion());//cambiar a fecha de inicio
        holder.vacantes.setText(String.valueOf(listOfertas.get(position).getVacantes()));
    }

    @Override
    public int getItemCount() {
        return listOfertas.size();
    }

    public class ListaOfertasHolder extends RecyclerView.ViewHolder {

        TextView id,fechainicio,vacantes;

        public ListaOfertasHolder(View vista) {
            super(vista);
            id = vista.findViewById(R.id.lisofe_id);
            fechainicio = vista.findViewById(R.id.lisofe_fechainicio);
            vacantes = vista.findViewById(R.id.lisofe_vacantes);
        }
    }
}
