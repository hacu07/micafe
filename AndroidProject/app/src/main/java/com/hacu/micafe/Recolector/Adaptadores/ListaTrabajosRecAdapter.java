package com.hacu.micafe.Recolector.Adaptadores;

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
 * Created by hacu1 on 26/11/2018.
 */

public class ListaTrabajosRecAdapter extends RecyclerView.Adapter<ListaTrabajosRecAdapter.ListaTrabajosHolder>
                                    implements View.OnClickListener{

    List<Oferta> listTrabajos;
    public View.OnClickListener listener;

    public ListaTrabajosRecAdapter(List<Oferta> listTrabajos) {
        this.listTrabajos = listTrabajos;
    }

    @NonNull
    @Override
    public ListaTrabajosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_trabajos_recolector,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);

        vista.setOnClickListener(this);//Escucha de evento

        return new ListaTrabajosRecAdapter.ListaTrabajosHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaTrabajosHolder holder, int position) {
        holder.id.setText(String.valueOf(listTrabajos.get(position).getId()));
        holder.finca.setText(listTrabajos.get(position).getServicios());//se toma servicios para almacenar el nombre de la finca
        holder.fechainicio.setText(listTrabajos.get(position).getFechainicio());//cambiar a fecha de inicio
        holder.vacantes.setText(String.valueOf(listTrabajos.get(position).getVacantes()));
    }

    @Override
    public int getItemCount() {
        return listTrabajos.size();
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

    public class ListaTrabajosHolder extends RecyclerView.ViewHolder {

        TextView id,fechainicio,vacantes,finca;

        public ListaTrabajosHolder(View vista) {
            super(vista);

            id = vista.findViewById(R.id.listrarec_id);
            finca = vista.findViewById(R.id.listrarec_finca);
            fechainicio = vista.findViewById(R.id.listrarec_fechainicio);
            vacantes = vista.findViewById(R.id.listrarec_vacantes);
        }
    }
}
