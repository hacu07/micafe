package com.hacu.micafe.Utilidades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.hacu.micafe.R;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by hacu1 on 06/11/2018.
 */

public class DatePickerFragment extends DialogFragment {
    private DatePicker datePicker;

    public interface DateDialogListener{
        void onFinishDialog(Date date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View vista = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);//Se infla el activity a mostrar el alert
        datePicker = vista.findViewById(R.id.dialog_date_picker);
        return new AlertDialog.Builder(getActivity())
                .setView(vista)//asigna la vista a mostrar en el alertDialog
                .setTitle("FECHA DE NACIMIENTO")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int anio = datePicker.getYear();
                        int mes = datePicker.getMonth();
                        int dia = datePicker.getDayOfMonth();
                        Date fecha = new GregorianCalendar(anio,mes,dia).getTime();
                        DateDialogListener activity = (DateDialogListener) getActivity();
                        activity.onFinishDialog(fecha);
                        dismiss();
                    }
                })
                .create();
    }
}
