package com.rolandoislas.multihotbar;

import com.sun.org.apache.bcel.internal.generic.BIPUSH;
import com.sun.org.apache.bcel.internal.generic.FDIV;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Iterator;

/**
 * Created by Rolando on 6/7/2016.
 */
public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String newName, byte[] bytecode) {
        if (className.equals("net.minecraft.entity.player.InventoryPlayer"))
            return patchPlayerInventory(className, bytecode, true);
        if (className.equals("yx"))
            return patchPlayerInventory(className, bytecode, false);
        return bytecode;
    }

    private byte[] patchPlayerInventory(String className, byte[] bytecode, boolean deobfuscated) {
        String methodNameGCI = deobfuscated ? "getCurrentItem" : "h";
        String methodDescriptionGCI = "()Lnet/minecraft/item/ItemStack;";
        String methodNameGHS = deobfuscated ? "getHotbarSize" : "i";
        String methodDescriptionGHS = "()I";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            // Extend getCurrentItem slots
            if (method.name.equals(methodNameGCI) && method.desc.equals(methodDescriptionGCI)) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                        ((IntInsnNode) currentNode).operand = Config.numberOfHotbars * 9;
                        break;
                    }
                }
            }
            // Extend getHotbarSize slots
            if (method.name.equals(methodNameGHS) && method.desc.equals(methodDescriptionGHS)) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                        ((IntInsnNode) currentNode).operand = Config.numberOfHotbars * 9;
                        break;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
