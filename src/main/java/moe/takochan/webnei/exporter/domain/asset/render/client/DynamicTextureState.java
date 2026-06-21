package moe.takochan.webnei.exporter.domain.asset.render.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureClock;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.ReflectionHelper;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderException;

public final class DynamicTextureState {

    private static final int MAX_FRAME_COUNT = 512;
    private static final String ANGELICA_INTERPOLATED_ICON_CLASS = "jss.notfine.render.InterpolatedIcon";
    private static final Field FRAME_COUNTER = ReflectionHelper
        .findField(TextureAtlasSprite.class, "frameCounter", "field_110973_g");
    private static final Field TICK_COUNTER = ReflectionHelper
        .findField(TextureAtlasSprite.class, "tickCounter", "field_110983_h");
    private static final Field ANIMATION_METADATA = ReflectionHelper
        .findField(TextureAtlasSprite.class, "animationMetadata", "field_110982_k");
    private static final Field FRAMES_TEXTURE_DATA = ReflectionHelper
        .findField(TextureAtlasSprite.class, "framesTextureData", "field_110976_a");

    private final List<TextureInfo> textures;
    private final int frameCount;
    private final boolean standardAtlasAnimation;
    private final Map<TextureAtlasSprite, SavedSpriteState> savedStates = new IdentityHashMap<>();

    private DynamicTextureState(List<TextureInfo> textures, int frameCount, boolean standardAtlasAnimation) {
        this.textures = textures;
        this.frameCount = frameCount;
        this.standardAtlasAnimation = standardAtlasAnimation;
    }

    public static DynamicTextureState from(ItemStack stack) {
        List<TextureInfo> textures = new ArrayList<>();
        collectTextures(stack, textures);
        if (textures.isEmpty()) {
            return new DynamicTextureState(textures, 1, false);
        }

        int lcm = 1;
        for (TextureInfo texture : textures) {
            if (!isStandard(texture.sprite)) {
                return new DynamicTextureState(textures, 1, false);
            }
            try {
                texture.animation = animationMetadata(texture.sprite);
                texture.frameTimes = frameTimes(texture.sprite, texture.animation);
                texture.period = sum(texture.frameTimes);
            } catch (RuntimeException e) {
                return new DynamicTextureState(textures, 1, false);
            }
            lcm = lcm(lcm, texture.period);
            if (lcm <= 1 || lcm > MAX_FRAME_COUNT) {
                return new DynamicTextureState(textures, 1, false);
            }
        }
        return new DynamicTextureState(textures, lcm, true);
    }

    public boolean isStandardAtlasAnimation() {
        return standardAtlasAnimation;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int currentIndex() throws AssetRenderException {
        if (textures.isEmpty()) {
            return 0;
        }
        long currentMod = -1L;
        long currentRemainder = -1L;
        for (TextureInfo texture : textures) {
            long period = texture.period;
            long remainder = textureIndex(texture);
            if (currentMod < 0L) {
                currentMod = period;
                currentRemainder = remainder % currentMod;
            } else {
                long[] merged = merge(currentRemainder, currentMod, remainder, period);
                if (merged == null) {
                    throw new AssetRenderException("Unable to merge dynamic texture periods");
                }
                currentMod = merged[0];
                currentRemainder = merged[1];
            }
        }
        return (int) currentRemainder;
    }

    public void updateAnimation() {
        for (TextureInfo texture : textures) {
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(texture.atlas);
            texture.sprite.updateAnimation();
        }
    }

    public void saveState() throws AssetRenderException {
        savedStates.clear();
        for (TextureInfo texture : textures) {
            int frameCounter = frameCounter(texture.sprite);
            int tickCounter = tickCounter(texture.sprite);
            int frameIndex = frameIndex(texture, frameCounter);
            savedStates.put(texture.sprite, new SavedSpriteState(frameCounter, tickCounter, frameIndex));
        }
    }

    public void restoreState() throws AssetRenderException {
        for (TextureInfo texture : textures) {
            SavedSpriteState state = savedStates.get(texture.sprite);
            if (state == null) {
                continue;
            }
            setFrameCounter(texture.sprite, state.frameCounter);
            setTickCounter(texture.sprite, state.tickCounter);
            uploadFrame(texture, state.frameIndex);
        }
        savedStates.clear();
    }

    private static void collectTextures(ItemStack stack, List<TextureInfo> textures) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).field_150939_a;
            if (RenderBlocks.renderItemIn3d(block.getRenderType())) {
                for (int side = 0; side < 6; side++) {
                    addTexture(textures, block.getIcon(side, stack.getItemDamage()), TextureMap.locationBlocksTexture);
                }
                return;
            }
        }

        int passes = Math.max(1, item.getRenderPasses(stack.getItemDamage()));
        ResourceLocation atlas = item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture
            : TextureMap.locationItemsTexture;
        for (int pass = 0; pass < passes; pass++) {
            addTexture(textures, item.getIcon(stack, pass), atlas);
        }
    }

    private static void addTexture(List<TextureInfo> textures, IIcon icon, ResourceLocation atlas) {
        if (!(icon instanceof TextureAtlasSprite)) {
            return;
        }
        TextureAtlasSprite sprite = (TextureAtlasSprite) icon;
        for (TextureInfo existing : textures) {
            if (existing.sprite == sprite) {
                return;
            }
        }
        textures.add(new TextureInfo(sprite, atlas));
    }

    private static boolean isStandard(TextureAtlasSprite sprite) {
        if (sprite instanceof TextureClock || sprite instanceof TextureCompass) {
            return false;
        }
        String className = sprite.getClass()
            .getName();
        boolean angelicaInterpolatedIcon = ANGELICA_INTERPOLATED_ICON_CLASS.equals(className);
        if (className.contains("TextureSpecial") || className.contains("TextureFX")
            || className.contains("Compass")
            || className.contains("Locator")) {
            return false;
        }
        try {
            Method updateAnimation = sprite.getClass()
                .getMethod("updateAnimation");
            if (updateAnimation.getDeclaringClass() != TextureAtlasSprite.class && !angelicaInterpolatedIcon) {
                return false;
            }
            return animationMetadata(sprite) != null && sprite.getFrameCount() > 1;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static int[] frameTimes(TextureAtlasSprite sprite, AnimationMetadataSection animation) {
        int count = animation.getFrameCount() == 0 ? sprite.getFrameCount() : animation.getFrameCount();
        int[] times = new int[count];
        for (int i = 0; i < times.length; i++) {
            times[i] = animation.getFrameTimeSingle(i);
        }
        return times;
    }

    private static int textureIndex(TextureInfo texture) throws AssetRenderException {
        int frameCounter = frameCounter(texture.sprite);
        int tickCounter = tickCounter(texture.sprite);
        int index = tickCounter;
        for (int i = 0; i < frameCounter; i++) {
            index += texture.frameTimes[i];
        }
        return index;
    }

    private static int frameIndex(TextureInfo texture, int frameCounter) {
        if (texture.animation.getFrameCount() == 0) {
            return frameCounter;
        }
        return texture.animation.getFrameIndex(frameCounter);
    }

    @SuppressWarnings("unchecked")
    private static void uploadFrame(TextureInfo texture, int frameIndex) throws AssetRenderException {
        try {
            List<int[][]> frames = (List<int[][]>) FRAMES_TEXTURE_DATA.get(texture.sprite);
            if (frameIndex < 0 || frameIndex >= frames.size()) {
                throw new AssetRenderException("Invalid texture frame index: " + frameIndex);
            }
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(texture.atlas);
            TextureUtil.uploadTextureMipmap(
                frames.get(frameIndex),
                texture.sprite.getIconWidth(),
                texture.sprite.getIconHeight(),
                texture.sprite.getOriginX(),
                texture.sprite.getOriginY(),
                false,
                false);
        } catch (IllegalAccessException e) {
            throw new AssetRenderException("Unable to upload saved texture frame", e);
        }
    }

    private static AnimationMetadataSection animationMetadata(TextureAtlasSprite sprite) {
        try {
            return (AnimationMetadataSection) ANIMATION_METADATA.get(sprite);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int frameCounter(TextureAtlasSprite sprite) throws AssetRenderException {
        try {
            return FRAME_COUNTER.getInt(sprite);
        } catch (IllegalAccessException e) {
            throw new AssetRenderException("Unable to read texture frame counter", e);
        }
    }

    private static int tickCounter(TextureAtlasSprite sprite) throws AssetRenderException {
        try {
            return TICK_COUNTER.getInt(sprite);
        } catch (IllegalAccessException e) {
            throw new AssetRenderException("Unable to read texture tick counter", e);
        }
    }

    private static void setFrameCounter(TextureAtlasSprite sprite, int value) throws AssetRenderException {
        try {
            FRAME_COUNTER.setInt(sprite, value);
        } catch (IllegalAccessException e) {
            throw new AssetRenderException("Unable to restore texture frame counter", e);
        }
    }

    private static void setTickCounter(TextureAtlasSprite sprite, int value) throws AssetRenderException {
        try {
            TICK_COUNTER.setInt(sprite, value);
        } catch (IllegalAccessException e) {
            throw new AssetRenderException("Unable to restore texture tick counter", e);
        }
    }

    private static int sum(int[] values) {
        int out = 0;
        for (int value : values) {
            out += value;
        }
        return out;
    }

    private static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int next = a % b;
            a = b;
            b = next;
        }
        return Math.abs(a);
    }

    private static long[] merge(long a, long m, long b, long n) {
        long[] egcd = egcd(m, n);
        long d = egcd[0];
        long r = b - a;
        if (r % d != 0) {
            return null;
        }
        long m1 = m / d;
        long n1 = n / d;
        long t = (r / d * modInv(m1, n1)) % n1;
        long newMod = m * n1;
        long newRemainder = (a + m * t) % newMod;
        if (newRemainder < 0) {
            newRemainder += newMod;
        }
        return new long[] { newMod, newRemainder };
    }

    private static long[] egcd(long a, long b) {
        if (b == 0L) {
            return new long[] { a, 1L, 0L };
        }
        long[] values = egcd(b, a % b);
        return new long[] { values[0], values[2], values[1] - a / b * values[2] };
    }

    private static long modInv(long a, long m) {
        long[] values = egcd(a, m);
        if (values[0] != 1L) {
            throw new ArithmeticException("No modular inverse");
        }
        long out = values[1] % m;
        return out < 0 ? out + m : out;
    }

    private static final class TextureInfo {

        private final TextureAtlasSprite sprite;
        private final ResourceLocation atlas;
        private AnimationMetadataSection animation;
        private int[] frameTimes;
        private int period;

        private TextureInfo(TextureAtlasSprite sprite, ResourceLocation atlas) {
            this.sprite = sprite;
            this.atlas = atlas;
        }
    }

    private static final class SavedSpriteState {

        private final int frameCounter;
        private final int tickCounter;
        private final int frameIndex;

        private SavedSpriteState(int frameCounter, int tickCounter, int frameIndex) {
            this.frameCounter = frameCounter;
            this.tickCounter = tickCounter;
            this.frameIndex = frameIndex;
        }
    }
}
