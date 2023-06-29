package com.stickms.api.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;

public abstract class EnchantListener implements Listener{
    protected Enchantment enchantment;

    public void setEnchantment(Enchantment enchantment) {this.enchantment = enchantment;}
}
