package com.hacu.micafe.Recolector.Interfaces;

import com.hacu.micafe.Caficultor.FragmentsCaf.InicioCaficultorFragment;
import com.hacu.micafe.Recolector.Fragments.DetalleOfertaRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.DetalleTrabajoFragment;
import com.hacu.micafe.Recolector.Fragments.ExperienciaRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.ListaPesadasFragment;
import com.hacu.micafe.Recolector.Fragments.ListaTrabajoFragment;
import com.hacu.micafe.Recolector.Fragments.OfertasRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.PerfilRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.RegistroExperienciaFragment;

/**
 * Created by hacu1 on 17/11/2018.
 */

public interface IFragments extends OfertasRecolectorFragment.OnFragmentInteractionListener,
        DetalleOfertaRecolectorFragment.OnFragmentInteractionListener,
        PerfilRecolectorFragment.OnFragmentInteractionListener,
        ExperienciaRecolectorFragment.OnFragmentInteractionListener,
        RegistroExperienciaFragment.OnFragmentInteractionListener,
        ListaTrabajoFragment.OnFragmentInteractionListener,
        InicioCaficultorFragment.OnFragmentInteractionListener,
        DetalleTrabajoFragment.OnFragmentInteractionListener,
        ListaPesadasFragment.OnFragmentInteractionListener
        {
}
