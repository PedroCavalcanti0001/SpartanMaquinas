package me.zkingofkill.spartan.listeners;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;

public class onBlockBreak implements Listener {
    public FileConfiguration f = Maquinas.getInstance().getConfig();

    @EventHandler
    public void onBreakMaquina(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        Location loc = block.getLocation();

        for (Map.Entry<String, Props> entry : new HashMap<>(Cache.props).entrySet()) {
            if (block.getType().equals(entry.getValue().getItem().getType())) {
                Maquina maquina = Cache.getMaquinaByLoc(loc);
                if (maquina != null) {
                    if (p.getName().equals(maquina.getDono()) || p.isOp()) {
                        int slots = Maquinas.getInstance().VerificarSlots(p.getInventory(), maquina.getProps().getItem());
                        if (p.isSneaking()) {
                            loc.getWorld().dropItem(loc, new ItemStackBuilder(maquina.getProps().getItem()).setAmount(maquina.getQuantidade()).build());
                            p.sendMessage("§f[§6§lMaquinas§f] §bVocê quebrou x" + maquina.getQuantidade() + " maquinas de " + maquina.getsId() + ".");

                            maquina.getHologram().getProps().delete();
                            Cache.toremove.add(maquina.getId());
                            Cache.maquinasnochao.remove(maquina.getId());
                            e.setCancelled(false);
                            e.getBlock().setType(Material.AIR);
                        } else {
                            p.sendMessage("§f[§6§lMaquinas§f] §bVocê quebrou x1 maquinas de " + maquina.getsId() + ".");
                            if (maquina.getQuantidade() >= 2) {
                                e.setCancelled(true);
                                maquina.setQuantidade(maquina.getQuantidade() - 1);
                            } else {
                                maquina.getHologram().getProps().delete();
                                Cache.toremove.add(maquina.getId());
                                Cache.maquinasnochao.remove(maquina.getId());
                                e.getBlock().setType(Material.AIR);
                            }
                            loc.getWorld().dropItem(loc, new ItemStackBuilder(maquina.getProps().getItem()).setAmount(1).build());
                        }
                    } else {
                        e.setCancelled(true);
                        p.sendMessage("§f[§6§lMaquinas§f] §cApenas o dono dessa maquina (" + maquina.getDono() + ") pode quebrar-la.");
                    }
                }
            }
        }
    }

}
