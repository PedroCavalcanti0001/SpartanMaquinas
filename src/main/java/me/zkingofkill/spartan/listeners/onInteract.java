package me.zkingofkill.spartan.listeners;

import me.zkingofkill.spartan.engines.EngineMenu;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class onInteract implements Listener {

    @EventHandler
    public void onInteractWithMachine(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Location loc = block.getLocation();
            for (Map.Entry<String, Props> entry : new HashMap<>(Cache.props).entrySet()) {
                if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta() &&
                        p.getItemInHand().getItemMeta().equals(entry.getValue().getItem().getItemMeta()) && p.getItemInHand().getType().equals(entry.getValue().getItem().getType())) {
                    return;
                }
                Maquina maquina = Cache.getMaquinaByLoc(loc);
                if (block.getType().equals(entry.getValue().getItem().getType())) {
                    if (maquina != null) {
                        if (maquina.getAmigos().contains(p.getName()) || maquina.getDono().equals(p.getName()) || p.hasPermission("spartanmaquinas.*")) {
                            maquina.getProps().getCombustiveis().forEach(combustivel ->{
                                    ItemStack item = p.getItemInHand();
                                    if (item != null && !item.getType().equals(Material.AIR)) {
                                        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
                                        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
                                        if (tag.hasKey("litros")) {
                                            int litros = tag.getInt("litros");
                                            combustivel.setLitros(litros);
                                            ItemStack i = new ItemStackBuilder(combustivel.getItem().getType()).setDurability(combustivel.getItem().getDurability()).setName(combustivel.getItem().getItemMeta().getDisplayName().replace("{litros}", litros + "L")).build();
                                            if (i.getType().equals(item.getType()) && i.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                                                if (maquina.getLitros() < maquina.getProps().getLitrosMax()*maquina.getQuantidade()) {
                                                    if (((maquina.getProps().getLitrosMax()*maquina.getQuantidade()) - maquina.getLitros()) >= combustivel.getLitros()) {
                                                        maquina.setLitros(maquina.getLitros() + combustivel.getLitros());
                                                        if (item.getAmount() >= 2) {
                                                            item.setAmount(item.getAmount() - 1);
                                                        } else {
                                                            p.getInventory().remove(item);
                                                        }

                                                    } else {
                                                        int q = combustivel.getLitros() - ((maquina.getProps().getLitrosMax()*maquina.getQuantidade()) - maquina.getLitros());
                                                        maquina.setLitros(maquina.getProps().getLitrosMax()*maquina.getQuantidade());
                                                        ItemStack toad = new ItemStackBuilder(item.getType()).setDurability(item.getDurability()).setName(item.getItemMeta().getDisplayName().replace(litros + "L", q + "L")).setAmount(1).build();
                                                        net.minecraft.server.v1_8_R3.ItemStack stack2 = CraftItemStack.asNMSCopy(toad);
                                                        NBTTagCompound tag2 = stack2.getTag() != null ? stack2.getTag() : new NBTTagCompound();
                                                        ItemStack toad2 = CraftItemStack.asCraftMirror(stack2);
                                                        tag2.remove("litros");
                                                        tag2.setInt("litros", q);
                                                        if (item.getAmount() >= 2) {
                                                            item.setAmount(item.getAmount() - 1);
                                                        } else if (item.getAmount() == 1) {
                                                            p.getInventory().remove(item);
                                                        }
                                                        p.getInventory().addItem(toad2);
                                                    }
                                                }
                                                if(maquina.getLitros() == (maquina.getProps().getLitrosMax()*maquina.getQuantidade()) ){
                                                    maquina.setAtiva(true);
                                                    maquina.start();
                                                }
                                                e.setCancelled(true);
                                            }else{
                                                new EngineMenu(p, maquina).open();
                                            }
                                        }
                                    }else{
                                        new EngineMenu(p, maquina).open();
                                    }
                            });
                        } else {
                            p.sendMessage("§f[§6§lMaquinas§f] §cVocê não tem permissão para usar essa maquina!");
                        }
                    }
                }
            }
        }
    }
}
