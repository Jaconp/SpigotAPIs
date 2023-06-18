package com.stickms.api.enchants;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EnchantBuilder {
    private final NamespacedKey key;
    private String name;
    private int startLevel;
    private int maxLevel;
    private EnchantmentTarget target;
    private boolean treasure;
    private boolean cursed;
    private Function<Enchantment, Boolean> conflicts;
    private Function<ItemStack, Boolean> canEnchant;
    private List<EnchantListener> enchantListeners;

    public EnchantBuilder(String minecraftKey){this(NamespacedKey.minecraft(minecraftKey));}
    public EnchantBuilder(Plugin plugin, String key){this(new NamespacedKey(plugin, key));}
    public EnchantBuilder(NamespacedKey key){
        this.key = key;
        this.name = key.getKey();
        this.startLevel = 1;
        this.maxLevel = 1;
        this.target = EnchantmentTarget.ALL;
        this.treasure = false;
        this.cursed = false;
        this.conflicts = (enchantment)-> false;
        this.canEnchant = (itemStack)->false;
        this.enchantListeners = new ArrayList<>();
    }

    public Enchantment build(Plugin plugin){
        Enchantment enchantWrapper = new EnchantWrapper(
                key,
                name, startLevel, maxLevel, target, treasure, cursed,
                conflicts, canEnchant);
        addListener(new EnchantListener(){
            @EventHandler(ignoreCancelled = true)
            public void onEnchant(EnchantItemEvent e){
                if (!e.getEnchantsToAdd().containsKey(enchantment)) return;
                e.setCancelled(true);
                for (Map.Entry<Enchantment, Integer> entry : e.getEnchantsToAdd().entrySet())
                    EnchantUtil.applyEnchantment(e.getItem(), entry.getKey(), entry.getValue());
            }
        });
        Enchantment alreadyThere = Enchantment.getByKey(key);
        if (alreadyThere == null || !EnchantUtil.areEqual(enchantWrapper, alreadyThere)){
            if (alreadyThere != null) EnchantUtil.deregister(alreadyThere);
            try {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(null, true);
                Enchantment.registerEnchantment(enchantWrapper);
                for (EnchantListener enchantListener : enchantListeners){
                    enchantListener.setEnchantment(enchantWrapper);
                    Bukkit.getPluginManager().registerEvents(enchantListener, plugin);
                }
                ((EnchantWrapper) enchantWrapper).setEnchantListeners(enchantListeners);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }else {
            enchantWrapper = alreadyThere;
        }

        return enchantWrapper;
    }

    public EnchantBuilder setName(String name){
        this.name = name;
        return this;
    }
    public EnchantBuilder setStartLevel(int startLevel){
        this.startLevel = startLevel;
        return this;
    }
    public EnchantBuilder setMaxLevel(int maxLevel){
        this.maxLevel = maxLevel;
        return this;
    }
    public EnchantBuilder setTarget(EnchantmentTarget target){
        this.target = target;
        return this;
    }
    public EnchantBuilder setTreasure(boolean treasure){
        this.treasure = treasure;
        return this;
    }
    public EnchantBuilder setCursed(boolean cursed){
        this.cursed = cursed;
        return this;
    }
    public EnchantBuilder conflicts(Function<Enchantment, Boolean> conflicts){
        this.conflicts = conflicts;
        return this;
    }
    public EnchantBuilder canEnchant(Function<ItemStack, Boolean> canEnchant){
        this.canEnchant = canEnchant;
        return this;
    }
    public EnchantBuilder addListener(EnchantListener listener){
        this.enchantListeners.add(listener);
        return this;
    }
    public EnchantBuilder setListeners(List<EnchantListener> listeners){
        this.enchantListeners = listeners;
        return this;
    }
}
