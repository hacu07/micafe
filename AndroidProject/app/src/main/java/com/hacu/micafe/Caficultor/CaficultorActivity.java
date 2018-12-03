package com.hacu.micafe.Caficultor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorFincaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorOfertaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.InicioCaficultorFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.PerfilCaficultorFragment;
import com.hacu.micafe.Caficultor.Interfaces.IFragments;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.R;
import com.hacu.micafe.SplashActivity;

import java.util.Locale;

public class CaficultorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragments,
        TextToSpeech.OnInitListener{

    private TextToSpeech tts; //convierte texto a voz
    TextView nav_nombre, nav_rol;
    ImageView nav_foto;
    Usuarios usuario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caficultor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tts = new TextToSpeech(getApplicationContext(),this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View cabeceraNav = navigationView.getHeaderView(0);//Obtiene la vista del nav donde se asignaran los datos
        nav_nombre = (TextView) cabeceraNav.findViewById(R.id.nav_nombre);
        nav_rol = (TextView) cabeceraNav.findViewById(R.id.nav_rol);
        nav_foto = (ImageView) cabeceraNav.findViewById(R.id.nav_foto);

        asignarDatosNav();
        //Bundle para recibir el bojeto enviado por parametro
        Bundle objetoEnviado = getIntent().getExtras();
        if (objetoEnviado!=null){
            usuario = (Usuarios) objetoEnviado.getSerializable("usuario");
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //carga fragment de inicio
        // AL INICIAR LA APP CARGA EL FRAGMENT DE BIENVENIDO
        Fragment fragment =  new InicioCaficultorFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragment).commit();
    }

    //Asigna los datos al menu navbar
    private void asignarDatosNav() {

        nav_nombre.setText(getSession().getString("nombre","micafe"));//asigna el nombre del usuario a los datos de navbar
        if (getSession().getString("urlimagen","null") != null){
            //ASIGNACION DE FOTO DE PERFIL CON LIBRERIA GLIDE (IMPORTADA EN APP)
            Glide.with(this).load(getString(R.string.ip_servidor)+getSession().getString("urlimagen","null")).into(nav_foto);
        }

        //Segun el IdRol muestra el rol
        switch (getSession().getInt("idrol",0)){
            case 2:
                nav_rol.setText("Caficultor");
                break;
            case 3:
                nav_rol.setText("Recolector");
                break;
            case 4:
                nav_rol.setText("Comerciante");
                break;
        }

        //cargar imagen en foto de perfil

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caficultor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salir) {
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Objeto para iniciar los fragments
        Fragment fragMostrar = null;
        boolean fragmentSeleccionado =  false;

        if (id == R.id.nav_finca) {
            fragMostrar = new CaficultorFincaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_ofertas) {
            fragMostrar = new CaficultorOfertaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_locales) {

        } else if (id == R.id.nav_perfil_caficultor) {
            fragMostrar = new PerfilCaficultorFragment();
            fragmentSeleccionado = true;
        }



        if (fragmentSeleccionado){
            //reemplaza el fragment en el contenedor que se indica
            getSupportFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragMostrar).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cerrarSesion() {
        //Eliminar SharedPreferences
        getSession().edit().clear().commit();
        Intent intent = new Intent(CaficultorActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){

            int result = tts.setLanguage(Locale.getDefault());//Toma el lenguaje predeterminado por el emulador del dispositivo
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                reproducirAudio("Bienvenido a mi café " + getSession().getString("nombre"," "));
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private SharedPreferences getSession(){
        SharedPreferences preferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return preferences;
    }

    private void reproducirAudio(String texto) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null,"id1");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
