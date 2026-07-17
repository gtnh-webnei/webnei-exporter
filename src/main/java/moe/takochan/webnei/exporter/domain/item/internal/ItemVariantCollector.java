package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import moe.takochan.webnei.exporter.domain.item.model.ItemTooltipSnapshotRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/**
 * 使用 Minecraft ItemStack API 采集展示字段。
 *
 * <p>
 * display_name 保留原始格式码（§x）。单个 stack 在读取 display name 时抛异常（例如 forestry 个体 species 为 null）
 * 不影响整体导出，保留现有 NEI source-backed 占位文本。
 */
public final class ItemVariantCollector {

    /** 与 NEI 渲染失败时一致的占位显示名。 */
    private static final String UNNAMED = "Unnamed";

    interface BurnTimeAccess {

        int sample(ItemStack stack) throws Throwable;

        void activate(ItemStack canonicalStack, int sampledBurnTime) throws Throwable;

        void clear() throws Throwable;
    }

    interface TooltipTextAccess {

        String collect(ItemStack stack);
    }

    private final TooltipKeySimulator keySimulator;
    private final BurnTimeAccess burnTimeAccess;
    private final TooltipTextAccess tooltipTextAccess;

    public ItemVariantCollector() {
        this(new TooltipKeySimulator(), new FurnaceBurnTimeAccess(), ItemVariantCollector::collectTooltipText);
    }

    ItemVariantCollector(TooltipKeySimulator keySimulator) {
        this(keySimulator, new FurnaceBurnTimeAccess(), ItemVariantCollector::collectTooltipText);
    }

    ItemVariantCollector(TooltipKeySimulator keySimulator, BurnTimeAccess burnTimeAccess,
        TooltipTextAccess tooltipTextAccess) {
        this.keySimulator = keySimulator;
        this.burnTimeAccess = burnTimeAccess;
        this.tooltipTextAccess = tooltipTextAccess;
    }

    public ItemVariantRow collectVariant(String datasetId, ItemVariantIdentity variant, ItemStack stack) {
        return new ItemVariantRow(
            datasetId,
            variant.getItemVariantId(),
            variant.getItemId(),
            variant.getDamage(),
            variant.getNbtHash(),
            variant.getNbtText(),
            displayName(stack));
    }

    public List<ItemTooltipSnapshotRow> collectTooltipSnapshots(String datasetId, ItemVariantIdentity variant,
        ItemStack canonicalStack) {
        final int sampledBurnTime;
        try {
            sampledBurnTime = burnTimeAccess.sample(canonicalStack.copy());
        } catch (Throwable failure) {
            throw new IllegalStateException("Failed to sample tooltip burn time", failure);
        }

        boolean overrideActive = false;
        List<ItemTooltipSnapshotRow> snapshots = null;
        Throwable primaryFailure = null;
        try {
            try {
                burnTimeAccess.activate(canonicalStack, sampledBurnTime);
            } catch (Throwable failure) {
                throw new IllegalStateException("Failed to begin tooltip burn-time override", failure);
            }
            overrideActive = true;
            snapshots = collectTooltipStates(datasetId, variant, canonicalStack);
        } catch (Throwable failure) {
            primaryFailure = failure;
        }

        if (overrideActive) {
            IllegalStateException clearFailure = clearBurnTimeOverride();
            if (clearFailure != null) {
                if (primaryFailure != null) {
                    primaryFailure.addSuppressed(clearFailure);
                } else {
                    throw clearFailure;
                }
            }
        }
        if (primaryFailure != null) {
            throw propagate(primaryFailure);
        }
        return snapshots;
    }

    private List<ItemTooltipSnapshotRow> collectTooltipStates(String datasetId, ItemVariantIdentity variant,
        ItemStack canonicalStack) {
        List<ItemTooltipSnapshotRow> snapshots = new ArrayList<>(TooltipKeyState.values().length);
        for (TooltipKeyState state : TooltipKeyState.values()) {
            ItemStack tooltipStack = canonicalStack.copy();
            snapshots.add(
                keySimulator.withState(
                    state,
                    () -> new ItemTooltipSnapshotRow(
                        datasetId,
                        variant.getItemVariantId(),
                        state.tooltipType(),
                        state.persistedKey(),
                        tooltipTextAccess.collect(tooltipStack))));
        }
        return snapshots;
    }

    private IllegalStateException clearBurnTimeOverride() {
        try {
            burnTimeAccess.clear();
            return null;
        } catch (Throwable failure) {
            return new IllegalStateException("Failed to end tooltip burn-time override", failure);
        }
    }

    private static String collectTooltipText(ItemStack stack) {
        String rarityColor = "";
        try {
            rarityColor = stack.getRarity().rarityColor.toString();
            @SuppressWarnings("unchecked")
            List<String> rawLines = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            return TooltipTextFormatter.format(rarityColor, rawLines);
        } catch (Exception ignored) {
            return TooltipTextFormatter.formatFailure(rarityColor);
        }
    }

    private static String displayName(ItemStack stack) {
        try {
            return value(stack.getDisplayName());
        } catch (Throwable ignored) {
            return UNNAMED;
        }
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }

    private static RuntimeException propagate(Throwable failure) {
        ItemVariantCollector.<RuntimeException>throwUnchecked(failure);
        throw new AssertionError("unreachable");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwUnchecked(Throwable failure) throws T {
        throw (T) failure;
    }

    private static final class FurnaceBurnTimeAccess implements BurnTimeAccess {

        @Override
        public int sample(ItemStack stack) {
            return TileEntityFurnace.getItemBurnTime(stack);
        }

        @Override
        public void activate(ItemStack canonicalStack, int sampledBurnTime) {
            TooltipBurnTimeOverride.activate(canonicalStack, sampledBurnTime);
        }

        @Override
        public void clear() {
            TooltipBurnTimeOverride.clear();
        }
    }
}
