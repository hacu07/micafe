package com.hacu.micafe.Recolector.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Caficultor.Adaptadores.ListaOfertasAdapter;
import com.hacu.micafe.Modelo.Oferta;
import com.hacu.micafe.R;

import java.util.List;

/**
 * Created by hacu1 on 18/11/2018.
 */

public class ListaOfertasRecAdapter extends RecyclerView.Adapter<ListaOfertasRecAdapter.ListaOfertasRecHolder> implements View.OnClickListener {
    @NonNull
    List<Oferta> listOfertas;
    public View.OnClickListener listener;

    public ListaOfertasRecAdapter(@NonNull List<Oferta> listOfertas) {
        this.listOfertas = listOfertas;
    }

    @Override
    public ListaOfertasRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ofertas_recolector,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);

        vista.setOnClickListener(this);//Escucha de evento

        return new ListaOfertasRecHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaOfertasRecHolder holder, int position) {
        holder.id.setText(String.valueOf(listOfertas.get(position).getId()));
        holder.finca.setText(listOfertas.get(position).getServicios());//se toma servicios para almacenar el nombre de la finca
        holder.fechainicio.setText(listOfertas.get(position).getFechainicio());//cambiar a fecha de inicio
        holder.vacantes.setText(String.valueOf(listOfertas.get(position).getVacantes()));
    }

    @Override
    public int getItemCount() {
        return listOfertas.size();
    }

    @Override
    public void onClick(View view) {
        if (listener!=null){
            listener.onClick(view);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    public class ListaOfertasRecHolder extends RecyclerView.ViewHolder {

        TextView id,fechainicio,vacantes,finca;

        public ListaOfertasRecHolder(View vista) {
            super(vista);

            id = vista.findViewById(R.id.lisoferec_id);
            finca = vista.findViewById(R.id.lisoferec_finca);
            fechainicio = vista.findViewById(R.id.lisoferec_fechainicio);
            vacantes = vista.findViewById(R.id.lisoferec_vacantes);
        }
    }
}
