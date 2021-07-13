package com.toast.apocalypse.common.compat.top;

import mcjty.theoneprobe.api.*;

public class TopPlugin implements ITheOneProbe {

    @Override
    public void registerProvider(IProbeInfoProvider iProbeInfoProvider) {

    }

    @Override
    public void registerEntityProvider(IProbeInfoEntityProvider iProbeInfoEntityProvider) {

    }

    @Override
    public int registerElementFactory(IElementFactory iElementFactory) {
        return 0;
    }

    @Override
    public IElementFactory getElementFactory(int i) {
        return null;
    }

    @Override
    public IOverlayRenderer getOverlayRenderer() {
        return null;
    }

    @Override
    public IProbeConfig createProbeConfig() {
        return null;
    }

    @Override
    public void registerProbeConfigProvider(IProbeConfigProvider iProbeConfigProvider) {

    }

    @Override
    public void registerBlockDisplayOverride(IBlockDisplayOverride iBlockDisplayOverride) {

    }

    @Override
    public void registerEntityDisplayOverride(IEntityDisplayOverride iEntityDisplayOverride) {

    }

    @Override
    public IStyleManager getStyleManager() {
        return null;
    }
}
