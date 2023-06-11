package com.stickms.api.enchants;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class EnchantWrapper extends Enchantment {
    private final String name;
    private final int startLevel;
    private final int maxLevel;
    private final EnchantmentTarget target;
    private final boolean treasure;
    private final boolean cursed;
    private final Function<Enchantment, Boolean> conflicts;
    private final Function<ItemStack, Boolean> canEnchant;
    private List<EnchantListener> enchantListeners;

    public EnchantWrapper(
            NamespacedKey key,
            String name, int startLevel, int maxLevel, EnchantmentTarget target, boolean treasure, boolean cursed,
            Function<Enchantment, Boolean> conflicts, Function<ItemStack, Boolean> canEnchant) {
        super(key);
        this.name = name;
        this.startLevel = startLevel;
        this.maxLevel = maxLevel;
        this.target = target;
        this.treasure = treasure;
        this.cursed = cursed;
        this.conflicts = conflicts;
        this.canEnchant = canEnchant;
    }

    @Override
    public @NotNull String getName() {return name;}

    @Override
    public int getStartLevel() {return startLevel;}

    @Override
    public int getMaxLevel() {return maxLevel;}

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {return target;}

    @Override
    public boolean isTreasure() {return treasure;}

    @Override
    public boolean isCursed() {return cursed;}

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {return conflicts.apply(enchantment);}

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {return canEnchant.apply(itemStack);}

    public List<EnchantListener> getEnchantListeners() {return enchantListeners;}
    public void setEnchantListeners(List<EnchantListener> enchantListeners) {this.enchantListeners = enchantListeners;}
}
