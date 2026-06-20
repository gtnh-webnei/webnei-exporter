package moe.takochan.webnei.exporter.hook.recipe;

import lombok.Getter;

@Getter
enum ReviewedSkippedRecipeCategory {

    AE2WCT_ORDERED_CRAFTING(
        "ae2wct:net.p455w0rd.wirelesscraftingterminal.integration.modules.neihelpers.neiaeshapedrecipehandler",
        "ae2wct", "有序合成"),
    BUILDCRAFT_FACTORY_REFINERY("buildcraft_factory:buildcraft.compat.nei.recipehandlerrefinery", "BuildCraft|Factory",
        "精炼厂"),
    CREATIVECORE_RECIPE_INFO("creativecore:com.creativemd.creativecore.api.nei.neirecipeinfohandler", "creativecore",
        "IRecipeInfo"),
    FORESTRY_BEE_BREEDING_TREE("forestry:hellfirepvp.beebetteratbees.client.gui.bbabguirecipetreehandler", "Forestry",
        "蜜蜂育种树"),
    IC2NEI_CROP_BREEDING("ic2nei:speiger.src.crops.prediction.neiplugin", "Ic2Nei", "IC2 Crop Breeding"),
    NEI_CUSTOM_DIAGRAM_ENDER_STORAGE_CHEST("neicustomdiagram:neicustomdiagram.diagramgroup.enderstorage.chestoverview",
        "neicustomdiagram", "末影箱子概况"),
    NEI_CUSTOM_DIAGRAM_ENDER_STORAGE_TANK("neicustomdiagram:neicustomdiagram.diagramgroup.enderstorage.tankoverview",
        "neicustomdiagram", "末影储罐概况"),
    NEI_CUSTOM_DIAGRAM_GREGTECH_CIRCUITS("neicustomdiagram:neicustomdiagram.diagramgroup.gregtech.circuits",
        "neicustomdiagram", "GT电路板"),
    NEI_CUSTOM_DIAGRAM_GREGTECH_LENSES("neicustomdiagram:neicustomdiagram.diagramgroup.gregtech.lenses",
        "neicustomdiagram", "GT透镜"),
    NEI_CUSTOM_DIAGRAM_GREGTECH_MATERIAL_PARTS("neicustomdiagram:neicustomdiagram.diagramgroup.gregtech.materialparts",
        "neicustomdiagram", "GT材料零件"),
    NEI_CUSTOM_DIAGRAM_GREGTECH_MATERIAL_TOOLS("neicustomdiagram:neicustomdiagram.diagramgroup.gregtech.materialtools",
        "neicustomdiagram", "GT材料工具"),
    NEI_CUSTOM_DIAGRAM_GREGTECH_ORE_PROCESSING("neicustomdiagram:neicustomdiagram.diagramgroup.gregtech.oreprocessing",
        "neicustomdiagram", "GT矿物处理"),
    MINECRAFT_FUEL("minecraft:fuel", "minecraft", "燃料"),
    NEI_INFORMATION("nei:information", "nei", "信息"),
    FORGE_MULTIPART_PROFILER("forgemultipart:codechicken.nei.recipe.profilerrecipehandler", "ForgeMultipart",
        "合成查询性能分析"),
    MOBSINFO_MOB_INFO("mobsinfo:mobsinfo.mobhandler", "mobsinfo", "Mob Info"),
    MOBSINFO_INFERNAL_DROPS("mobsinfo:mobsinfo.mobhandlerinfernal", "mobsinfo", "Infernal Drops"),
    PROJECT_RED_TRANSMISSION_SHAPED("projred_transmission:mrtjp.projectred.core.libmc.recipe.prshapedrecipehandler",
        "ProjRed|Transmission", "有序合成"),
    BARTWORKS_ORES("bartworks:bartworks.neihandler.oreneihandler", "bartworks", "gt:bartworks_ores"),
    ET_FUTURUM_BANNER_PATTERNS("etfuturum:ganymedes01.etfuturum.compat.nei.bannerpatternhandler", "etfuturum", "旗帜图案"),
    PROJECT_RED_TRANSMISSION_SHAPELESS(
        "projred_transmission:mrtjp.projectred.core.libmc.recipe.prshapelessrecipehandler", "ProjRed|Transmission",
        "无序合成"),
    GALACTICRAFT_REFINERY("galacticraftcore:micdoodle8.mods.galacticraft.core.nei.refineryrecipehandler",
        "GalacticraftCore", "精炼机"),
    ADVANCED_SOLAR_PANEL_MOLECULAR_TRANSFORMER("advancedsolarpanel:advsolar.client.nei.mtrecipehandler",
        "AdvancedSolarPanel", "Molecular Transformer"),
    MOBSINFO_VILLAGER_TRADES("mobsinfo:mobsinfo.villagertradeshandler", "mobsinfo", "Villager Trades"),
    LOGISTICS_PIPES_SOLDERING_STATION("logisticspipes:logisticspipes.nei.neisolderingstationrecipemanager",
        "LogisticsPipes", "Soldering Station"),
    GT_NEI_ORE_PLUGIN_VEIN_STAT("gtneioreplugin:gtneioreplugin.plugin.gregtech5.plugingt5veinstat", "gtneioreplugin",
        "矿脉信息"),
    GT_NEI_ORE_PLUGIN_SMALL_ORE_STAT("gtneioreplugin:gtneioreplugin.plugin.gregtech5.plugingt5smallorestat",
        "gtneioreplugin", "贫瘠矿石信息"),
    GT_NEI_ORE_PLUGIN_UNDERGROUND_FLUID("gtneioreplugin:gtneioreplugin.plugin.gregtech5.plugingt5undergroundfluid",
        "gtneioreplugin", "地下流体信息"),
    THAUMCRAFT_ITEM_ASPECTS("thaumcraft:ru.timeconqueror.tcneiadditions.nei.aspectfromitemstackhandler", "Thaumcraft",
        "thaumcraft:item_aspects"),
    AMUN_RA_CIRCUIT_FABRICATOR("galacticraftamunra:de.katzenpapst.amunra.nei.recipehandler.arcircuitfab",
        "GalacticraftAmunRa", "元件制造台"),
    GALACTICRAFT_CIRCUIT_FABRICATOR(
        "galacticraftcore:micdoodle8.mods.galacticraft.core.nei.circuitfabricatorrecipehandler", "GalacticraftCore",
        "元件制造台"),
    TINKERS_TOOL_MATERIALS("tconstruct:tconstruct.plugins.nei.recipehandlertoolmaterials", "TConstruct",
        "tconstruct:tool_materials"),
    AVARITIA_EXTREME_COMPRESSION("avaritia:extreme_compression", "Avaritia", "中子态素压缩机"),
    IC2_SHAPED_CRAFTING("ic2:ic2.neiintegration.core.recipehandler.advrecipehandler", "IC2", "Shaped IC2 Crafting"),
    GENETICS_ACCLIMATISER("genetics:genetics.acclimatiser", "Genetics", "适应性调整器"),
    IC2_SHAPELESS_CRAFTING("ic2:ic2.neiintegration.core.recipehandler.advshapelessrecipehandler", "IC2",
        "Shapeless IC2 Crafting"),
    BOTANIA_LEXICA_BOTANIA("botania:vazkii.botania.client.integration.nei.recipe.recipehandlerlexicabotania", "Botania",
        "植物魔法辞典"),
    IC2_MACERATOR("ic2:macerator", "IC2", "Macerator"),
    IC2_EXTRACTOR("ic2:extractor", "IC2", "Extractor"),
    IC2_COMPRESSOR("ic2:compressor", "IC2", "Compressor"),
    IC2_SCRAPBOX("ic2:ic2.neiintegration.core.recipehandler.scrapboxrecipehandler", "IC2", "Scrapbox"),
    GALAXY_SPACE_ASSEMBLY_MACHINE("galaxyspace:galaxyspace.core.nei.assemblymachinerecipehandler", "GalaxySpace",
        "熔合机"),
    IC2_METAL_FORMER_EXTRUDING("ic2:metalformer.metalformerrecipehandlerextruding", "IC2", "金属成型机"),
    STRUCTURELIB_MULTIBLOCK_RENDERER(
        "structurelib:blockrenderer6343.integration.structurelib.structurecompatneihandler", "structurelib", "多方块结构"),
    IC2_METAL_FORMER_CUTTING("ic2:metalformer.metalformerrecipehandlercutting", "IC2", "金属成型机"),
    MINECRAFT_FLUID_REGISTRY("minecraft:tonius.neiintegration.mods.mcforge.recipehandlerfluidregistry", "minecraft",
        "流体注册表"),
    IC2_METAL_FORMER_ROLLING("ic2:metalformer.metalformerrecipehandlerrolling", "IC2", "金属成型机"),
    MINECRAFT_ORE_DICTIONARY("minecraft:tonius.neiintegration.mods.mcforge.recipehandleroredictionary", "minecraft",
        "矿物辞典"),
    IC2_CENTRIFUGE("ic2:centrifuge", "IC2", "热能离心机"),
    IC2_BLOCK_CUTTER("ic2:blockcutter", "IC2", "方块切割机"),
    IC2_ORE_WASHING("ic2:orewashing", "IC2", "洗矿机"),
    IC2_SOLID_CANNER("ic2:solidcanner", "IC2", "Canning Machine"),
    IC2_BLAST_FURNACE("ic2:blastfurnace", "IC2", "高炉"),
    IC2_LATHE("ic2:ic2.neiintegration.core.recipehandler.latherecipehandler", "IC2", "车床"),
    IC2_FLUID_CANNER("ic2:fluidcanner", "IC2", "Fluid Canning Machine"),
    EXTRA_UTILITIES_INFO("extrautilities:com.rwtema.extrautils.nei.infohandler", "ExtraUtilities",
        "Extra Utilities物品说明"),
    RAILCRAFT_ROCK_CRUSHER("railcraft:tonius.neiintegration.mods.railcraft.recipehandlerrockcrusher", "Railcraft",
        "碎石机"),
    GREGTECH_MULTIBLOCK_RENDERER("gregtech:blockrenderer6343.integration.gregtech.gtneimultiblockhandler", "gregtech",
        "GT多方块结构"),
    AE2_CELL_VIEW("appliedenergistics2:appeng.integration.modules.neihelpers.neicellviewhandler", "appliedenergistics2",
        "元件视图"),
    AE2FC_CELL_VIEW("ae2fc:com.glodblock.github.nei.neicellviewhandler", "ae2fc", "元件视图"),
    OPEN_COMPUTERS_MANUAL("opencomputers:li.cil.oc.integration.nei.manualusagehandler", "OpenComputers", "Manual"),
    OPEN_COMPUTERS_API("opencomputers:li.cil.oc.integration.nei.callbackdochandler", "OpenComputers",
        "OpenComputers API");

    private final String categoryId;
    private final String modId;
    private final String name;

    ReviewedSkippedRecipeCategory(String categoryId, String modId, String name) {
        this.categoryId = categoryId;
        this.modId = modId;
        this.name = name;
    }
}
