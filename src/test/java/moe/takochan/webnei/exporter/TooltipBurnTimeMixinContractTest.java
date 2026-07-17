package moe.takochan.webnei.exporter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class TooltipBurnTimeMixinContractTest {

    private static final Path MIXIN_CONFIG = Paths.get("src/main/resources/mixins.webnei.json");
    private static final Path MIXIN_SOURCE = Paths
        .get("src/main/java/moe/takochan/webnei/exporter/coremod/mixin/furnace/TileEntityFurnaceMixin.java");

    @Test
    void earlyCommonMixinsContainOnlyTheFurnaceOverrideForThisFix() throws IOException {
        String config = read(MIXIN_CONFIG);

        assertTrue(config.contains("\"furnace.TileEntityFurnaceMixin\""));
        assertFalse(config.contains("Lwjgl2KeyboardMixin"));
        assertFalse(config.contains("Lwjgl3ifyKeyboardMixin"));
    }

    @Test
    void furnaceMixinInjectsAtMappedStaticMethodHead() throws IOException {
        String source = read(MIXIN_SOURCE);

        assertTrue(source.contains("@Mixin(TileEntityFurnace.class)"));
        assertTrue(source.contains("@Inject(method = \"getItemBurnTime\", at = @At(\"HEAD\"), cancellable = true)"));
        assertTrue(source.contains("TooltipBurnTimeOverride.overrideFor(stack)"));
        assertFalse(source.contains("remap = false"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
