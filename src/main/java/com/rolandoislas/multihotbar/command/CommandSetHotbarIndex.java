package com.rolandoislas.multihotbar.command;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandSetHotbarIndex implements ICommand {
    @Override
    public String getName() {
        return "multihotbar:setHotbarIndex";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/multihotbar:setHotbarIndex <integer index>";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
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
