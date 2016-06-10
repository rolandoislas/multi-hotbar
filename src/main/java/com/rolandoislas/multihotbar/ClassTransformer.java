package com.rolandoislas.multihotbar;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Created by Rolando on 6/7/2016.
 */
public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String newName, byte[] bytecode) {
        // InventoryPlayer
        if (className.equals("net.minecraft.entity.player.InventoryPlayer"))
            return patchPlayerInventory(bytecode, true);
        if (className.equals("yx"))
            return patchPlayerInventory(bytecode, false);
        // NetHandlerPlayServer
        if (className.equals("net.minecraft.network.NetHandlerPlayServer"))
            return patchCreativeInventory(bytecode, true);
        if (className.equals("nh"))
            return patchCreativeInventory(bytecode, false);
        // ForgeHooks
        if (className.equals("net.minecraftforge.common.ForgeHooks"))
            return patchPickBlock(bytecode);
        // Minecraft
        if (className.equals("net.minecraft.client.Minecraft"))
            return patchMiddleClick(bytecode, true);
        if (className.equals("bao"))
            return patchMiddleClick(bytecode, false);
        // TConstruct - AmmoSlotHolder
        if (className.equals("tconstruct.weaponry.client.AmmoSlotHandler"))
            return patchTConstuctSlotHighlight(bytecode);
        // Inventory Tweaks - InvTweaksContainerManager
        if (className.equals("invtweaks.InvTweaksContainerManager"))
            return patchInventoryTweaksAutoRefill(bytecode);
        return bytecode;
    }

    private byte[] patchInventoryTweaksAutoRefill(byte[] bytecode) {
        String methodNameITS = "indexToSlot";
        String methodDescriptionITS = "(Linvtweaks/api/container/ContainerSection;I)I";
        String methodNameGS = "getSlot";
        String methodDescriptionGS = "(Linvtweaks/api/container/ContainerSection;I)Lnet/minecraft/inventory/Slot;";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            // Extend getHotbarSize slots
            if ((method.name.equals(methodNameITS) && method.desc.equals(methodDescriptionITS)) ||
                    (method.name.equals(methodNameGS) && method.desc.equals(methodDescriptionGS))) {
                AbstractInsnNode lineNumberNode = method.instructions.get(1);
                InsnList callMethod = new InsnList();
                callMethod.add(new VarInsnNode(Opcodes.ILOAD, 2));
                callMethod.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/rolandoislas/"+MultiHotbar.MODID+"/ClassTransformer",
                        "getCorrectInvTweaksIndex", "(I)I", false));
                callMethod.add(new VarInsnNode(Opcodes.ISTORE, 2));
                method.instructions.insert(lineNumberNode, callMethod);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @SuppressWarnings("unused")
    public static int getCorrectInvTweaksIndex(int index) {
        /*
            27-35 first hotbar
            0-26 other hotbars
         */
        return index > 36 ? index - 36 : index;
    }

    private byte[] patchTConstuctSlotHighlight(byte[] bytecode) {
        String methodName = "onRenderOverlay";
        String methodDescription = "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Pre;)V";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName) && method.desc.equals(methodDescription)) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                VarInsnNode xInsertNode = null;
                VarInsnNode yInsertNode = null;
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    // Update hotbar size
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                        IntInsnNode intNode = (IntInsnNode) currentNode;
                        if (intNode.operand == 9)
                            intNode.operand = Config.numberOfHotbars * 9;
                    }
                    // Get node that stores the x variable
                    if (currentNode.getOpcode() == Opcodes.ISTORE) {
                        VarInsnNode varNode = (VarInsnNode) currentNode;
                        if (varNode.var == 6)
                            xInsertNode = varNode;
                    }
                    // Get node that stores the z(y) variable
                    if (currentNode.getOpcode() == Opcodes.ISTORE) {
                        VarInsnNode varNode = (VarInsnNode) currentNode;
                        if (varNode.var == 7)
                            yInsertNode = varNode;
                    }
                }
                // Call helper function to correct x
                InsnList callXMethod = new InsnList();
                callXMethod.add(new VarInsnNode(Opcodes.ILOAD, 5)); // slot
                callXMethod.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/rolandoislas/"+MultiHotbar.MODID+"/HotBarRenderer",
                        "getXForSlot", "(I)I", false));
                callXMethod.add(new VarInsnNode(Opcodes.ISTORE, 6)); // x
                method.instructions.insert(xInsertNode, callXMethod);
                // y helper fuctiom
                InsnList callYMethod = new InsnList();
                callYMethod.add(new VarInsnNode(Opcodes.ILOAD, 5)); // slot
                callYMethod.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/rolandoislas/"+MultiHotbar.MODID+"/HotBarRenderer",
                        "getYForSlot", "(I)I", false));
                callYMethod.add(new VarInsnNode(Opcodes.ISTORE, 7)); // y
                method.instructions.insert(yInsertNode, callYMethod);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchMiddleClick(byte[] bytecode, boolean deobfuscated) {
        String methodName = deobfuscated ? "middleClickMouse" : "ao";
        String methodnameSrg = "func_147112_ai";
        String methodDescription = "()V";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            // Update middle mouse click so the correct slot is selected
            if ((method.name.equals(methodName) || (deobfuscated && method.name.equals(methodnameSrg))) &&
                    method.desc.equals(methodDescription)) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                VarInsnNode varInsertNode = null;
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    // Get node that stores the slot variable
                    if (currentNode.getOpcode() == Opcodes.ISTORE) {
                        VarInsnNode varNode = (VarInsnNode) currentNode;
                        if (varNode.var == 2)
                             varInsertNode = varNode;
                    }
                }
                InsnList callMethod = new InsnList();
                callMethod.add(new VarInsnNode(Opcodes.ILOAD, 2));
                callMethod.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/rolandoislas/"+MultiHotbar.MODID+"/ClassTransformer",
                        "getCorrectedSlot", "(I)I", false));
                callMethod.add(new VarInsnNode(Opcodes.ISTORE, 2));
                method.instructions.insert(varInsertNode, callMethod);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @SuppressWarnings("unused")
    public static int getCorrectedSlot(int slot) {
        /*
            36-44 are the original 9 hotbar slots
            9-n are any beyond 9
         */
        return (slot >= 36 && slot <= 44) ? slot : slot - 45 + 9;
    }

    private byte[] patchPickBlock(byte[] bytecode) {
        String methodName = "onPickBlock";
        String methodDescription = "(Lnet/minecraft/util/MovingObjectPosition;" +
                "Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;)Z";
        String methodDescriptionObfuscated = "(Lazu;Lyz;Lahb;)Z";
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            /*
                update onPickBlock to use correct hotbar size
             */
            if (method.name.equals(methodName) && (method.desc.equals(methodDescription) ||
                    method.desc.equals(methodDescriptionObfuscated))) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                        ((IntInsnNode) currentNode).operand = Config.numberOfHotbars * 9;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchCreativeInventory(byte[] bytecode, boolean deobfuscated) {
        String methodName = deobfuscated ? "processCreativeInventoryAction" : "a";
        String methodDescription = deobfuscated ?
                "(Lnet/minecraft/network/play/client/C10PacketCreativeInventoryAction;)V" : "a(Ljm;)V";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            /*
                Update processCreativeInventoryAction()'s slot number check so that the new hotbar size does
                not inflate the upper bounds. The check seems to be "slot < 36 + hotbar size". This results in 45
                in vanilla because the 36 and hotbar size (9) are static.
             */
            if (method.name.equals(methodName) && method.desc.equals(methodDescription)) {
                Iterator<AbstractInsnNode> instructions = method.instructions.iterator();
                while (instructions.hasNext()) {
                    AbstractInsnNode currentNode = instructions.next();
                    if (currentNode.getOpcode() == Opcodes.BIPUSH) {
                        ((IntInsnNode) currentNode).operand = 45 - Config.numberOfHotbars * 9;
                        break;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchPlayerInventory(byte[] bytecode, boolean deobfuscated) {
        String methodName = deobfuscated ? "getHotbarSize" : "i";
        String methodDescription = "()I";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytecode);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            // Extend getHotbarSize slots
            if (method.name.equals(methodName) && method.desc.equals(methodDescription)) {
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
