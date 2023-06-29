package com.stickms.api.enchants;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EnchantBuilder{
    private final Plugin plugin;
    private final NamespacedKey key;
    private String name;
    private int startLevel;
    private int maxLevel;
    private EnchantmentTarget target;
    private boolean treasure;
    private boolean cursed;
    private Enchantment[] conflicts;
    private Predicate<ItemStack> canEnchant;
    private List<EnchantListener> enchantListeners;

    public EnchantBuilder(Plugin plugin, String key){this(plugin, key, false);}
    public EnchantBuilder(Plugin plugin, String key, boolean minecraftNamespace){
        this.plugin = plugin;
        this.key = minecraftNamespace ? NamespacedKey.minecraft(key) : new NamespacedKey(plugin, key);
        this.name = key;
        this.startLevel = 1;
        this.maxLevel = 1;
        this.target = EnchantmentTarget.ALL;
        this.treasure = false;
        this.cursed = false;
        this.conflicts = new Enchantment[0];
        this.canEnchant = itemStack->false;
        this.enchantListeners = new ArrayList<>();
    }

    public EnchantWrapper build(){
        EnchantWrapper enchantWrapper = new EnchantWrapper(
                key,
                name, startLevel, maxLevel, target, treasure, cursed,
                conflicts, canEnchant);
        this.enchantListeners.add(new EnchantListener(){
            @EventHandler(ignoreCancelled = true)
            public void onEnchant(EnchantItemEvent e){
                if (!e.getEnchantsToAdd().containsKey(enchantment)) return;
                EnchantUtil.applyEnchantment(e.getItem(), enchantment, e.getEnchantsToAdd().get(enchantment));
            }
        });
        Enchantment alreadyThere = Enchantment.getByKey(key);
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
            enchantWrapper.setEnchantListeners(enchantListeners);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        return enchantWrapper;
    }

    public EnchantBuilder setName(String name){
        this.name = name;
        return this;
    }
    public EnchantBuilder setLevelRange(int startLevel, int maxLevel){
        this.startLevel = startLevel;
        this.maxLevel = maxLevel;
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
    public EnchantBuilder conflicts(Enchantment... conflicts){
        this.conflicts = conflicts;
        return this;
    }
    public EnchantBuilder canEnchant(Material... canEnchantMaterials){
        this.canEnchant = itemStack -> Arrays.stream(canEnchantMaterials)
                .anyMatch(material -> itemStack.getType().equals(material));
        return this;
    }
    public EnchantBuilder canEnchant(Predicate<ItemStack> canEnchant){
        this.canEnchant = canEnchant;
        return this;
    }

    public EnchantBuilder addListener(EnchantListener listener){
        this.enchantListeners.add(listener);
        return this;
    }
    public EnchantBuilder addListeners(EnchantListener... listeners){
        this.enchantListeners.addAll(Arrays.asList(listeners));
        return this;
    }
    public EnchantBuilder addListeners(List<EnchantListener> listeners){
        this.enchantListeners.addAll(listeners);
        return this;
    }
    public EnchantBuilder setListeners(EnchantListener... listeners){
        this.enchantListeners = new ArrayList<>(Arrays.asList(listeners));
        return this;
    }
    public EnchantBuilder setListeners(List<EnchantListener> listeners){
        this.enchantListeners = new ArrayList<>(listeners);
        return this;
    }
}
