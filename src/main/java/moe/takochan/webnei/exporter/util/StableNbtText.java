package moe.takochan.webnei.exporter.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * 生成 canonical NBT 文本。
 *
 * <p>
 * compound key 递归排序，list 保持原顺序，用于 ItemStack identity 和 nbt_hash，避免不同来源的等价 NBT 因 key 顺序不同而失配。
 */
public final class StableNbtText {

    /** Minecraft 1.7.10 NBTTagList 内部 list 字段，用于按原顺序读取 list 元素。 */
    private static final Field TAG_LIST_FIELD = tagListField();

    private StableNbtText() {}

    /** 生成 compound 的 canonical 文本；无 NBT 时返回空字符串。 */
    public static String of(NBTTagCompound tag) {
        return tag == null ? "" : write(tag);
    }

    /** 分派不同 NBT 类型的 canonical 写出逻辑。 */
    private static String write(NBTBase tag) {
        if (tag instanceof NBTTagCompound) {
            return writeCompound((NBTTagCompound) tag);
        }
        if (tag instanceof NBTTagList) {
            return writeList((NBTTagList) tag);
        }
        return tag.toString();
    }

    /** 写出 compound：key 按字典序排序，value 递归写出。 */
    private static String writeCompound(NBTTagCompound tag) {
        @SuppressWarnings("unchecked")
        Set<String> keySet = tag.func_150296_c();
        List<String> keys = new ArrayList<>(keySet);
        Collections.sort(keys);

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            String key = keys.get(i);
            builder.append(key)
                .append(':')
                .append(write(tag.getTag(key)));
        }
        builder.append('}');
        return builder.toString();
    }

    /** 写出 list：保持原始元素顺序，元素递归写出。 */
    private static String writeList(NBTTagList tag) {
        List<NBTBase> values = listValues(tag);
        if (values == null) {
            return tag.toString();
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(write(values.get(i)));
        }
        builder.append(']');
        return builder.toString();
    }

    /** 读取 NBTTagList 内部元素；字段不可用时返回 null，让调用方回退到原始 toString。 */
    @SuppressWarnings("unchecked")
    private static List<NBTBase> listValues(NBTTagList tag) {
        if (TAG_LIST_FIELD == null) {
            return null;
        }
        try {
            return (List<NBTBase>) TAG_LIST_FIELD.get(tag);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /** 查找 Minecraft 1.7.10 NBTTagList 的内部 list 字段。 */
    private static Field tagListField() {
        try {
            Field field = NBTTagList.class.getDeclaredField("tagList");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
