package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.item.Item;

import org.junit.jupiter.api.Test;

class ForgeItemIdentityResolverTest {

    private static final Path RESOLVER_SOURCE = Paths
        .get("src/main/java/moe/takochan/webnei/exporter/domain/item/internal/ForgeItemIdentityResolver.java");

    private final ForgeItemIdentityResolver resolver = new ForgeItemIdentityResolver();

    @Test
    void rejectsUnregisteredItemWithoutCallingForgeIdentifierResolution() {
        Item item = new Item();

        assertFalse(resolver.hasStableIdentity(item));
        assertThrows(IllegalArgumentException.class, () -> resolver.resolveItem(item));
    }

    @Test
    void checksRegistryNameBeforeCallingForgeStructuredIdentityApi() throws IOException {
        String source = new String(Files.readAllBytes(RESOLVER_SOURCE), StandardCharsets.UTF_8);
        int registryLookup = source.indexOf("String registryName = Item.itemRegistry.getNameForObject(item);");
        int missingIdentityCheck = source.indexOf("if (registryName == null)", registryLookup);
        int forgeLookup = source.indexOf("GameRegistry.findUniqueIdentifierFor(item)", missingIdentityCheck);

        assertTrue(registryLookup >= 0);
        assertTrue(missingIdentityCheck > registryLookup);
        assertTrue(forgeLookup > missingIdentityCheck);
    }
}
