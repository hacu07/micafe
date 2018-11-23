package com.hacu.micafe.Recolector.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hacu.micafe.R;


public class PerfilRecolectorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView imagen;
    private TextView nombre,cedula,fechanacimiento;
    private EditText correo, celular, direccion, departamento, municipio;


    public PerfilRecolectorFragment() {
        // Required empty public constructor
    }


    public static PerfilRecolectorFragment newInstance(String param1, String param2) {
        PerfilRecolectorFragment fragment = new PerfilRecolectorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_perfil_recolector, container, false);
        instanciarElementos(vista);
        asignarDatosUsuario();
        return vista;
    }

    private void asignarDatosUsuario() {
        nombre.setText(getSession().getString("nombre","No se logro cargar la información"));
        correo.setText(getSession().getString("correo","No se logro cargar la información"));
        cedula.setText(getSession().getString("cedula","No se logro cargar la información"));
        celular.setText(getSession().getString("celular","No se logro cargar la información"));
        fechanacimiento.setText(getSession().getString("fechanacimiento","No se logro cargar la información"));
        direccion.setText(getSession().getString("direccion","No se logro cargar la información"));
        departamento.setText(getSession().getString("departamento","No se logro cargar la información"));
        municipio.setText(getSession().getString("municipio","No se logro cargar la información"));
    }

    private void instanciarElementos(View vista) {
        imagen = vista.findViewById(R.id.perrec_img);
        nombre = vista.findViewById(R.id.perrec_nombre);
        correo = vista.findViewById(R.id.perrec_correo);
        cedula = vista.findViewById(R.id.perrec_cedula);
        celular = vista.findViewById(R.id.perrec_celular);
        fechanacimiento = vista.findViewById(R.id.perrec_fechanacimiento);
        direccion = vista.findViewById(R.id.perrec_direccion);
        departamento = vista.findViewById(R.id.perrec_departamento);
        municipio = vista.findViewById(R.id.perrec_municipio);
    }

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
