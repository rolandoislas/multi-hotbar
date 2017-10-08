package com.rolandoislas.multihotbar.asm;

import com.rolandoislas.multihotbar.data.Constants;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ClassTransformerInventoryPlayer implements IClassTransformer {
    private static final String CLASS_NAME;
    private static final String[] METHOD_GET_CURRENT_ITEM;

    static {
        CLASS_NAME = "net.minecraft.entity.player.InventoryPlayer";
        METHOD_GET_CURRENT_ITEM = new String[] {
                "getCurrentItem", // name
                "i", // name obfuscated
                "()Lnet/minecraft/item/ItemStack;", // description
                "()Laip;" // description obfuscated
        };
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!name.equals(CLASS_NAME) && !transformedName.equals(CLASS_NAME))
            return basicClass;
        LogManager.getLogger(Constants.MODID).debug(String.format("Transforming %s", CLASS_NAME));
        // Read bytecode
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        // Transform
        transformGetCurrentItem(classNode);
        // Write to array
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformGetCurrentItem(ClassNode classNode) {
        boolean found = false;
        for (MethodNode method : classNode.methods) {
            if ((method.name.equals(METHOD_GET_CURRENT_ITEM[0]) || method.name.equals(METHOD_GET_CURRENT_ITEM[1])) &&
                    (method.desc.equals(METHOD_GET_CURRENT_ITEM[2]) || method.desc.equals(METHOD_GET_CURRENT_ITEM[3]))) {
                found = true;
                LogManager.getLogger(Constants.MODID).debug(String.format("Transforming %s#%s",
                        CLASS_NAME, "getCurrentItem()"));
                // Create new instructions list
                InsnList getCurrentItem = new InsnList();
                LabelNode labelZero = new LabelNode();
                LabelNode labelOne = new LabelNode();
                getCurrentItem.add(labelZero);
                getCurrentItem.add(new LineNumberNode(((LineNumberNode)method.instructions.get(1)).line, labelZero));
                getCurrentItem.add(new VarInsnNode(Opcodes.ALOAD, 0));
                getCurrentItem.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/rolandoislas/multihotbar/util/InventoryHelperCommon",
                        "getCurrentItem",
                        "(Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/item/ItemStack;",
                        false
                ));
                getCurrentItem.add(new InsnNode(Opcodes.ARETURN));
                getCurrentItem.add(labelOne);
                // Set method variables
                method.maxLocals = 1;
                method.maxStack = 1;
                // Replace old instruction list
                method.instructions.clear();
                method.instructions.add(getCurrentItem);
                break;
            }
        }
        if (!found) {
            LogManager.getLogger(Constants.MODID).warn(String.format("Could not find %s#getCurrentItem", CLASS_NAME));
            for (MethodNode method : classNode.methods)
                LogManager.getLogger(Constants.MODID).debug(method.name + " " + method.desc);
            System.exit(1);
        }
    }
}
