package moe.takochan.webnei.exporter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import moe.takochan.webnei.exporter.domain.asset.render.client.AssetRenderTickHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        FMLCommonHandler.instance()
            .bus()
            .register(new AssetRenderTickHandler());
    }
}
