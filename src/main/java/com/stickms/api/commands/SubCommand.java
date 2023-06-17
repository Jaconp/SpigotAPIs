package com.stickms.api.commands;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public abstract class SubCommand {
    private final int argsLength;
    private final BiFunction<CommandSender, String[], Boolean> requirements;
    public SubCommand(BiFunction<CommandSender, String[], Boolean> requirements){this(0, requirements);}
    public SubCommand(int argsLength, BiFunction<CommandSender, String[], Boolean> requirements){
        this.argsLength = argsLength;
        this.requirements = requirements;
    }

    public boolean validate(CommandSender sender, String[] args){
        if (args.length != argsLength || !requirements.apply(sender, args)) return false;
        execute(sender, args);
        if (sender instanceof Player) playerExecute((Player) sender, args);
        else if (sender instanceof ConsoleCommandSender) consoleExecute((ConsoleCommandSender) sender, args);
        else if (sender instanceof BlockCommandSender) commandBlockExecute((BlockCommandSender) sender, args);
        return true;
    }

    public void execute(CommandSender sender, String[] args){};
    public void playerExecute(Player player, String[] args){}
    public void consoleExecute(ConsoleCommandSender console, String[] args){}
    public void commandBlockExecute(BlockCommandSender block, String[] args){}
}
