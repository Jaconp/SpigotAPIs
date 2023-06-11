package com.stickms.api.enchants;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
        Enchantment enchantment = new EnchantWrapper(
                key,
                name, startLevel, maxLevel, target, treasure, cursed,
                conflicts, canEnchant);
        Enchantment alreadyThere = Enchantment.getByKey(key);
        if (alreadyThere == null || !EnchantUtil.areEqual(enchantment, alreadyThere)){
            try {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(null, true);
                Enchantment.registerEnchantment(enchantment);
                for (EnchantListener enchantListener : enchantListeners){
                    enchantListener.setEnchantment(enchantment);
                    Bukkit.getPluginManager().registerEvents(enchantListener, plugin);
                }
                ((EnchantWrapper) enchantment).setEnchantListeners(enchantListeners);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }else {
            enchantment = alreadyThere;
        }

        return enchantment;
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
