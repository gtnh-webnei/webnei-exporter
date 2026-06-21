package moe.takochan.webnei.exporter.hook.recipe;

import lombok.Getter;

/**
 * 经人工 review 判定为「非配方」、不应进入 recipe 阶段处理的分类清单。
 *
 * <p>
 * 这是人工业务决策的快照，无法由运行时规则自动判定，因此显式维护。 匹配键是 {@code categoryId}，与导出的 {@code recipe_category.tsv}、数据库主键完全一致：
 * 维护者只需查看导出的 recipe_category 数据，找到不该作为配方分类的那一行，把它的 {@code category_id} 抄进本枚举即可。 {@code modId} 与 {@code name}
 * 对应同一行的 mod_id、display_name，仅供辨认。
 *
 * <p>
 * GTNH 版本更新后，按新导出的 recipe_category 数据维护本枚举即可。
 */
@Getter
enum ReviewedSkippedRecipeCategory {

    /** 不处理：有序合成 */
    NEIAESHAPED_RECIPE_HANDLER(
        "net.p455w0rd.wirelesscraftingterminal.integration.modules.neihelpers.neiaeshapedrecipehandler", "ae2wct",
        "有序合成"),
    /** 不处理：精炼厂 */
    RECIPE_HANDLER_REFINERY("buildcraft.compat.nei.recipehandlerrefinery", "BuildCraft|Factory", "精炼厂"),
    /** 不处理：IRecipeInfo */
    NEIRECIPE_INFO_HANDLER("com.creativemd.creativecore.api.nei.neirecipeinfohandler", "creativecore", "IRecipeInfo"),
    /** 图表：蜜蜂育种树 */
    BBABGUI_RECIPE_TREE_HANDLER("hellfirepvp.beebetteratbees.client.gui.bbabguirecipetreehandler", "Forestry", "蜜蜂育种树"),
    /** 不处理：IC2 Crop Breeding */
    NEIPLUGIN("speiger.src.crops.prediction.neiplugin", "Ic2Nei", "IC2 Crop Breeding"),
    /** 不处理：末影箱子概况 */
    CUSTOM_DIAGRAM_GROUP(
        "com.github.dcysteine.neicustomdiagram.api.diagram.customdiagramgroup.neicustomdiagram.diagramgroup.enderstorage.chestoverview",
        "neicustomdiagram", "末影箱子概况"),
    /** 不处理：末影储罐概况 */
    CUSTOM_DIAGRAM_GROUP_1(
        "com.github.dcysteine.neicustomdiagram.api.diagram.customdiagramgroup.neicustomdiagram.diagramgroup.enderstorage.tankoverview",
        "neicustomdiagram", "末影储罐概况"),
    /** 图表：GT电路板 */
    CUSTOM_DIAGRAM_GROUP_2(
        "com.github.dcysteine.neicustomdiagram.api.diagram.customdiagramgroup.neicustomdiagram.diagramgroup.gregtech.circuits",
        "neicustomdiagram", "GT电路板"),
    /** 图表：GT透镜 */
    DIAGRAM_GROUP(
        "com.github.dcysteine.neicustomdiagram.api.diagram.diagramgroup.neicustomdiagram.diagramgroup.gregtech.lenses",
        "neicustomdiagram", "GT透镜"),
    /** 图表：GT材料零件 */
    DIAGRAM_GROUP_1(
        "com.github.dcysteine.neicustomdiagram.api.diagram.diagramgroup.neicustomdiagram.diagramgroup.gregtech.materialparts",
        "neicustomdiagram", "GT材料零件"),
    /** 图表：GT材料工具 */
    DIAGRAM_GROUP_2(
        "com.github.dcysteine.neicustomdiagram.api.diagram.diagramgroup.neicustomdiagram.diagramgroup.gregtech.materialtools",
        "neicustomdiagram", "GT材料工具"),
    /** 图表：GT矿物处理 */
    DIAGRAM_GROUP_3(
        "com.github.dcysteine.neicustomdiagram.api.diagram.diagramgroup.neicustomdiagram.diagramgroup.gregtech.oreprocessing",
        "neicustomdiagram", "GT矿物处理"),
    /** 信息：燃料 */
    FUEL_RECIPE_HANDLER("codechicken.nei.recipe.fuelrecipehandler", "minecraft", "燃料"),
    /** 不处理：信息 */
    INFORMATION_HANDLER("codechicken.nei.recipe.informationhandler", "nei", "信息"),
    /** 不处理：合成查询性能分析 */
    PROFILER_RECIPE_HANDLER("codechicken.nei.recipe.profilerrecipehandler", "ForgeMultipart", "合成查询性能分析"),
    /** 生物掉落：Mob Info */
    MOB_HANDLER("com.kuba6000.mobsinfo.nei.mobhandler.mobsinfo.mobhandler", "mobsinfo", "Mob Info"),
    /** 生物掉落：Infernal Drops */
    MOB_HANDLER_INFERNAL("com.kuba6000.mobsinfo.nei.mobhandlerinfernal.mobsinfo.mobhandlerinfernal", "mobsinfo",
        "Infernal Drops"),
    /** 不处理：有序合成 */
    PRSHAPED_RECIPE_HANDLER("mrtjp.projectred.core.libmc.recipe.prshapedrecipehandler", "ProjRed|Transmission", "有序合成"),
    /** 特殊页面：gt:bartworks_ores */
    ORE_NEIHANDLER("bartworks.neihandler.oreneihandler", "bartworks", "gt:bartworks_ores"),
    /** 不处理：旗帜图案 */
    BANNER_PATTERN_HANDLER("ganymedes01.etfuturum.compat.nei.bannerpatternhandler", "etfuturum", "旗帜图案"),
    /** 不处理：无序合成 */
    PRSHAPELESS_RECIPE_HANDLER("mrtjp.projectred.core.libmc.recipe.prshapelessrecipehandler", "ProjRed|Transmission",
        "无序合成"),
    /** 不处理：精炼机 */
    REFINERY_RECIPE_HANDLER("micdoodle8.mods.galacticraft.core.nei.refineryrecipehandler", "GalacticraftCore", "精炼机"),
    /** 不处理：Molecular Transformer */
    MTRECIPE_HANDLER("advsolar.client.nei.mtrecipehandler", "AdvancedSolarPanel", "Molecular Transformer"),
    /** 查询/索引：Villager Trades */
    VILLAGER_TRADES_HANDLER("com.kuba6000.mobsinfo.nei.villagertradeshandler.mobsinfo.villagertradeshandler",
        "mobsinfo", "Villager Trades"),
    /** 不处理：Soldering Station */
    NEISOLDERING_STATION_RECIPE_MANAGER("logisticspipes.nei.neisolderingstationrecipemanager", "LogisticsPipes",
        "Soldering Station"),
    /** 特殊页面：矿脉信息 */
    PLUGIN_GT5_VEIN_STAT("gtneioreplugin.plugin.gregtech5.plugingt5veinstat", "gtneioreplugin", "矿脉信息"),
    /** 特殊页面：贫瘠矿石信息 */
    PLUGIN_GT5_SMALL_ORE_STAT("gtneioreplugin.plugin.gregtech5.plugingt5smallorestat", "gtneioreplugin", "贫瘠矿石信息"),
    /** 特殊页面：地下流体信息 */
    PLUGIN_GT5_UNDERGROUND_FLUID("gtneioreplugin.plugin.gregtech5.plugingt5undergroundfluid", "gtneioreplugin",
        "地下流体信息"),
    /** 信息：thaumcraft:item_aspects */
    ASPECT_FROM_ITEM_STACK_HANDLER("ru.timeconqueror.tcneiadditions.nei.aspectfromitemstackhandler", "Thaumcraft",
        "thaumcraft:item_aspects"),
    /** 不处理：元件制造台 */
    ARCIRCUIT_FAB("de.katzenpapst.amunra.nei.recipehandler.arcircuitfab", "GalacticraftAmunRa", "元件制造台"),
    /** 不处理：元件制造台 */
    CIRCUIT_FABRICATOR_RECIPE_HANDLER("micdoodle8.mods.galacticraft.core.nei.circuitfabricatorrecipehandler",
        "GalacticraftCore", "元件制造台"),
    /** 信息：tconstruct:tool_materials */
    RECIPE_HANDLER_TOOL_MATERIALS("tconstruct.plugins.nei.recipehandlertoolmaterials", "TConstruct",
        "tconstruct:tool_materials"),
    /** 不处理：中子态素压缩机 */
    COMPRESSION_HANDLER("fox.spiteful.avaritia.compat.nei.compressionhandler.extreme_compression", "Avaritia",
        "中子态素压缩机"),
    /** 不处理：Shaped IC2 Crafting */
    ADV_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.advrecipehandler", "IC2", "Shaped IC2 Crafting"),
    /** 不处理：适应性调整器 */
    ACCLIMATISER_RECIPE_HANDLER("binnie.genetics.nei.acclimatiserrecipehandler.genetics.acclimatiser", "Genetics",
        "适应性调整器"),
    /** 不处理：Shapeless IC2 Crafting */
    ADV_SHAPELESS_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.advshapelessrecipehandler", "IC2",
        "Shapeless IC2 Crafting"),
    /** 查询/索引：植物魔法辞典 */
    RECIPE_HANDLER_LEXICA_BOTANIA("vazkii.botania.client.integration.nei.recipe.recipehandlerlexicabotania", "Botania",
        "植物魔法辞典"),
    /** 不处理：Macerator */
    MACERATOR_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.maceratorrecipehandler", "IC2", "Macerator"),
    /** 不处理：Extractor */
    EXTRACTOR_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.extractorrecipehandler", "IC2", "Extractor"),
    /** 不处理：Compressor */
    COMPRESSOR_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.compressorrecipehandler", "IC2", "Compressor"),
    /** 不处理：Scrapbox */
    SCRAPBOX_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.scrapboxrecipehandler", "IC2", "Scrapbox"),
    /** 不处理：熔合机 */
    ASSEMBLY_MACHINE_RECIPE_HANDLER("galaxyspace.core.nei.assemblymachinerecipehandler", "GalaxySpace", "熔合机"),
    /** 不处理：金属成型机 */
    METAL_FORMER_RECIPE_HANDLER_EXTRUDING("ic2.neiintegration.core.recipehandler.metalformerrecipehandlerextruding",
        "IC2", "金属成型机"),
    /** 图表：多方块结构 */
    STRUCTURE_COMPAT_NEIHANDLER("blockrenderer6343.integration.structurelib.structurecompatneihandler", "structurelib",
        "多方块结构"),
    /** 不处理：金属成型机 */
    METAL_FORMER_RECIPE_HANDLER_CUTTING("ic2.neiintegration.core.recipehandler.metalformerrecipehandlercutting", "IC2",
        "金属成型机"),
    /** 信息：流体注册表 */
    RECIPE_HANDLER_FLUID_REGISTRY("tonius.neiintegration.mods.mcforge.recipehandlerfluidregistry", "minecraft",
        "流体注册表"),
    /** 不处理：金属成型机 */
    METAL_FORMER_RECIPE_HANDLER_ROLLING("ic2.neiintegration.core.recipehandler.metalformerrecipehandlerrolling", "IC2",
        "金属成型机"),
    /** 信息：矿物辞典 */
    RECIPE_HANDLER_ORE_DICTIONARY("tonius.neiintegration.mods.mcforge.recipehandleroredictionary", "minecraft", "矿物辞典"),
    /** 不处理：热能离心机 */
    CENTRIFUGE_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.centrifugerecipehandler", "IC2", "热能离心机"),
    /** 不处理：方块切割机 */
    BLOCK_CUTTER_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.blockcutterrecipehandler", "IC2", "方块切割机"),
    /** 不处理：洗矿机 */
    ORE_WASHING_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.orewashingrecipehandler", "IC2", "洗矿机"),
    /** 不处理：Canning Machine */
    SOLID_CANNER_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.solidcannerrecipehandler", "IC2",
        "Canning Machine"),
    /** 不处理：高炉 */
    BLAST_FURNACE_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.blastfurnacerecipehandler", "IC2", "高炉"),
    /** 不处理：车床 */
    LATHE_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.latherecipehandler", "IC2", "车床"),
    /** 不处理：Fluid Canning Machine */
    FLUID_CANNER_RECIPE_HANDLER("ic2.neiintegration.core.recipehandler.fluidcannerrecipehandler", "IC2",
        "Fluid Canning Machine"),
    /** 不处理：Extra Utilities物品说明 */
    INFO_HANDLER("com.rwtema.extrautils.nei.infohandler", "ExtraUtilities", "Extra Utilities物品说明"),
    /** 不确定：碎石机 */
    RECIPE_HANDLER_ROCK_CRUSHER("tonius.neiintegration.mods.railcraft.recipehandlerrockcrusher", "Railcraft", "碎石机"),
    /** 不处理：GT多方块结构 */
    GTNEIMULTIBLOCK_HANDLER("blockrenderer6343.integration.gregtech.gtneimultiblockhandler", "gregtech", "GT多方块结构"),
    /** 不处理：元件视图 */
    NEICELL_VIEW_HANDLER("appeng.integration.modules.neihelpers.neicellviewhandler", "appliedenergistics2", "元件视图"),
    /** 不处理：元件视图 */
    NEICELL_VIEW_HANDLER_1("com.glodblock.github.nei.neicellviewhandler", "ae2fc", "元件视图"),
    /** 不处理：Manual */
    MANUAL_USAGE_HANDLER("li.cil.oc.integration.nei.manualusagehandler", "OpenComputers", "Manual"),
    /** 不处理：OpenComputers API */
    CALLBACK_DOC_HANDLER("li.cil.oc.integration.nei.callbackdochandler", "OpenComputers", "OpenComputers API");

    /** 被排除分类的 category_id，与 recipe_category 导出/主键一致。唯一参与匹配。 */
    private final String categoryId;

    /** 归属 mod，仅供维护者辨认。 */
    private final String modId;

    /** 分类显示名，仅供维护者辨认。 */
    private final String name;

    ReviewedSkippedRecipeCategory(String categoryId, String modId, String name) {
        this.categoryId = categoryId;
        this.modId = modId;
        this.name = name;
    }
}
