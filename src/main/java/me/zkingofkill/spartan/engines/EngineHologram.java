package me.zkingofkill.spartan.engines;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.utils.Utils;
import org.apache.commons.lang3.StringUtils;

public class EngineHologram {

    private Hologram hologram;
    private Maquina maquina;

    public EngineHologram(Maquina maquina) {
        this.maquina = maquina;
        this.hologram = HologramsAPI.getHolograms(Maquinas.getInstance()).stream().filter(h -> h.getLocation().equals(Utils.getUtils().stringToLocation(maquina.getLocation()).add(0.5, 2.7, 0.5))).findAny().orElse(null);
    }

    public void create() {
        hologram = HologramsAPI.createHologram(Maquinas.getInstance(), Utils.getUtils().stringToLocation(maquina.getLocation()).add(0.5, 2.7, 0.5));
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<tipo:" + maquina + ">", 1.0, () -> maquina.getsId());
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<dono:" + maquina + ">", 1.0, () -> maquina.getDono());
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<comb:" + maquina + ">", 1.0, () -> getCombustivelProgress(maquina.getLitros(), maquina.getProps().getLitrosMax() * maquina.getQuantidade(), 20, "▍", "§6", "§7"));
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<qnt:" + maquina + ">", 1.0, () -> String.valueOf(maquina.getQuantidade()));
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<money:" + maquina + ">", 1.0, () -> Utils.getUtils().numberFormat(maquina.getDrops() * maquina.getProps().getValor(), 1));
        HologramsAPI.registerPlaceholder(Maquinas.getInstance(), "<status:" + maquina + ">", 1.0, () -> (maquina.isAtiva() ? "§6Ligada" : "§cDesativada"));
        hologram.appendTextLine("§aMaquina de <tipo:" + maquina + ">" + " (<qnt:" + maquina + ">)");
        hologram.appendTextLine("§f<comb:" + maquina + ">");
        hologram.appendTextLine("§aDono: §6<dono:" + maquina + ">");
        hologram.appendTextLine("§aMoney: §6<money:" + maquina + ">");
        hologram.appendTextLine("§aStatus: §6<status:" + maquina + ">");
        hologram.setAllowPlaceholders(true);
        hologram.getVisibilityManager().setVisibleByDefault(maquina.isHologramas());
    }

    private String getCombustivelProgress(int current, int max, int totalBars, String barChar, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return StringUtils.repeat(completedColor + barChar, progressBars)
                + StringUtils.repeat(notCompletedColor + barChar, totalBars - progressBars);
    }

    public Hologram getProps() {
        return hologram;
    }

    public void setHolo(Hologram hologram) {
        this.hologram = hologram;
    }

    public Maquina getMaquina() {
        return maquina;
    }

    public void setMaquina(Maquina maquina) {
        this.maquina = maquina;
    }
}
