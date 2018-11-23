package com.hacu.micafe.Recolector.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.hacu.micafe.Modelo.VolleySingleton;
import com.hacu.micafe.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class PerfilRecolectorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //PERMISOS
    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    //necesario para almacenar la foto desps de capturarla por la cam del dispositivo
    private static final String CARPETA_PRINCIPAL = "MiCafeApp/";//Directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//Carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path; //almacena la ruta de la imagen
    File fileImagen; //utilizado al momento de tomar la foto
    Bitmap bitmap; //utilizado al momento de tomar la foto

    private Button btnActualizar;
    private ImageView imagen;
    private TextView nombre,cedula,fechanacimiento;
    private EditText correo, celular, direccion, departamento, municipio;
    private StringRequest stringRequest;


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

        //Cuando presiona la imagen para poder cambiar la foto de perfil
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoOpciones();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPerfilRecolector();
            }
        });


        //Valida si tiene los Permisos
        if(solicitaPermisosVersionesSuperiores()){
            imagen.setEnabled(true);
        }else{
            imagen.setEnabled(false);
        }

        return vista;
    }

    private void actualizarPerfilRecolector() {
        String url = getString(R.string.ip_servidor)+"apimicafe.php?";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                switch (response){
                    case "Actualizo":
                        imprimirMensaje("Actualizacion completa");
                        break;
                    case "Error":
                        imprimirMensaje("No se actualizo, intente de nuevo. \n" + response);
                        break;
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"NO SE HA CONECTADO",Toast.LENGTH_SHORT).show();
                        //progreso.hide();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Se obtiene los datos a enviar
                if (bitmap == null){
                    bitmap = ((BitmapDrawable)imagen.getDrawable()).getBitmap();//asigna al bitmap la imagen que se encuentre en campoFoto
                    //bitmap = BitmapFactory.decodeResource()
                }
                String imagen = convertirImgString(bitmap); //Bitmap: objeto que contiene la fotografia
                //Retornamos parametros
                Map<String,String> parametros =  new HashMap<>();
                parametros.put("opcion","actualizarperfilrecolector");
                parametros.put("cedula",cedula.getText().toString());
                parametros.put("correo",correo.getText().toString());
                parametros.put("celular",celular.getText().toString());
                parametros.put("imagen",imagen);
                parametros.put("direccion",direccion.getText().toString());
                parametros.put("departamento",departamento.getText().toString());
                parametros.put("municipio",municipio.getText().toString());
                return parametros;
            }
        };
        //request.add(stringRequest);
        //limita el tiempo de actualizacion a dos segundos
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    /*
    Obtiene el bitmap que llega, lo comprime y lo transforma a base64 para
    poderlo enviar al servidor
     */
    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }

    //Muestras las opciones para cargar la imagen
    private void mostrarDialogoOpciones() {
        //tomar img de galeria del celular
        //opciones a mostrar en alertDialog
        final CharSequence[] opciones = {"Tomar Foto","Elegir De Galeria","Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una opcion");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto"))   {
                    //llamado a metodo para activar la camara
                    //Toast.makeText(getContext(),"cargar camara...",Toast.LENGTH_SHORT).show();
                    abrirCamara();
                }else{
                    //Si selecciona elegir de galeria la imagen
                    if (opciones[i].equals("Elegir De Galeria")){
                        //Permite buscar la imagen de la galeria
                        Intent miIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        miIntent.setType("image/");
                        startActivityForResult(miIntent.createChooser(miIntent,"Seleccione"),COD_SELECCIONA);
                        //Desps de esto se debe implementar el metodo onActivityResult
                        //para continuar
                    }else{
                        //si seleccion cancelar
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        //mostrar el alertDialog
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            //Evalua segun el permiso (constante enviada) si se toma la foto de la camara o de la galeria
            switch (requestCode){
                case COD_FOTO://Toma fotografia
                    MediaScannerConnection.scanFile(getContext(), new String[]{path}, null
                            , new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("Path",path);
                                }
                            });

                    bitmap = BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    break;
                case COD_SELECCIONA://selecciono de la galeria
                    Uri miPath= data.getData();
                    imagen.setImageURI(miPath);

                    //asignamos la imagen al bitmap para poder ser enviado al servidor
                    try{
                        bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),miPath);
                        imagen.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
            }
            bitmap =  redimensionarImagen(bitmap,600,400);
        }catch (Exception e){
            Toast.makeText(getContext(),"Imagen no seleccionada \n"+ e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, int anchoNuevo, int altoNuevo) {
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();
        if (ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto = altoNuevo/alto;

            Matrix matrix = new Matrix();//permite manipular los datos
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);
        }else {
            return bitmap;
        }
    }

    private void abrirCamara() {
        //Indicamos donde vamos a almacenar la imagen (se envia la ruta)
        File miFile = new File(Environment.getExternalStorageDirectory(),DIRECTORIO_IMAGEN);
        //permite valida si el directorio fue creado en la memoria
        boolean esCreada = miFile.exists();

        if (esCreada==false){
            //crea el directorio
            esCreada = miFile.mkdirs();
        }

        if (esCreada){
            //construir la imagen
            Long consecutivo = System.currentTimeMillis()/1000;//toma el momento(FECHA Y HORA) que fue creada
            String nombre = consecutivo.toString()+".jpg";//asigna el tipo de img

            path = Environment.getExternalStorageDirectory()+File.separator+DIRECTORIO_IMAGEN+
                    File.separator+nombre;//indicamos la ruta de almacenamiento
            fileImagen = new File(path);//SE CONSTRUYE EL ARCHIVO


            //Se ejecuta la camara
            Intent miIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            /*
            IMPORTANTE:
            Antes se debe agregar el contenido de la etiqueta PROVIDER en el manifest
            Se crea el archivo xml solicitado y se agrega el contenido necesario y listo
             */

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){//para version mayor 7.0 de android
                String authorities = getContext().getPackageName()+".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(),authorities,fileImagen);
                miIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            }
            else{
                miIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileImagen));
            }

            startActivityForResult(miIntent,COD_FOTO);
        }
    }

    private boolean solicitaPermisosVersionesSuperiores() {
        //si la version es menor a 6.0
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true; //Usa los permisos del manifest
        }

        //Si el permiso de la camara y memoria externa estan activados

        //ASI SE CONSULTA LOS PERMISOS EN UN FRAGMENT
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        //si ninguno de arriba se cumple, Debe solicitar permisos para camara y escritura
        if ((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},MIS_PERMISOS);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivado");
        dialogo.setMessage("Debe aceptar permisos para que la app funcione correctamente");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MIS_PERMISOS){
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED ){
                imagen.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"Si","No"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("¿Desea configurar los permisos de forma manual?");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")){
                    //Carga configuraciones del dispotivo
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else {
                    //si no, cerramos dialogo
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();//Mostrar alertDialog
    }

    private void cargarWebServiceImagen(String urlImagen) {
        urlImagen = urlImagen.replace(" ","%20");
        Log.i("urlImagen",urlImagen);
        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imagen.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Imagen de perfil no encontrada",Toast.LENGTH_SHORT).show();
                    }
                });
        //request.add(imageRequest);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(imageRequest);
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
        cargarWebServiceImagen(getString(R.string.ip_servidor)+getSession().getString("urlimagen","null"));

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
        btnActualizar = vista.findViewById(R.id.perrec_btnactualizar);
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
