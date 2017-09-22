package com.rolandoislas.multihotbar.command;

import com.rolandoislas.multihotbar.HotbarLogic;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0)
            throw new CommandException("Reset command does not accept arguments.");
        HotbarLogic.reset(true);
        sender.addChatMessage(new ChatComponentText("Hotbar reset."));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
