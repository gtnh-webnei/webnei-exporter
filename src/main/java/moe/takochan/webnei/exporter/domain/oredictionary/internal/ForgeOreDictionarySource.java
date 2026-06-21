package moe.takochan.webnei.exporter.domain.oredictionary.internal;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** 从 Forge OreDictionary 读取 dictionary name 和对应 ItemStack 列表。 */
public final class ForgeOreDictionarySource {

    private final OreDictionaryRegistrar registrar;

    public ForgeOreDictionarySource(OreDictionaryRegistrar registrar) {
        this.registrar = registrar;
    }

    /** 遍历 Forge OreDictionary 并交给 registrar 注册。 */
    public void collect() {
        for (String dictionaryName : OreDictionary.getOreNames()) {
            if (dictionaryName == null || dictionaryName.isEmpty()) {
                continue;
            }
            List<ItemStack> stacks = OreDictionary.getOres(dictionaryName, false);
            this.registrar.register(dictionaryName, stacks);
        }
    }
}
