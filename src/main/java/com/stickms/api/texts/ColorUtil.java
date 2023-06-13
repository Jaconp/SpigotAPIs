package com.stickms.api.texts;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern COLOR_PATTERN = Pattern.compile("(#[A-Fa-f0-9]{6})");

    public static String translateHexCode(@NotNull String string){
        Matcher matcher = COLOR_PATTERN.matcher(string);
        while (matcher.find()){
            string = string.replace(matcher.group(), net.md_5.bungee.api.ChatColor.of(matcher.group()) + "");
        }
        return string;
    }

    public static String translateHexAndBukkit(@NotNull String string){
        return ChatColor.translateAlternateColorCodes('&', translateHexCode(string));
    }

    public static net.md_5.bungee.api.ChatColor fromHexCode(@NotNull String hexCode){
        Preconditions.checkArgument(
                hexCode.length() == 7 && COLOR_PATTERN.matcher(hexCode).matches(),
                "Invalid hex code!");
        return net.md_5.bungee.api.ChatColor.of(hexCode);
    }

    public static net.md_5.bungee.api.ChatColor fromRGB(int red, int green, int blue){
        Preconditions.checkArgument(
                red<=255 && red>=0 && green<=255 && green>=0 && blue<=255 && blue>=0,
                "Invalid RGB code!");
        return net.md_5.bungee.api.ChatColor.of(new Color(red, green, blue));
    }
}
