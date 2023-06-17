package com.stickms.api.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.Map;

public class EnchantListener implements Listener{
    protected Enchantment enchantment;

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e){
        if (!e.getEnchantsToAdd().containsKey(enchantment)) return;
        e.setCancelled(true);
        for (Map.Entry<Enchantment, Integer> entry : e.getEnchantsToAdd().entrySet())
            EnchantUtil.applyEnchantment(e.getItem(), entry.getKey(), entry.getValue());
    }

    public void setEnchantment(Enchantment enchantment) {this.enchantment = enchantment;}
}
