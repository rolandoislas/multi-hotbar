package com.rolandoislas.multihotbar.command;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandSetHotbarOrder implements ICommand {
    @Override
    public String getCommandName() {
        return "multihotbar:setHotbarOrder";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/multihotbar:setHotbarOrder <csv order (e.g. 0,1,2,3)>|[integer] [integer] [integer] [integer]";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            StringBuilder orderString = new StringBuilder();
            for (String arg : args)
                orderString.append(arg).append(",");
            if (orderString.length() > 0)
                orderString.deleteCharAt(orderString.length() - 1);
            HotbarLogic.hotbarOrder = Config.commaIntStringToArray(orderString.toString());
        }
        catch (IllegalArgumentException e) {
            throw new CommandException("Failed to parse order arguments");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
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
