package moe.takochan.webnei.exporter.compat;

import cpw.mods.fml.common.Loader;

/**
 * 集中维护 webnei-exporter 与第三方 mod 集成时使用的 mod ID 字面量。所有 hook、mixin loader 检测目标 mod
 * 是否加载时都通过此处枚举，而不是分散写 {@code Loader.isModLoaded("Avaritia")} 或引用各 mod 自己的
 * MODID 常量——便于统一审计、避免 modid 漂移时遗漏修改点。
 *
 * <p>
 * mod ID 字面量手写并以注释标注来源（{@code @Mod(modid=...)} 注解、{@code Reference.MOD_ID} 等），与各 mod
 * 实际声明保持一致。新增集成时在此处补充一个枚举值，再在 hook / mixin loader 调用 {@link #isLoaded()}。
 */
public enum Mods {

    /** Avaritia — {@code @Mod(modid="Avaritia")}（Avaritia 主类未暴露常量）。 */
    AVARITIA("Avaritia"),
    /** Eternal Singularity — {@code singulariteam.eternalsingularity.Reference.MOD_ID}。 */
    ETERNAL_SINGULARITY("eternalsingularity"),
    /** Draconic Evolution — {@code com.brandon3055.draconicevolution.common.lib.References.MODID}。 */
    DRACONIC_EVOLUTION("DraconicEvolution"),
    /** EnderIO — {@code crazypants.enderio.EnderIO.MODID}。 */
    ENDER_IO("EnderIO"),
    /** Gadomancy — {@code makeo.gadomancy.common.Gadomancy.MODID}。 */
    GADOMANCY("gadomancy"),
    /** Galacticraft Core — {@code micdoodle8.mods.galacticraft.core.Constants.MOD_ID_CORE}。 */
    GALACTICRAFT_CORE("GalacticraftCore"),
    /** Galacticraft Planets / Mars — {@code Constants.MOD_ID_PLANETS}（含 ItemRendererThermalArmor 等子模块）。 */
    GALACTICRAFT_PLANETS("GalacticraftMars"),
    /** Galaxy Space — {@code galaxyspace.GalaxySpace.MODID}。 */
    GALAXY_SPACE("GalaxySpace"),
    /** Witching Gadgets — {@code witchinggadgets.WitchingGadgets.MODID}。 */
    WITCHING_GADGETS("WitchingGadgets");

    public final String id;

    Mods(String id) {
        this.id = id;
    }

    /** 该 mod 当前运行时是否已加载。 */
    public boolean isLoaded() {
        return Loader.isModLoaded(id);
    }
}
