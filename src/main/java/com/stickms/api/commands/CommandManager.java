package com.stickms.api.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandManager extends BukkitCommand {
    private List<SubCommand> subCommands;
    public CommandManager(String command, String[] aliases, String description, String usage){
        this(command, aliases, description, usage, null);
    }

    public CommandManager(String command, String[] aliases, String description, String usage, String permission) {
        super(command);
        this.setAliases(Arrays.asList(aliases));
        this.setDescription(description);
        this.setUsage(usage);
        if (permission != null){
            this.setPermission(permission);
            this.setPermissionMessage(ChatColor.RED + "You have no permission to use this.");
        }
        try {
            CommandUtil.getCommandMap().register(command, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.subCommands = new ArrayList<>();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        for (SubCommand subCommand : this.subCommands) if (subCommand.validate(sender, args)) return false;
        execute(sender, args);
        if (sender instanceof Player) playerExecute((Player) sender, args);
        else if (sender instanceof ConsoleCommandSender) consoleExecute((ConsoleCommandSender) sender, args);
        else if (sender instanceof BlockCommandSender) commandBlockExecute((BlockCommandSender) sender, args);
        return false;
    }
    public void execute(CommandSender sender, String[] args){}
    public void playerExecute(Player player, String[] args){}
    public void consoleExecute(ConsoleCommandSender console, String[] args){}
    public void commandBlockExecute(BlockCommandSender block, String[] args){}

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return onTabComplete(sender, args);
    }
    public List<String> onTabComplete(CommandSender sender, String[] args){return new ArrayList<>();}

    public void addSubCommands(SubCommand... subCommands){this.subCommands.addAll(Arrays.asList(subCommands));}
}
