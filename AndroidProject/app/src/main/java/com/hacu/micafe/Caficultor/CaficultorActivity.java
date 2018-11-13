package com.hacu.micafe.Caficultor;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorFincaFragment;
import com.hacu.micafe.Caficultor.Interfaces.IFragments;
import com.hacu.micafe.Modelo.Usuarios;
import com.hacu.micafe.R;

public class CaficultorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragments {

    TextView nav_nombre, nav_rol;
    ImageView nav_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caficultor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View cabeceraNav = navigationView.getHeaderView(0);//Obtiene la vista del nav donde se asignaran los datos
        nav_nombre = (TextView) cabeceraNav.findViewById(R.id.nav_nombre);
        nav_rol = (TextView) cabeceraNav.findViewById(R.id.nav_rol);
        nav_foto = (ImageView) cabeceraNav.findViewById(R.id.nav_foto);

        //Bundle para recibir el bojeto enviado por parametro
        Bundle objetoEnviado = getIntent().getExtras();
        Usuarios usuario = null;

        if (objetoEnviado!=null){
            usuario = (Usuarios) objetoEnviado.getSerializable("usuario");
            asignarDatosNav(usuario);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Asigna los datos al menu navbar
    private void asignarDatosNav(Usuarios usuario) {
        nav_nombre.setText(usuario.getNombre());//asigna el nombre del usuario a los datos de navbar

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

        if (id == R.id.nav_finca) {
            fragMostrar = new CaficultorFincaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        if (fragmentSeleccionado){
            //reemplaza el fragment en el contenedor que se indica
            getSupportFragmentManager().beginTransaction().replace(R.id.content_caficultor,fragMostrar).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void imprimirMensaje(String mensaje){
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
