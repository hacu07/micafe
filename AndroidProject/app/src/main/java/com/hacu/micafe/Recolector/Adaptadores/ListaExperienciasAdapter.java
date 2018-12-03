package com.hacu.micafe.Recolector.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Experiencia;
import com.hacu.micafe.R;

import java.util.ArrayList;

/**
 * Created by hacu1 on 03/12/2018.
 */

public class ListaExperienciasAdapter extends RecyclerView.Adapter<ListaExperienciasAdapter.ListaExperienciaHoder> {
    ArrayList<Experiencia> listaExperiencias;

    public ListaExperienciasAdapter(ArrayList<Experiencia> listaExperiencias) {
        this.listaExperiencias = listaExperiencias;
    }

    @NonNull
    @Override
    public ListaExperienciaHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_experiencias_recolector,parent,false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListaExperienciaHoder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaExperienciaHoder holder, int position) {
        holder.empresa.setText(listaExperiencias.get(position).getEmpresa());
        holder.cargo.setText(listaExperiencias.get(position).getCargo());
        holder.funciones.setText(listaExperiencias.get(position).getFunciones());
        holder.tiempo.setText(String.valueOf(listaExperiencias.get(position).getTiempo()));
        holder.supervisor.setText(listaExperiencias.get(position).getSupervisor());
        holder.contactosupervisor.setText(listaExperiencias.get(position).getContactosupervisor());
    }

    @Override
    public int getItemCount() {
        return listaExperiencias.size();
    }

    public class ListaExperienciaHoder extends RecyclerView.ViewHolder {
        private TextView empresa,cargo,funciones,tiempo,supervisor,contactosupervisor;
        public ListaExperienciaHoder(View vista) {
            super(vista);
            empresa = vista.findViewById(R.id.lisexprec_empresa);
            cargo = vista.findViewById(R.id.lisexprec_cargo);
            funciones = vista.findViewById(R.id.lisexprec_funciones);
            tiempo = vista.findViewById(R.id.lisexprec_tiempo);
            supervisor = vista.findViewById(R.id.lisexprec_supervisor);
            contactosupervisor = vista.findViewById(R.id.lisexprec_contactosupervisor);
        }
    }
}
