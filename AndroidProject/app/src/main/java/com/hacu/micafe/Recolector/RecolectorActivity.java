package com.hacu.micafe.Recolector;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.R;
import com.hacu.micafe.Recolector.Fragments.ExperienciaRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.ListaTrabajoFragment;
import com.hacu.micafe.Recolector.Fragments.OfertasRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.PerfilRecolectorFragment;
import com.hacu.micafe.Recolector.Interfaces.IFragments;

public class RecolectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragments {

    TextView nav_nombre, nav_rol;
    ImageView nav_foto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recolector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View cabeceraNav = navigationView.getHeaderView(0);//Obtiene la vista del nav donde se asignaran los datos
        nav_nombre = (TextView) cabeceraNav.findViewById(R.id.navrec_nombre);
        nav_rol = (TextView) cabeceraNav.findViewById(R.id.navrec_rol);
        nav_foto = (ImageView) cabeceraNav.findViewById(R.id.navrec_foto);

        //Bundle para recibir el bojeto enviado por parametro
        Bundle objetoEnviado = getIntent().getExtras();
        Usuarios usuario = null;

        if (objetoEnviado!=null){
            usuario = (Usuarios) objetoEnviado.getSerializable("usuario");
            asignarDatosNav(usuario);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void asignarDatosNav(Usuarios usuario) {
        nav_nombre.setText(usuario.getNombre());//asigna el nombre del usuario a los datos de navbar

        if (usuario.getUrlimagen() != null){
            //ASIGNACION DE FOTO DE PERFIL CON LIBRERIA GLIDE (IMPORTADA EN APP)
            Glide.with(this).load(getString(R.string.ip_servidor)+usuario.getUrlimagen()).into(nav_foto);
        }

        //Segun el IdRol muestra el rol
        switch (usuario.getIdrol()){
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
        getMenuInflater().inflate(R.menu.recolector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        if (id == R.id.nav_camera) {
            fragMostrar = new OfertasRecolectorFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_perfil_recolector) {
            fragMostrar = new PerfilRecolectorFragment();
            fragmentSeleccionado = true;
        }else if (id == R.id.nav_experiencia) {
            fragMostrar = new ExperienciaRecolectorFragment();
            fragmentSeleccionado = true;
        }else if (id == R.id.nav_trabajo) {
            fragMostrar = new ListaTrabajoFragment();
            fragmentSeleccionado = true;
        }

        if (fragmentSeleccionado){
            //reemplaza el fragment en el contenedor que se indica
            getSupportFragmentManager().beginTransaction().replace(R.id.content_recolector,fragMostrar).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
