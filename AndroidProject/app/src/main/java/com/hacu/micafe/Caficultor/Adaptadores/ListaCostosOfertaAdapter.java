package com.hacu.micafe.Caficultor.Adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hacu.micafe.Modelo.Costo;
import com.hacu.micafe.R;

import java.util.ArrayList;

/**
 * Created by hacu1 on 02/12/2018.
 */

public class ListaCostosOfertaAdapter extends RecyclerView.Adapter<ListaCostosOfertaAdapter.ListaOfertasHolder> {

    private ArrayList<Costo> listaCostos;

    public ListaCostosOfertaAdapter(ArrayList<Costo> listaCostos) {
        this.listaCostos = listaCostos;
    }

    @NonNull
    @Override
    public ListaOfertasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_costos_oferta_caf,parent,false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListaOfertasHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaOfertasHolder holder, int position) {
        holder.titulo.setText(listaCostos.get(position).getTitulo());
        holder.valor.setText(String.valueOf(listaCostos.get(position).getValor()));
        holder.descripcion.setText(listaCostos.get(position).getDescripcion());
        holder.fecha.setText(listaCostos.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return listaCostos.size();
    }

    public class ListaOfertasHolder extends RecyclerView.ViewHolder {

        private TextView titulo,valor,descripcion,fecha;

        public ListaOfertasHolder(View vista) {
            super(vista);
            titulo =  vista.findViewById(R.id.liscosofe_titulo);
            valor =  vista.findViewById(R.id.liscosofe_valor);
            descripcion =  vista.findViewById(R.id.liscosofe_descripcion);
            fecha =  vista.findViewById(R.id.liscosofe_fecha);
        }
    }
}
