package com.hacu.micafe.Caficultor.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Comentario;
import com.hacu.micafe.R;

import java.util.ArrayList;

/**
 * Created by hacu1 on 30/11/2018.
 */

public class ListadoCalificacionPostuladosAdapter extends RecyclerView.Adapter<ListadoCalificacionPostuladosAdapter.ListadoCalificacionHolder> {

    private ArrayList<Comentario> listaComentarios;

    public ListadoCalificacionPostuladosAdapter(ArrayList<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    @NonNull
    @Override
    public ListadoCalificacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_calificacion_postulado,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListadoCalificacionHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoCalificacionHolder holder, int position) {
        holder.nombre.setText(listaComentarios.get(position).getNombreAdmin());
        holder.telefono.setText(listaComentarios.get(position).getCelular());
        holder.califcacion.setText(String.valueOf(listaComentarios.get(position).getCalificacion()));
        holder.comentario.setText(String.valueOf(listaComentarios.get(position).getComentario()));
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class ListadoCalificacionHolder extends RecyclerView.ViewHolder {
        TextView nombre,telefono,califcacion,comentario;
        public ListadoCalificacionHolder(View vista) {
            super(vista);
            nombre = vista.findViewById(R.id.liscalpos_nombre);
            telefono = vista.findViewById(R.id.liscalpos_celular);
            califcacion = vista.findViewById(R.id.liscalpos_calificacion);
            comentario = vista.findViewById(R.id.liscalpos_comentario);
        }
    }
}
