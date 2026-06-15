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

    private static final Field TAG_LIST_FIELD = tagListField();

    private StableNbtText() {}

    public static String of(NBTTagCompound tag) {
        return tag == null ? "" : write(tag);
    }

    private static String write(NBTBase tag) {
        if (tag instanceof NBTTagCompound) {
            return writeCompound((NBTTagCompound) tag);
        }
        if (tag instanceof NBTTagList) {
            return writeList((NBTTagList) tag);
        }
        return tag.toString();
    }

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
