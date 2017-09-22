package com.rolandoislas.multihotbar.command;

import com.rolandoislas.multihotbar.HotbarLogic;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandResetHotbar implements ICommand {
    @Override
    public String getCommandName() {
        return "multihotbar:reset";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/multihotbar:reset";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0)
            throw new CommandException("Reset command does not accept arguments.");
        HotbarLogic.reset(true);
        sender.addChatMessage(new TextComponentString("Hotbar reset."));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        ArrayList<String> list = new ArrayList<>();
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand iCommand) {
        return 0;
    }
}
