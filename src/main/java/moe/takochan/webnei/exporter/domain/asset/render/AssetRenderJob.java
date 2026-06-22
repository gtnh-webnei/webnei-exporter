package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.drawable.DrawableResource;
import lombok.Getter;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;

@Getter
public final class AssetRenderJob {

    private static final char KEY_SEPARATOR = '\u0000';

    private final String datasetId;
    private final String ownerType;
    private final String ownerId;
    private final String kind;
    private final ItemStack itemStack;
    private final FluidStack fluidStack;
    private final DrawableResource image;
    private final String fallbackText;

    private AssetRenderJob(String datasetId, String ownerType, String ownerId, String kind, ItemStack itemStack,
        FluidStack fluidStack, DrawableResource image, String fallbackText) {
        this.datasetId = datasetId;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.kind = kind;
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
        this.image = image;
        this.fallbackText = fallbackText;
    }

    public static AssetRenderJob itemIcon(String datasetId, String itemVariantId, ItemStack stack) {
        return itemStackIcon(
            datasetId,
            AssetContract.OWNER_TYPE_ITEM_VARIANT,
            itemVariantId,
            AssetContract.KIND_ITEM_ICON,
            stack);
    }

    public static AssetRenderJob recipeCategoryIcon(String datasetId, String categoryId, ItemStack stack) {
        return itemStackIcon(
            datasetId,
            AssetContract.OWNER_TYPE_RECIPE_CATEGORY,
            categoryId,
            AssetContract.KIND_RECIPE_CATEGORY_ICON,
            stack);
    }

    public static AssetRenderJob recipeCategoryImageIcon(String datasetId, String categoryId, DrawableResource image) {
        return new AssetRenderJob(
            datasetId,
            AssetContract.OWNER_TYPE_RECIPE_CATEGORY,
            categoryId,
            AssetContract.KIND_RECIPE_CATEGORY_ICON,
            null,
            null,
            image,
            null);
    }

    public static AssetRenderJob recipeCategoryTextIcon(String datasetId, String categoryId, String text) {
        return new AssetRenderJob(
            datasetId,
            AssetContract.OWNER_TYPE_RECIPE_CATEGORY,
            categoryId,
            AssetContract.KIND_RECIPE_CATEGORY_ICON,
            null,
            null,
            null,
            text);
    }

    public static AssetRenderJob fluidIcon(String datasetId, String fluidId, FluidStack stack) {
        return new AssetRenderJob(
            datasetId,
            AssetContract.OWNER_TYPE_FLUID,
            fluidId,
            AssetContract.KIND_FLUID_ICON,
            null,
            stack.copy(),
            null,
            null);
    }

    public String key() {
        return ownerType + KEY_SEPARATOR + ownerId + KEY_SEPARATOR + kind;
    }

    private static AssetRenderJob itemStackIcon(String datasetId, String ownerType, String ownerId, String kind,
        ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.stackSize = 1;
        return new AssetRenderJob(datasetId, ownerType, ownerId, kind, copy, null, null, null);
    }
}
