package com.rolandoislas.multihotbar.command;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import java.util.ArrayList;
import java.util.List;

public class CommandSetHotbarIndex implements ICommand {
    @Override
    public String getCommandName() {
        return "multihotbar:setHotbarIndex";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/multihotbar:setHotbarIndex <integer index>";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new CommandException("Not enough arguments");
        else if (args.length > 1)
            throw new CommandException("Too many arguments");
        try {
            int index = Integer.parseInt(args[0]);
            if (index < 0 || index >= Config.numberOfHotbars)
                throw new CommandException("Index out of bounds");
            HotbarLogic.hotbarIndex = index;
        }
        catch (NumberFormatException e) {
            throw new CommandException("Not a valid index");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return new ArrayList<>();
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
