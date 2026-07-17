package moe.takochan.webnei.exporter.rfb;

import java.util.jar.Manifest;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public final class LwjglxKeyboardTransformer implements RfbClassTransformer {

    static final String TARGET_CLASS = "org.lwjglx.input.Keyboard";
    static final String TARGET_METHOD = "isKeyDown";
    static final String TARGET_DESCRIPTOR = "(I)Z";
    static final String OVERRIDE_OWNER = "moe/takochan/webnei/exporter/domain/item/internal/TooltipKeyOverride";
    static final String OVERRIDE_METHOD = "overrideFor";
    static final String OVERRIDE_DESCRIPTOR = "(I)Ljava/lang/Boolean;";

    @Override
    public String id() {
        return "lwjglx-keyboard-tooltip-keys";
    }

    @Override
    public boolean shouldTransformClass(ExtensibleClassLoader classLoader, Context context, Manifest manifest,
        String className, ClassNodeHandle classNode) {
        return context == Context.LCL_NO_TRANSFORMS && TARGET_CLASS.equals(className) && classNode.isPresent();
    }

    @Override
    public void transformClass(ExtensibleClassLoader classLoader, Context context, Manifest manifest, String className,
        ClassNodeHandle classNode) {
        if (context != Context.LCL_NO_TRANSFORMS || !TARGET_CLASS.equals(className)) {
            throw new IllegalStateException("Unexpected RFB transform target " + context + " " + className);
        }
        if (classNode.getNode() == null) {
            throw new IllegalStateException("Missing RFB transform class " + TARGET_CLASS);
        }

        MethodNode target = null;
        int matches = 0;
        for (MethodNode method : classNode.getNode().methods) {
            if (TARGET_METHOD.equals(method.name) && TARGET_DESCRIPTOR.equals(method.desc)
                && (method.access & Opcodes.ACC_STATIC) != 0) {
                target = method;
                matches++;
            }
        }
        if (matches != 1) {
            throw new IllegalStateException(
                "Expected exactly one static " + TARGET_CLASS
                    + '.'
                    + TARGET_METHOD
                    + TARGET_DESCRIPTOR
                    + ", found "
                    + matches);
        }
        if (target.instructions == null || target.instructions.getFirst() == null) {
            throw new IllegalStateException("Target method has no instructions: " + TARGET_METHOD + TARGET_DESCRIPTOR);
        }

        LabelNode originalPath = new LabelNode();
        InsnList injection = new InsnList();
        injection.add(new VarInsnNode(Opcodes.ILOAD, 0));
        injection
            .add(new MethodInsnNode(Opcodes.INVOKESTATIC, OVERRIDE_OWNER, OVERRIDE_METHOD, OVERRIDE_DESCRIPTOR, false));
        injection.add(new InsnNode(Opcodes.DUP));
        injection.add(new JumpInsnNode(Opcodes.IFNULL, originalPath));
        injection.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
        injection.add(new InsnNode(Opcodes.IRETURN));
        injection.add(originalPath);
        injection.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Boolean" }));
        injection.add(new InsnNode(Opcodes.POP));
        target.instructions.insertBefore(target.instructions.getFirst(), injection);
        target.maxStack = Math.max(target.maxStack, 2);
    }
}
