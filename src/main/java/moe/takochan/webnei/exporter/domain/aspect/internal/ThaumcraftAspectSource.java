package moe.takochan.webnei.exporter.domain.aspect.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** 使用 Thaumcraft 和 Thaumcraft NEI Plugin 的结构化 API 采集 Aspect 数据。 */
public final class ThaumcraftAspectSource {

    private static final int FIRST_COMPONENT_INDEX = 0;
    private static final int SECOND_COMPONENT_INDEX = 1;

    private final AspectRegistrar registrar;
    private final ItemDomainStore itemStore;

    public ThaumcraftAspectSource(AspectRegistrar registrar, ItemDomainStore itemStore) {
        this.registrar = registrar;
        this.itemStore = itemStore;
    }

    public void collect() {
        Map<String, ItemStack> stacks = itemStore.data()
            .stacks();
        Map<String, String> itemVariantIdsByAspectTag = indexAspectItemVariants(stacks);
        registerAspectDefinitions(itemVariantIdsByAspectTag);
        registerItemAspects(stacks);
    }

    private Map<String, String> indexAspectItemVariants(Map<String, ItemStack> stacks) {
        Map<String, String> itemVariantIdsByAspectTag = new LinkedHashMap<>();
        for (Map.Entry<String, ItemStack> entry : stacks.entrySet()) {
            ItemStack stack = entry.getValue();
            if (!(stack.getItem() instanceof ItemAspect)) {
                continue;
            }
            AspectList listedAspects = ItemAspect.getAspects(stack);
            if (listedAspects == null || listedAspects.aspects.size() != 1) {
                throw invalidAspectDisplayStack(entry.getKey());
            }
            Aspect aspect = listedAspects.aspects.keySet()
                .iterator()
                .next();
            if (aspect == null) {
                throw invalidAspectDisplayStack(entry.getKey());
            }
            itemVariantIdsByAspectTag.putIfAbsent(aspect.getTag(), entry.getKey());
        }
        return itemVariantIdsByAspectTag;
    }

    private void registerAspectDefinitions(Map<String, String> itemVariantIdsByAspectTag) {
        int registryOrder = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            String itemVariantId = itemVariantIdsByAspectTag.get(aspect.getTag());
            if (itemVariantId == null) {
                throw new IllegalStateException("No ItemAspect item variant exists for Aspect " + aspect.getTag());
            }
            registrar.registerDefinition(aspect, itemVariantId, registryOrder);
            if (!aspect.isPrimal()) {
                Aspect[] components = aspect.getComponents();
                registrar.registerComponent(
                    aspect.getTag(),
                    FIRST_COMPONENT_INDEX,
                    components[FIRST_COMPONENT_INDEX].getTag());
                registrar.registerComponent(
                    aspect.getTag(),
                    SECOND_COMPONENT_INDEX,
                    components[SECOND_COMPONENT_INDEX].getTag());
            }
            registryOrder++;
        }
    }

    private void registerItemAspects(Map<String, ItemStack> stacks) {
        for (Map.Entry<String, ItemStack> entry : stacks.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.getItem() instanceof ItemAspect) {
                continue;
            }
            AspectList effectiveAspects = effectiveAspects(entry.getKey(), stack);
            if (effectiveAspects == null || effectiveAspects.aspects.isEmpty()) {
                continue;
            }
            for (Map.Entry<Aspect, Integer> aspectEntry : effectiveAspects.aspects.entrySet()) {
                int amount = aspectEntry.getValue();
                if (amount <= 0) {
                    WebneiExporterMod.LOG.warn(
                        "Skipping non-positive Aspect amount for item variant {} and Aspect {}.",
                        entry.getKey(),
                        aspectEntry.getKey()
                            .getTag());
                    continue;
                }
                registrar.registerItemAspect(
                    entry.getKey(),
                    aspectEntry.getKey()
                        .getTag(),
                    amount);
            }
        }
    }

    private AspectList effectiveAspects(String itemVariantId, ItemStack stack) {
        try {
            AspectList baseAspects = ThaumcraftApiHelper.getObjectAspects(stack);
            return ThaumcraftApiHelper.getBonusObjectTags(stack, baseAspects);
        } catch (RuntimeException | StackOverflowError error) {
            WebneiExporterMod.LOG.warn(
                "Skipping Aspect export for item variant {} because Thaumcraft aspect calculation failed.",
                itemVariantId,
                error);
            return null;
        }
    }

    private static IllegalStateException invalidAspectDisplayStack(String itemVariantId) {
        return new IllegalStateException(
            "ItemAspect item variant " + itemVariantId + " must contain exactly one non-null Aspect.");
    }
}
