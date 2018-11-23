package com.hacu.micafe.Caficultor.Adaptadores;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hacu.micafe.LoginActivity;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListaPostulantesAdapter extends RecyclerView.Adapter<ListaPostulantesAdapter.ListaPostulantesHolder>
                                    implements View.OnClickListener{

    List<Usuarios> listaUsuarios;
    Context context;
    String ip;

    private View.OnClickListener listener;

    public ListaPostulantesAdapter(List<Usuarios> listaUsuarios, Context context, String ip) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
        this.ip = ip;
    }

    @NonNull
    @Override
    public ListaPostulantesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_postulados_ofertas_caficultor,parent, false);
        RecyclerView.LayoutParams layoutParams =  new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        vista.setOnClickListener(this);//Escucha de evento
        return new ListaPostulantesHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPostulantesHolder holder, int position) {
        holder.cedula.setText(listaUsuarios.get(position).getCedula());
        holder.nombre.setText(listaUsuarios.get(position).getNombre());
        holder.edad.setText(calcularEdad(listaUsuarios.get(position).getFechanacimiento()));

        //carga la imagen
        if (listaUsuarios.get(position).getUrlimagen()!=null){

            //cargarImagenWebService(listDestinos.get(position).getRuta_imagen(),holder);

            //USO DE LA LIBRERIA GLIDE PARA CARGAR LAS IMAGENES
            Glide.with(context).load(ip+listaUsuarios.get(position).getUrlimagen()).into(holder.imagen);
        }else {
            holder.imagen.setImageResource(R.drawable.logo);

        }
    }

    private String calcularEdad(String fechanacimiento) {
        Calendar calendar = Calendar.getInstance();//obtiene la fecha del dispositivo
        SimpleDateFormat formato =  new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = null;
        int edad = 0;
        try {
            Date fecNacDate = formato.parse(fechanacimiento);
            fechaActual = formato.parse(formato.format(calendar.getTime()));
            Log.i("date",String.valueOf(fechaActual.getDate()));

            Log.i("date","Fecha Actual anio: "+String.valueOf(fechaActual.getYear()));
            Log.i("date","Fecha Nac Anio: "+String.valueOf(fecNacDate.getYear()));
            edad = ((fechaActual.getYear()) - (fecNacDate.getYear()));

            if ((fechaActual.getMonth()<fecNacDate.getMonth()) ||
                    (fechaActual.getMonth() == fecNacDate.getMonth() && fechaActual.getDate()<fecNacDate.getDate())){
                edad --;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(edad);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
        }
    }

    public class ListaPostulantesHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView cedula,nombre,edad,calificacion;

        public ListaPostulantesHolder(View itemView) {
            super(itemView);
            cedula = itemView.findViewById(R.id.lisposofe_cedula);
            nombre = itemView.findViewById(R.id.lisposofe_nombre);
            edad = itemView.findViewById(R.id.lisposofe_edad);
            calificacion = itemView.findViewById(R.id.lisposofe_calificacion);
            imagen = itemView.findViewById(R.id.lisposofe_imagen);
        }
    }
}
