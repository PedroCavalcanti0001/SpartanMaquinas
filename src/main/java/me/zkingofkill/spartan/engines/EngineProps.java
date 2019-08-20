package me.zkingofkill.spartan.engines;

import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Combustivel;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EngineProps {

    public static void init() {
        //Carrega as propriedades das maquinas
        for (String section : Maquinas.getInstance().getConfig().getConfigurationSection("Maquinas").getKeys(false)) {
            String id = section;
            String nome = Maquinas.getInstance().getConfig().getString("Maquinas." + id + ".Nome");
            int mat = Integer.parseInt(Maquinas.getInstance().getConfig().getString("Maquinas." + id + ".Item").split(":")[0]);
            int mat_data = Integer.parseInt(Maquinas.getInstance().getConfig().getString("Maquinas." + id + ".Item").split(":")[1]);
            ItemStack item = new ItemStackBuilder(Material.getMaterial(mat)).setDurability(mat_data).setName(nome.replace("&", "§")).build();
            int stackmax = Maquinas.getInstance().getConfig().getInt("Maquinas." + id + ".StackMax");
            int litrosmax = Maquinas.getInstance().getConfig().getInt("Maquinas." + id + ".LitrosMax");
            double Delay = Maquinas.getInstance().getConfig().getDouble("Maquinas." + id + ".Delay");
            int Consumo = Maquinas.getInstance().getConfig().getInt("Maquinas." + id + ".Consumo");
            int dropsqnt = Maquinas.getInstance().getConfig().getInt("Maquinas." + id + ".Drops");
            int rankminimo = Maquinas.getInstance().getConfig().getInt("Maquinas." + id + ".RankMinimo");
            double Valor = Maquinas.getInstance().getConfig().getDouble("Maquinas." + id + ".Valor");
            List<String> list = Maquinas.getInstance().getConfig().getStringList("Maquinas." + id + ".Combustiveis");
            List<Combustivel> combustiveis = new ArrayList<>();
            for (String c : list) {
                for (String Comb : Maquinas.getInstance().getConfig().getConfigurationSection("Combustiveis").getKeys(false)) {
                    if (c.equals(Comb)) {
                        String cnome = Maquinas.getInstance().getConfig().getString("Combustiveis." + Comb + ".Nome");
                        int cmat = Integer.parseInt(Maquinas.getInstance().getConfig().getString("Combustiveis." + Comb + ".Item").split(":")[0]);
                        int cmat_data = Integer.parseInt(Maquinas.getInstance().getConfig().getString("Combustiveis." + Comb + ".Item").split(":")[1]);
                        List<String> lore = Maquinas.getInstance().getConfig().getStringList("Combustiveis." + Comb + ".Lore");
                        ItemStack citem = new ItemStackBuilder(Material.getMaterial(cmat)).setDurability((short) cmat_data).setName(cnome.replace("&", "§")).setLore(lore).build();
                        Combustivel combustivel = new Combustivel(Integer.parseInt(c), citem);
                        combustiveis.add(combustivel);
                    }
                }
            }
            Props prop = new Props(id, nome, item, litrosmax, stackmax, dropsqnt, Delay, Valor, combustiveis, Consumo, rankminimo);

            Cache.props.put(id, prop);
        }
    }
}
