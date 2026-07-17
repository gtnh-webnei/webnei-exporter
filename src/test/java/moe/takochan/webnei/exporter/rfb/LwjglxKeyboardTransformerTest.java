package moe.takochan.webnei.exporter.rfb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

import moe.takochan.webnei.exporter.domain.item.internal.TooltipKeyOverride;

class LwjglxKeyboardTransformerTest {

    private static final String RESOURCE = "META-INF/rfb-plugin/webnei.properties";

    @AfterEach
    void clearOverride() {
        TooltipKeyOverride.clear();
    }

    @Test
    void resourceDeclaresPluginOrderingExclusionAndExpandedVersion() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
            .getResourceAsStream(RESOURCE)) {
            assertNotNull(input);
            properties.load(input);
        }

        assertEquals(WebneiRfbPlugin.class.getName(), properties.getProperty("className"));
        assertEquals("lwjgl3ify", properties.getProperty("loadAfter"));
        assertEquals("lwjgl3ify", properties.getProperty("loadRequires"));
        assertEquals("moe.takochan.webnei.exporter.rfb.", properties.getProperty("transformerExclusions"));
        assertFalse(
            properties.getProperty("version")
                .contains("${"));
    }

    @Test
    void pluginRegistersOnlyTheTargetedTransformer() {
        RfbClassTransformer[] transformers = new WebneiRfbPlugin().makeTransformers();

        assertEquals(1, transformers.length);
        assertTrue(transformers[0] instanceof LwjglxKeyboardTransformer);
    }

    @Test
    void acceptsOnlyExcludedLaunchClassLoaderContextAndExactClass() {
        LwjglxKeyboardTransformer transformer = new LwjglxKeyboardTransformer();
        ClassNodeHandle present = classHandle(false, 1);

        assertTrue(
            transformer.shouldTransformClass(
                null,
                RfbClassTransformer.Context.LCL_NO_TRANSFORMS,
                null,
                LwjglxKeyboardTransformer.TARGET_CLASS,
                present));
        assertFalse(
            transformer.shouldTransformClass(
                null,
                RfbClassTransformer.Context.LCL_WITH_TRANSFORMS,
                null,
                LwjglxKeyboardTransformer.TARGET_CLASS,
                present));
        assertFalse(
            transformer.shouldTransformClass(
                null,
                RfbClassTransformer.Context.SYSTEM,
                null,
                LwjglxKeyboardTransformer.TARGET_CLASS,
                present));
        assertFalse(
            transformer.shouldTransformClass(
                null,
                RfbClassTransformer.Context.LCL_NO_TRANSFORMS,
                null,
                "org.lwjglx.input.Other",
                present));
    }

    @Test
    void injectsExactHeadOverrideAndFallsThroughOnNull() throws Exception {
        LwjglxKeyboardTransformer transformer = new LwjglxKeyboardTransformer();
        ClassNodeHandle handle = classHandle(false, 1);
        transformer.transformClass(
            null,
            RfbClassTransformer.Context.LCL_NO_TRANSFORMS,
            null,
            LwjglxKeyboardTransformer.TARGET_CLASS,
            handle);

        MethodNode target = targetMethod(handle.getNode());
        MethodInsnNode overrideCall = (MethodInsnNode) target.instructions.get(1);
        assertEquals(LwjglxKeyboardTransformer.OVERRIDE_OWNER, overrideCall.owner);
        assertEquals(LwjglxKeyboardTransformer.OVERRIDE_METHOD, overrideCall.name);
        assertEquals(LwjglxKeyboardTransformer.OVERRIDE_DESCRIPTOR, overrideCall.desc);

        byte[] transformed = handle.computeBytes();
        new ClassReader(transformed).accept(new ClassNode(), ClassReader.EXPAND_FRAMES);
        Class<?> keyboard = new TestClassLoader().define(LwjglxKeyboardTransformer.TARGET_CLASS, transformed);
        Method isKeyDown = keyboard.getMethod(LwjglxKeyboardTransformer.TARGET_METHOD, int.class);

        assertEquals(Boolean.FALSE, isKeyDown.invoke(null, org.lwjgl.input.Keyboard.KEY_LSHIFT));
        TooltipKeyOverride.activateReleased();
        TooltipKeyOverride.setPressed(org.lwjgl.input.Keyboard.KEY_LSHIFT, true);
        assertEquals(Boolean.TRUE, isKeyDown.invoke(null, org.lwjgl.input.Keyboard.KEY_LSHIFT));
        assertEquals(Boolean.FALSE, isKeyDown.invoke(null, org.lwjgl.input.Keyboard.KEY_LCONTROL));
        assertEquals(Boolean.FALSE, isKeyDown.invoke(null, org.lwjgl.input.Keyboard.KEY_A));
    }

    @Test
    void missingAndRepeatedStaticTargetsFailExplicitly() {
        LwjglxKeyboardTransformer transformer = new LwjglxKeyboardTransformer();
        ClassNodeHandle missing = classHandle(false, 0);
        ClassNodeHandle repeated = classHandle(false, 1);
        repeated.getNode().methods.add(copyTargetMethod());

        IllegalStateException missingFailure = assertThrows(
            IllegalStateException.class,
            () -> transformer.transformClass(
                null,
                RfbClassTransformer.Context.LCL_NO_TRANSFORMS,
                null,
                LwjglxKeyboardTransformer.TARGET_CLASS,
                missing));
        IllegalStateException repeatedFailure = assertThrows(
            IllegalStateException.class,
            () -> transformer.transformClass(
                null,
                RfbClassTransformer.Context.LCL_NO_TRANSFORMS,
                null,
                LwjglxKeyboardTransformer.TARGET_CLASS,
                repeated));

        assertTrue(
            missingFailure.getMessage()
                .contains("found 0"));
        assertTrue(
            repeatedFailure.getMessage()
                .contains("found 2"));
    }

    @Test
    void rfbClassesDoNotUseTooltipOverrideClassLiteral() throws IOException {
        String plugin = new String(
            Files.readAllBytes(Paths.get("src/main/java/moe/takochan/webnei/exporter/rfb/WebneiRfbPlugin.java")),
            StandardCharsets.UTF_8);
        String transformer = new String(
            Files.readAllBytes(
                Paths.get("src/main/java/moe/takochan/webnei/exporter/rfb/LwjglxKeyboardTransformer.java")),
            StandardCharsets.UTF_8);

        assertFalse(plugin.contains("TooltipKeyOverride"));
        assertFalse(transformer.contains("TooltipKeyOverride.class"));
    }

    private static ClassNodeHandle classHandle(boolean nonStatic, int targetCount) {
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "org/lwjglx/input/Keyboard", null, "java/lang/Object", null);
        for (int i = 0; i < targetCount; i++) {
            int access = Opcodes.ACC_PUBLIC | (nonStatic ? 0 : Opcodes.ACC_STATIC);
            org.objectweb.asm.MethodVisitor method = writer.visitMethod(
                access,
                LwjglxKeyboardTransformer.TARGET_METHOD,
                LwjglxKeyboardTransformer.TARGET_DESCRIPTOR,
                null,
                null);
            method.visitCode();
            method.visitInsn(Opcodes.ICONST_0);
            method.visitInsn(Opcodes.IRETURN);
            method.visitMaxs(1, nonStatic ? 2 : 1);
            method.visitEnd();
        }
        writer.visitEnd();
        return new ClassNodeHandle(writer.toByteArray());
    }

    private static MethodNode targetMethod(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (LwjglxKeyboardTransformer.TARGET_METHOD.equals(method.name)
                && LwjglxKeyboardTransformer.TARGET_DESCRIPTOR.equals(method.desc)) {
                return method;
            }
        }
        throw new AssertionError("Missing target method");
    }

    private static MethodNode copyTargetMethod() {
        MethodNode method = new MethodNode(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            LwjglxKeyboardTransformer.TARGET_METHOD,
            LwjglxKeyboardTransformer.TARGET_DESCRIPTOR,
            null,
            null);
        method.visitCode();
        method.visitInsn(Opcodes.ICONST_0);
        method.visitInsn(Opcodes.IRETURN);
        method.visitMaxs(1, 1);
        method.visitEnd();
        return method;
    }

    private static final class TestClassLoader extends ClassLoader {

        private Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
