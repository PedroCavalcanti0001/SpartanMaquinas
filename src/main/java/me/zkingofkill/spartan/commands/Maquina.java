package me.zkingofkill.spartan.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Combustivel;
import me.zkingofkill.spartan.objects.Props;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import me.zkingofkill.spartan.utils.Utils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Maquina implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length <= 1) {
                sender.sendMessage("§cUse: /maquina dar <maquina/combustivel>");
            } else {
                if (args[1].equalsIgnoreCase("teste")) {
                    WorldEditPlugin worldEditPlugin = null;
                    worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                    if (worldEditPlugin == null) {
                        sender.sendMessage("Error with region undoing! Error: WorldEdit is null.");
                    }
                    Player player = (Player) sender;
                    Selection sel = worldEditPlugin.getSelection(player);

                    if (sel instanceof CuboidSelection) {
                        Vector min = sel.getNativeMinimumPoint();
                        Vector max = sel.getNativeMaximumPoint();
                        for (int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                            for (int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                                    Location tmpblock = new Location(player.getWorld(), x, y, z);
                                    me.zkingofkill.spartan.objects.Maquina m = new me.zkingofkill.spartan.objects.Maquina("ferro", Utils.getUtils().locationToString(tmpblock), player.getName(), 1);
                                    m.setLitros(60);
                                    m.setAtiva(true);
                                    m.start();
                                    Cache.maquinasnochao.put(Cache.generateID(), m);
                                }
                            }
                        }
                        player.sendMessage(ChatColor.AQUA + "Undo Complete!");
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
                    }
                }
                if(args[0].equalsIgnoreCase("limpar")){
                  for(Map.Entry<Integer, me.zkingofkill.spartan.objects.Maquina> entry : new HashMap<>(Cache.maquinasnochao).entrySet()){
                      Block block = Utils.getUtils().stringToLocation(entry.getValue().getLocation()).getBlock();
                      if(block == null || !block.getType().equals(entry.getValue().getProps().getItem().getType())){
                          Cache.toremove.add(entry.getValue().getId());
                          Cache.maquinasnochao.remove(entry.getValue().getId());

                      }

                  }
                    System.out.println("maquinas limpadas");
                }
                if (args[0].equalsIgnoreCase("dar") && args[1].equalsIgnoreCase("maquina")) {
                    if (args.length >= 5) {
                        Player player = Bukkit.getPlayer(args[2]);
                        Props maquina = Maquinas.getCache().getMaquinaByName(args[3]);
                        int quantidade = Integer.parseInt(args[4]);
                        if (maquina != null) {
                            if (Maquinas.getInstance().VerificarSlots(player.getInventory(), maquina.getItem()) >= quantidade) {
                                ItemStack it = new ItemStackBuilder(maquina.getItem()).setAmount(quantidade).build();
                                player.getInventory().addItem(it);
                                player.sendMessage("§aVocê recebeu uma maquina de " + maquina.getsId() + ".");
                                sender.sendMessage("§aMaquina dada com sucesso!");
                            } else {
                                sender.sendMessage("§cEsse player não tem slots suficientes no inventario.");
                            }
                        } else {
                            sender.sendMessage("§cMaquina não encontrada!");
                        }
                    } else {
                        sender.sendMessage("§cUse: /maquina dar maquina <player> <maquina> <quantidade>");
                    }
                } else if (args[0].equalsIgnoreCase("dar") && args[1].equalsIgnoreCase("combustivel")) {
                    if (args.length >= 6) {
                        Player player = Bukkit.getPlayer(args[2]);
                        int quantidade = Integer.parseInt(args[5]);
                        int litros = Integer.parseInt(args[4]);
                        Combustivel c = Cache.getCombustivelById(Integer.parseInt(args[3]));
                        ItemStack i = new ItemStackBuilder(c.clone().getItem())
                                .setName(c.getItem().getItemMeta().getDisplayName()
                                        .replace("{litros}", litros + "L"))
                                .setAmount(quantidade).build();

                        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(i);
                        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
                        if (tag.hasKey("litros")) {
                            tag.remove("litros");
                        }
                        tag.setInt("litros", litros);
                        stack.setTag(tag);
                        ItemStack item = CraftItemStack.asCraftMirror(stack);
                        player.getInventory().addItem(item);


                    } else {
                        sender.sendMessage("§cUse: /maquina dar combustivel <player> <id> <litros> <quantidade>");
                    }
                }

            }
        }
        return false;
    }
}
