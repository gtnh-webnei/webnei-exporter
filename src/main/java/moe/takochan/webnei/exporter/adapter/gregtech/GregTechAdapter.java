package moe.takochan.webnei.exporter.adapter.gregtech;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterResult;
import moe.takochan.webnei.exporter.adapter.IModAdapter;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

/** GregTech/GT++/BartWorks 相关 item variant 语义补充。 */
public final class GregTechAdapter implements IModAdapter {

    @Override
    public String id() {
        return Mods.GregTech.ID;
    }

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(id());
    }

    @Override
    public boolean supportsNeiHandler(NeiHandlerEntry entry) {
        return false;
    }

    @Override
    public AdapterResult extractNeiHandler(NeiHandlerEntry entry, AdapterContext context) {
        return AdapterResult.unsupported();
    }

    @Override
    public void fillItemVariant(ItemStack stack, ItemVariantRow row, AdapterContext context) {
        String expression = GregTechChemicalExpressionExtractor.itemExpression(stack);
        if (!expression.isEmpty()) {
            row.setChemicalExpression(expression);
        }
    }

}
