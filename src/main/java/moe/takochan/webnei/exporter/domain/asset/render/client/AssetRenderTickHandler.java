package moe.takochan.webnei.exporter.domain.asset.render.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class AssetRenderTickHandler {

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AssetRenderDispatcher.INSTANCE.drain();
        }
    }
}
