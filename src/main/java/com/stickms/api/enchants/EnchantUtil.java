package com.stickms.api.enchants;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;

public class EnchantUtil {
    public static boolean areEqual(Enchantment first, Enchantment second){
        if (first == null || second == null) return false;
        if (!(first.getKey().equals(second.getKey()) && first.getName().equals(second.getName())
                && first.getStartLevel() == second.getStartLevel() && first.getMaxLevel() == second.getMaxLevel()
                && first.getItemTarget() == second.getItemTarget() && first.isTreasure() == second.isTreasure()
                && first.isCursed() == second.isCursed() && first.getClass() == second.getClass())) return false;
        for (Enchantment enchantment : Enchantment.values()){
            if (first.conflictsWith(enchantment) != second.conflictsWith(enchantment)) return false;
        }
        for (Material material : Material.values()){
            if (!material.isItem()) continue;
            if (first.canEnchantItem(new ItemStack(material)) != second.canEnchantItem(new ItemStack(material))) return false;
        }
        return true;
    }

    public static void deregister(NamespacedKey enchantKey){
        Enchantment enchantment = Enchantment.getByKey(enchantKey);
        if (enchantment == null) return;
        deregister(enchantment);
    }
    public static void deregister(@NotNull Enchantment enchantment){
        if (enchantment instanceof EnchantWrapper wrapper){
            for (EnchantListener listener : wrapper.getEnchantListeners()) HandlerList.unregisterAll(listener);
        }
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");
            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
            byKey.remove(enchantment.getKey());

            Field nameField = Enchantment.class.getDeclaredField("byName");
            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
            byName.remove(enchantment.getName());
        } catch (NoSuchFieldException | IllegalAccessException ignored) { }
    }
}
