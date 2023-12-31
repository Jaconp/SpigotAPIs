package com.stickms.api.enchants;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public final class EnchantUtil {
    public static final Set<Enchantment> DEFAULT_ENCHANTMENTS = Set.of(Enchantment.values());
    private EnchantUtil() {throw new UnsupportedOperationException("Utility class cannot be initiated.");}

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
        if (itemMeta.getEnchantLevel(enchantment) != level) {
            itemMeta.addEnchant(enchantment, level, true);
        }
        if (DEFAULT_ENCHANTMENTS.contains(enchantment)){
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
    public static boolean hasEnchantMainHand(Player player, Enchantment enchantment){
        return player.getInventory().getItemInMainHand() != null &&
                player.getInventory().getItemInMainHand().containsEnchantment(enchantment);
    }
    public static String romanNumeral(int level){
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] hrns = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] ths = {"", "M", "MM", "MMM"};
        return ths[level / 1000] + hrns[(level % 1000) / 100] + tens[(level % 100) / 10] + ones[level % 10];
    }
}
