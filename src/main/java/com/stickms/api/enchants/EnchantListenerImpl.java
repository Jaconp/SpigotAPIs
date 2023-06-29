package com.stickms.api.enchants;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public abstract class EnchantListenerImpl extends EnchantListener{
    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        if (!EnchantUtil.hasEnchantMainHand(event.getPlayer(), enchantment)) return;
        this.onBlockBreak(event);
    }
    public void onBlockBreak(BlockBreakEvent event){}
    @EventHandler
    public void blockDropItem(BlockDropItemEvent event){
        if (!EnchantUtil.hasEnchantMainHand(event.getPlayer(), enchantment)) return;
        this.onBlockDropItem(event);
    }
    public void onBlockDropItem(BlockDropItemEvent event){}
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.hasEnchantMainHand(player, enchantment)) return;
        this.onEntityDamageByPlayer(player, event);
    }
    public void onEntityDamageByPlayer(Player damager, EntityDamageByEntityEvent event){}
    @EventHandler
    public void shootBow(EntityShootBowEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (!EnchantUtil.hasEnchantMainHand(player, enchantment)) return;
        this.onPlayerShootBow(player, event);
    }
    public void onPlayerShootBow(Player shooter, EntityShootBowEvent event){}
    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        if (!EnchantUtil.hasEnchantMainHand(event.getEntity(), enchantment)) return;
        this.onPlayerDeath(event);
    }
    public void onPlayerDeath(PlayerDeathEvent event){}
    @EventHandler
    public void consume(PlayerItemConsumeEvent event){
        if (!event.getItem().containsEnchantment(enchantment)) return;
        this.onConsume(event);
    }
    public void onConsume(PlayerItemConsumeEvent event){}
}
