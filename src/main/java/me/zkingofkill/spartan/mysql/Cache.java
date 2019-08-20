package me.zkingofkill.spartan.mysql;

import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.objects.Combustivel;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Cache {
    public static HashMap<Integer, Maquina> maquinasnochao = new HashMap<>();
    public static List<Integer> toremove = new ArrayList<>();
    public static HashMap<String, Props> props = new HashMap<>();
    public static HashMap<Player, Maquina> amiggosadding = new HashMap<>();

    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(Maquinas.getInstance(), 20 * 60 * 10, 20 * 60 * 10);
    }

    public static void save() {
        toremove.forEach(integer -> {
            if (MySql.HasInDatabase(integer)) {
                MySql.deleteMaquina(integer);
                Cache.toremove.remove(integer);
            }
        });
        new HashMap<>(maquinasnochao).forEach(MySql::insertMaquina);
        System.out.println("salvando maquinas: " + maquinasnochao.size());
    }

    public static Maquina getMaquinaByLoc(Location location) {
        return maquinasnochao.values().stream().filter(m -> m.getLocation().equals(Utils.getUtils().locationToString(location))).findAny().orElse(null);
    }

    public static Props getMaquinaByName(String name) {
        return props.values().stream().filter(m -> m.getsId().equals(name)).findAny().orElse(null);
    }

    public static Combustivel getCombustivelById(int id) {
        Combustivel combustivel = null;
        for (Props props : props.values()) {
            for (Combustivel combustivel1 : props.getCombustiveis()) {
                if (combustivel1.getId() == id) combustivel = combustivel1;
            }
        }
        return combustivel;
    }

    public static int generateID() {
        if (maquinasnochao.size() >= 1) {
            return Collections.max(maquinasnochao.keySet().stream().map(Integer::new).collect(Collectors.toList())) + 1;
        } else {
            return 0;
        }
    }

    public static void removeMaquina(Maquina maquina) {
        for (Maquina entry : new HashMap<>(maquinasnochao).values()) {
            if (entry.equals(maquina)) {
                maquinasnochao.remove(entry);
            }
        }
    }
}
