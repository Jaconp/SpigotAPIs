package com.stickms.api.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

@SuppressWarnings("ConstantConditions")
public class CommandUtil {
    public static CommandMap getCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        return (CommandMap) field.get(Bukkit.getServer());
    }

    public static void registerCommand(String cmdName, JavaPlugin plugin, TabExecutor tabExecutor){
        plugin.getCommand(cmdName).setExecutor(tabExecutor);
        plugin.getCommand(cmdName).setTabCompleter(tabExecutor);
    }
    public static void registerCommand(String cmdName, JavaPlugin plugin, CommandExecutor commandExecutor){
        plugin.getCommand(cmdName).setExecutor(commandExecutor);
    }
    public static void registerCommand(String cmdName, JavaPlugin plugin, CommandExecutor commandExecutor, TabCompleter completer){
        plugin.getCommand(cmdName).setExecutor(commandExecutor);
        plugin.getCommand(cmdName).setTabCompleter(completer);
    }
    public static void deregisterCommand(String command, JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        deregisterCommand(plugin.getCommand(command));
    }
    public static void deregisterCommand(Command command) throws NoSuchFieldException, IllegalAccessException {
        command.unregister(getCommandMap());
    }
}
