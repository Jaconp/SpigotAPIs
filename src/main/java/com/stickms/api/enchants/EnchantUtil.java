package com.stickms.api.enchants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("ConstantConditions")
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
    public static ItemStack applyEnchantment(ItemStack itemStack, Enchantment enchantment, int level){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        if (getDefaultEnchants().contains(enchantment)){
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if (!itemMeta.hasLore()) {
            itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + enchantment.getName() + " " + romanNumeral(level)));
        } else {
            List<String> lore = itemMeta.getLore();
            lore.add(ChatColor.GRAY + enchantment.getName() + " " + romanNumeral(level));
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public static List<Enchantment> getDefaultEnchants(){
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        enchantments.add(Enchantment.PROTECTION_FIRE);
        enchantments.add(Enchantment.PROTECTION_FALL);
        enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
        enchantments.add(Enchantment.PROTECTION_PROJECTILE);
        enchantments.add(Enchantment.OXYGEN);
        enchantments.add(Enchantment.WATER_WORKER);
        enchantments.add(Enchantment.THORNS);
        enchantments.add(Enchantment.DEPTH_STRIDER);
        enchantments.add(Enchantment.FROST_WALKER);
        enchantments.add(Enchantment.BINDING_CURSE);
        enchantments.add(Enchantment.DAMAGE_ALL);
        enchantments.add(Enchantment.DAMAGE_UNDEAD);
        enchantments.add(Enchantment.DAMAGE_ARTHROPODS);
        enchantments.add(Enchantment.KNOCKBACK);
        enchantments.add(Enchantment.FIRE_ASPECT);
        enchantments.add(Enchantment.LOOT_BONUS_MOBS);
        enchantments.add(Enchantment.SWEEPING_EDGE);
        enchantments.add(Enchantment.DIG_SPEED);
        enchantments.add(Enchantment.SILK_TOUCH);
        enchantments.add(Enchantment.DURABILITY);
        enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
        enchantments.add(Enchantment.ARROW_DAMAGE);
        enchantments.add(Enchantment.ARROW_KNOCKBACK);
        enchantments.add(Enchantment.ARROW_FIRE);
        enchantments.add(Enchantment.ARROW_INFINITE);
        enchantments.add(Enchantment.LUCK);
        enchantments.add(Enchantment.LURE);
        enchantments.add(Enchantment.LOYALTY);
        enchantments.add(Enchantment.IMPALING);
        enchantments.add(Enchantment.RIPTIDE);
        enchantments.add(Enchantment.CHANNELING);
        enchantments.add(Enchantment.MULTISHOT);
        enchantments.add(Enchantment.QUICK_CHARGE);
        enchantments.add(Enchantment.PIERCING);
        enchantments.add(Enchantment.MENDING);
        enchantments.add(Enchantment.VANISHING_CURSE);
        enchantments.add(Enchantment.SWIFT_SNEAK);
        enchantments.add(Enchantment.SOUL_SPEED);
        return enchantments;
    }
    public static String romanNumeral(int level){
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] hrns = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] ths = {"", "M", "MM", "MMM"};

        return ths[level / 1000] + hrns[(level % 1000) / 100] + tens[(level % 100) / 10] + ones[level % 10];
    }
}
