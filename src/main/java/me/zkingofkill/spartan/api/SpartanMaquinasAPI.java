package me.zkingofkill.spartan.api;

import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Combustivel;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpartanMaquinasAPI {

    public static void give(Player player, Props maquina, int quantidade){
        ItemStack it = new ItemStackBuilder(maquina.getItem()).setAmount(quantidade).build();
        player.getInventory().addItem(it);
    }

    public static void giveComb(Player player, Combustivel combustivel, int quantidade){
        combustivel.setLitros(quantidade);
        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(combustivel.getItem());
        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
        if (tag.hasKey("litros")) {
            tag.remove("litros");
        }
        tag.setInt("litros", quantidade);
        stack.setTag(tag);
        ItemStack it = new ItemStackBuilder(CraftItemStack.asCraftMirror(stack)).setName(combustivel.getItem().getItemMeta().getDisplayName().replace("{litros}", quantidade + "L")).setAmount(1).build();
        player.getInventory().addItem(it);
    }

    public static Props getMaquina(String maquina){
        return Cache.getMaquinaByName(maquina);
    }

    public static Combustivel getCombustivel(int combustivel) { return Cache.getCombustivelById(combustivel);}

}
