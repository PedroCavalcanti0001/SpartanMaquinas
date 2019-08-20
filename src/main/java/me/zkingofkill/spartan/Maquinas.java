package me.zkingofkill.spartan;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import fr.minuskube.inv.InventoryManager;
import me.zkingofkill.spartan.commands.Maquina;
import me.zkingofkill.spartan.engines.EngineProps;
import me.zkingofkill.spartan.listeners.onBlockBreak;
import me.zkingofkill.spartan.listeners.onBlockPlace;
import me.zkingofkill.spartan.listeners.onChatEvent;
import me.zkingofkill.spartan.listeners.onInteract;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.mysql.MySql;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Maquinas extends JavaPlugin {

    private static Maquinas instance;
    private static InventoryManager inventoryManager;
    private static Economy economy = null;


    public static Cache getCache() {
        return new Cache();
    }

    public static Maquinas getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        Cache.init();
        MySql.criarTabela();
        EngineProps.init();
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        Bukkit.getPluginManager().registerEvents(new onBlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new onBlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new onInteract(), this);
        Bukkit.getPluginManager().registerEvents(new onChatEvent(), this);
        setupEconomy();
        getCommand("maquina").setExecutor(new Maquina());
        HologramsAPI.getHolograms(instance).forEach(Hologram::delete);
        new BukkitRunnable() {
            @Override
            public void run() {
                MySql.carregarMaquinas();
            }
        }.runTaskLater(this, 20);
    }

    @Override
    public void onDisable() {
        Cache.save();
    }

    public int VerificarSlots(Inventory inventory, ItemStack... search) {
        List<ItemStack> itens = Arrays.asList(search);
        int slots = 0;
        ItemStack[] contents = inventory.getContents();
        for (ItemStack item : contents) {
            if (item != null) {
                slots += itens.stream().filter(w -> w.getType().equals(item.getType()) && w.getItemMeta().equals(item.getItemMeta())).mapToInt(w -> (64 - item.getAmount())).sum();
            } else {
                slots += 64;
            }
        }
        return slots;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
