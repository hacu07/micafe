package com.hacu.micafe.Caficultor.Interfaces;

import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorFincaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.CaficultorOfertaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.DetalleOfertaCafFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroFincaFragment;
import com.hacu.micafe.Caficultor.FragmentsCaf.RegistroOfertaFragment;

/**
 * Created by hacu1 on 12/11/2018.
 */

public interface IFragments extends CaficultorFincaFragment.OnFragmentInteractionListener,
        RegistroFincaFragment.OnFragmentInteractionListener,
        CaficultorOfertaFragment.OnFragmentInteractionListener,
        RegistroOfertaFragment.OnFragmentInteractionListener,
        DetalleOfertaCafFragment.OnFragmentInteractionListener{
}
