package moe.takochan.webnei.exporter.coremod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import moe.takochan.webnei.exporter.compat.Mods;

/**
 * GTNH LateMixin 入口：在 mod postInit 阶段按已加载 mod 注册 mixin 到
 * {@code mixins.webnei.late.json}。比 EARLY 阶段安全，避免目标 mod 类未加载时 mixin 失败。
 */
@LateMixin
public class WebneiLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.webnei.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();

        boolean isClient = FMLLaunchHandler.side()
            .isClient();
        if (!isClient) {
            return mixins;
        }

        // Draconic Evolution: RenderEarthItem 时间驱动旋转
        if (Mods.DRACONIC_EVOLUTION.isLoaded()) {
            mixins.add("draconicevolution.RenderEarthItemMixin");
        }

        // Galacticraft Core: ItemRendererTier1Rocket 时间驱动旋转
        if (Mods.GALACTICRAFT_CORE.isLoaded()) {
            mixins.add("galacticraft.ItemRendererTier1RocketMixin");
        }

        // Galacticraft Planets (mars/asteroids): Tier2/Tier3 火箭时间驱动旋转
        if (Mods.GALACTICRAFT_PLANETS.isLoaded()) {
            mixins.add("galacticraft.ItemRendererTier2RocketMixin");
            mixins.add("galacticraft.ItemRendererTier3RocketMixin");
        }

        // AmunRa: ItemRendererShuttle 时间驱动旋转
        if (Mods.AMUNRA.isLoaded()) {
            mixins.add("amunra.ItemRendererShuttleMixin");
        }

        // GalaxySpace: ItemRendererRocket 时间驱动旋转
        if (Mods.GALAXY_SPACE.isLoaded()) {
            mixins.add("galaxyspace.ItemRendererRocketMixin");
        }

        return mixins;
    }
}
