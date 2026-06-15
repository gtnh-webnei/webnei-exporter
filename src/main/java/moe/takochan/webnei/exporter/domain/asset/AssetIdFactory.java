package moe.takochan.webnei.exporter.domain.asset;

import moe.takochan.webnei.exporter.domain.item.ItemIdentity;
import moe.takochan.webnei.exporter.domain.item.ItemVariantIdentity;

/** 根据领域身份生成稳定、路径安全的 asset_id。 */
public final class AssetIdFactory {

    public String itemIcon(ItemIdentity item, ItemVariantIdentity variant) {
        StringBuilder builder = new StringBuilder();
        builder.append("item/")
            .append(segment(item.getModId()))
            .append('/')
            .append(segment(item.getRegistryName()))
            .append('_')
            .append(variant.getDamage());
        if (!variant.getNbtHash()
            .isEmpty()) {
            builder.append('_')
                .append(segment(variant.getNbtHash()));
        }
        builder.append(".png");
        return builder.toString();
    }

    private static String segment(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^A-Za-z0-9._-]", "_");
        sanitized = sanitized.replaceAll("^\\.+", "")
            .replaceAll("\\.+$", "");
        return sanitized.isEmpty() ? "value" : sanitized;
    }
}
