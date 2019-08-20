package me.zkingofkill.spartan.engines;

import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EngineSchedule {
    private static boolean isStart = false;

    public double getTimeLeft(Maquina maquina) {
        double f = -1;
        long now = System.currentTimeMillis();
        long cooldownTime = maquina.getTime();
        double totalTime = maquina.getProps().getDelay();
        int r = (int) (now - cooldownTime) / 1000;
        f = (r - totalTime) * (-1);
        return f;
    }

    public void start() {
        if (!isStart) {
            isStart = true;
            Bukkit.getScheduler().runTaskTimer(Maquinas.getInstance(), () -> {
                for (Maquina mc : Cache.maquinasnochao.values()) {
                        if (getTimeLeft(mc) <= 0) {
                            if (mc.isAtiva()) {
                                mc.setDrops(mc.getDrops() + (mc.getQuantidade() * mc.getProps().getDropsQnt()));
                                mc.setLitros(mc.getLitros() - (mc.getProps().getConsumo() * mc.getQuantidade()));
                                if (mc.getLitros() >= mc.getProps().getConsumo() * mc.getQuantidade()) {
                                    mc.start();
                                } else {
                                    mc.setAtiva(false);
                                    if (mc.isNotificacoes()) {
                                        Player p = Bukkit.getPlayer(mc.getDono());
                                        if (p != null) {
                                            p.sendMessage("§f[§6§lMaquinas§f] §aSua maquina de " + mc.getsId() + " (" + mc.getQuantidade() + ") acabou o combustivel.");
                                        }
                                    }
                                }
                            }
                        }


                }
            }, 20, 20);
        }
    }


}
     