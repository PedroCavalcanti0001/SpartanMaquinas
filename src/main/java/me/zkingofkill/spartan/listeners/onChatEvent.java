package me.zkingofkill.spartan.listeners;

import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.rankup.api.RankupAPI;
import me.zkingofkill.spartan.rankup.objects.Rank;
import me.zkingofkill.spartan.rankup.objects.User;
import me.zkingofkill.spartan.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Map;

public class onChatEvent implements Listener {

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        for (Map.Entry<Player, Maquina> entry : Cache.amiggosadding.entrySet()) {
            if (entry.getKey().equals(p)) {
                Player pd = Bukkit.getPlayer(e.getMessage());
                Maquina m = Cache.getMaquinaByLoc(Utils.getUtils().stringToLocation(entry.getValue().getLocation()));
                ArrayList<String> list = m.getAmigos();
                if (e.getMessage().equalsIgnoreCase("cancelar")) {
                    Cache.amiggosadding.remove(entry.getKey());
                    p.sendMessage("§f[§6§lMaquinas§f] §aCancelado com sucesso!");
                } else if (pd != null
                        || list.contains(e.getMessage())) {
                    User user = RankupAPI.getCache().getUserByPlayer(pd);
                    if (user.getRank().getPosition() >= entry.getValue().getProps().getRankMinimo() || user.getPrestigy() >= 1  || p.hasPermission("spartanmaquinas.*")) {
                        if (list.contains(e.getMessage())) {
                            list.remove(e.getMessage());
                            Cache.amiggosadding.remove(entry.getKey());
                            p.sendMessage("§f[§6§lMaquinas§f] §a" + e.getMessage() + " removido com sucesso!");
                        } else {
                            list.add(pd.getName());
                            Cache.amiggosadding.remove(entry.getKey());
                            p.sendMessage("§f[§6§lMaquinas§f] §a" + e.getMessage() + " adicionado com sucesso!");
                        }
                        m.setAmigos(list);
                    } else {
                        p.sendMessage("§f[§6§lMaquinas§f] §cEsse jogador não pode ser adicionado nessa maquina, pois seu rank é inferior ao rank minimo da maquina.");
                    }
                } else {
                    p.sendMessage("§f[§6§lMaquinas§f] §cNão existe nenhum player na maquina ou online com esse nick!");
                    p.sendMessage("§f[§6§lMaquinas§f] §aDigite §c§lcancelar §apara cancelar.");
                }

                e.setCancelled(true);
            }
        }
    }
}
