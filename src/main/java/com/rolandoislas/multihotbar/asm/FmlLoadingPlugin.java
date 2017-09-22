package com.rolandoislas.multihotbar.asm;

import com.rolandoislas.multihotbar.data.Constants;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"com.rolandoislas.multihotbar.asm"})
@IFMLLoadingPlugin.Name(Constants.NAME + " Coremod")
public class FmlLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                ClassTransformerInventoryPlayer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
