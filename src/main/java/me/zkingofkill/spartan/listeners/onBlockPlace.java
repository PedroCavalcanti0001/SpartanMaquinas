package me.zkingofkill.spartan.listeners;

import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.api.SpartanMaquinasAPI;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.rankup.api.RankupAPI;
import me.zkingofkill.spartan.rankup.objects.Rank;
import me.zkingofkill.spartan.rankup.objects.User;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import me.zkingofkill.spartan.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Map;

public class onBlockPlace implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        Location loc = e.getBlockPlaced().getLocation();
        Block frente = e.getBlockAgainst();
        ItemStack item = p.getItemInHand();
        if (item.hasItemMeta()) {
            for (Map.Entry<String, Props> a : Cache.props.entrySet()) {
                if (item.getType().equals(a.getValue().getItem().getType()) &&
                        item.getItemMeta().equals(a.getValue().getItem().getItemMeta())) {
                    Maquina maquina = Cache.getMaquinaByLoc(frente.getLocation());
                    int mQntInv = Arrays.stream(p.getInventory().getContents())
                            .filter(i -> i != null && i.getItemMeta().equals(a.getValue().getItem().getItemMeta())
                                    && i.getType().equals(a.getValue().getItem().getType()))
                            .mapToInt(ItemStack::getAmount).sum();
                    if (maquina != null && a.getValue().getNome().equalsIgnoreCase(maquina.getProps().getNome())) {
                        e.setCancelled(true);
                        int q = a.getValue().getStackMax() - maquina.getQuantidade();
                        if (maquina.getDono().equals(p.getName())) {
                            if (p.isSneaking()) {
                                if (maquina.getQuantidade() < a.getValue().getStackMax()) {
                                    if (q != 0 && q >= mQntInv) {
                                        maquina.setQuantidade(maquina.getQuantidade() + mQntInv);
                                        p.getInventory().removeItem(new ItemStackBuilder(a.getValue().getItem()).setAmount(mQntInv).build());
                                        p.sendMessage("§f[§6§lMaquinas§f] §aVocê stackou mais " + mQntInv + " maquinas de " + maquina.getsId() + "!");
                                    } else if (q != 0) {
                                        maquina.setQuantidade(maquina.getQuantidade() + q);
                                        p.sendMessage("§f[§6§lMaquinas§f] §aVocê stackou mais " + q + " maquinas de " + maquina.getsId() + "!");
                                        p.getInventory().removeItem(new ItemStackBuilder(a.getValue().getItem()).setAmount(q).build());
                                    }
                                } else {
                                    p.sendMessage("§f[§6§lMaquinas§f] §cEssa maquina já está com a quantidade maxima de stacks! (" + a.getValue().getStackMax() + ")");
                                }
                            } else {
                                if (q >= 1) {
                                    p.sendMessage("§f[§6§lMaquinas§f] §aVocê stackou mais 1 maquina de " + maquina.getsId() + "!");
                                    maquina.setQuantidade(maquina.getQuantidade() + 1);
                                    maquina.getHologram().getProps().getVisibilityManager().resetVisibilityAll();
                                    p.getInventory().removeItem(new ItemStackBuilder(a.getValue().getItem()).setAmount(1).build());
                                } else {
                                    p.sendMessage("§f[§6§lMaquinas§f] §cEssa maquina já está com a quantidade maxima de stacks! (" + a.getValue().getStackMax() + ")");
                                }
                            }
                        } else {
                            p.sendMessage("§f[§6§lMaquinas§f] §cApenas o dono dessa maquina (" + maquina.getDono() + ") pode fazer isso.");
                        }
                    } else {
                        Maquina m = new Maquina(a.getValue().getsId(), Utils.getUtils().locationToString(loc), p.getName(), mQntInv);
                        User user = RankupAPI.getCache().getUserByPlayer(p);
                        Rank rank = RankupAPI.getCache().getByPosition(m.getProps().getRankMinimo());
                        int id = Cache.generateID();
                        m.setId(id);
                        if (user.getRank().getPosition() >= m.getProps().getRankMinimo() || user.getPrestigy() >= 1 || p.hasPermission("spartanmaquinas.*")) {
                            if (p.isSneaking()) {
                                if (mQntInv >= a.getValue().getStackMax()) {
                                    mQntInv = a.getValue().getStackMax();
                                }
                                Cache.maquinasnochao.put(id, m);
                                p.sendMessage("§f[§6§lMaquinas§f] §aVocê colocou " + mQntInv + " maquinas de " + m.getsId() + "!");
                                m.setQuantidade(mQntInv);
                                int finalMQntInv = mQntInv >= m.getProps().getStackMax() ? mQntInv-1 : mQntInv;
                                new BukkitRunnable() {
                                     @Override
                                     public void run() {
                                         p.getInventory().removeItem(new ItemStackBuilder(a.getValue().getItem()).setAmount(finalMQntInv).build());
                                     }
                                 }.runTask(Maquinas.getInstance());


                            } else {
                                m.setQuantidade(1);
                                Cache.maquinasnochao.put(id, m);
                                p.sendMessage("§f[§6§lMaquinas§f] §aMaquina de " + m.getsId() + " colocada com sucesso!");
                            }
                        } else {
                            p.sendMessage("§f[§6§lMaquinas§f] §cVocê precisa ser rank " + rank.getPrefix() + " §cou superior para usar essa maquina.");
                            e.setCancelled(true);
                        }
                    }
                }

            }
        }
    }
}
