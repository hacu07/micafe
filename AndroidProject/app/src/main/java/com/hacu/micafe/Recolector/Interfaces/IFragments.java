package com.hacu.micafe.Recolector.Interfaces;

import com.hacu.micafe.Recolector.Fragments.DetalleOfertaRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.OfertasRecolectorFragment;
import com.hacu.micafe.Recolector.Fragments.PerfilRecolectorFragment;

/**
 * Created by hacu1 on 17/11/2018.
 */

public interface IFragments extends OfertasRecolectorFragment.OnFragmentInteractionListener,
        DetalleOfertaRecolectorFragment.OnFragmentInteractionListener,
        PerfilRecolectorFragment.OnFragmentInteractionListener{
}
