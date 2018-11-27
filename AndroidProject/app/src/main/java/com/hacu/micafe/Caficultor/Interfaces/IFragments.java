package com.hacu.micafe.Caficultor.Interfaces;

import com.hacu.micafe.Caficultor.FragmentsCaf.AceptadosOfertaCafFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorFincaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorOfertaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.DetalleAceptadoOfertaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.DetalleOfertaCafFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.DetallePostuladoOfertaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.PerfilCaficultorFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.PostuladosOfertasCafFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroFincaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroOfertaFragment;

/**
 * Created by hacu1 on 12/11/2018.
 */

public interface IFragments extends CaficultorFincaFragment.OnFragmentInteractionListener,
        RegistroFincaFragment.OnFragmentInteractionListener,
        CaficultorOfertaFragment.OnFragmentInteractionListener,
        RegistroOfertaFragment.OnFragmentInteractionListener,
        DetalleOfertaCafFragment.OnFragmentInteractionListener,
        PostuladosOfertasCafFragment.OnFragmentInteractionListener,
        AceptadosOfertaCafFragment.OnFragmentInteractionListener,
        PerfilCaficultorFragment.OnFragmentInteractionListener,
        DetallePostuladoOfertaFragment.OnFragmentInteractionListener,
        DetalleAceptadoOfertaFragment.OnFragmentInteractionListener{
}
