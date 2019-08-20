package me.zkingofkill.spartan.engines;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.mysql.Cache;
import me.zkingofkill.spartan.objects.Combustivel;
import me.zkingofkill.spartan.objects.Maquina;
import me.zkingofkill.spartan.utils.ItemStackBuilder;
import me.zkingofkill.spartan.utils.Utils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EngineMenu implements InventoryProvider {

    private Maquina maquina;
    private Player player;
    private ItemStack dono;
    private ItemStack status;
    private ItemStack stacks;
    private ItemStack drops;
    private ItemStack combustivel;
    private ItemStack addmigos;
    private ItemStack notificacoes;
    private ItemStack holograma;

    public EngineMenu(Player player, Maquina maquina) {
        this.maquina = maquina;
        this.player = player;
        dono = new ItemStackBuilder(maquina.getProps().getItem().getType())
                .setDurability(maquina.getProps().getItem().getDurability())
                .setName(maquina.getProps().getNome().replace("&", "§"))
                .addLore("", "§5Dono: §7" + maquina.getDono()).build();

        status = new ItemStackBuilder(Material.REDSTONE_TORCH_ON)
                .setName("§1§lStatus")
                .addLore("", maquina.isAtiva() ? ("§aMaquina ativa.") : "§cMaquina desativada.")
                .addLore("")
                .addLore("§aClique parar ativar/desativar.")
                .build();

        stacks = new ItemStackBuilder(Material.DISPENSER)
                .setName("§a§lMaquinas estacadas")
                .addLore("", "§7" + maquina.getQuantidade() + "/" + maquina.getProps().getStackMax()).build();

        drops = new ItemStackBuilder(Material.GOLD_NUGGET)
                .setName("§a§lDrops")
                .addLore("", "§7Quantidade de drops: " + Utils.getUtils().numberFormat(maquina.getDrops(), 1))
                .addLore("§7Valor: " + Utils.getUtils().numberFormat(maquina.getDrops() * maquina.getProps().getValor(), 0))
                .addLore("")
                .addLore("§aClique para vender seus drops.").build();

        combustivel = new ItemStackBuilder(Material.GLASS_BOTTLE)
                .setName("§a§lCombustivel")
                .addLore("", "§7Litros: " + maquina.getLitros() + "L/" + maquina.getProps().getLitrosMax()*maquina.getQuantidade() + "L")
                .addLore("")
                .addLore("§aClique para adicionar combustivel.").build();

        ItemStackBuilder item = new ItemStackBuilder(Material.STAINED_GLASS_PANE);
        item.setDurability(((short) 15));
        item.setName("§a§lAmigos: ");
        item.addLore("");
        if (maquina.getAmigos().size() >= 1) {
            for (String a : maquina.getAmigos()) {
                item.addLore("§7" + a + ",");
            }
        } else {
            item.addLore("§cNenhum amigo com permissão.");
        }
        item.addLore("", "§aClique para add/rem seus amigos.");
        addmigos = item.build();
        notificacoes = new ItemStackBuilder(Material.HOPPER)
                .setName("§1§lNotificações:")
                .addLore("", maquina.isNotificacoes() ? "§aNotificação ativa." : "§cNotificação desativada.")
                .addLore("")
                .addLore("§aClique parar ativar/desativar.")
                .build();

        holograma = new ItemStackBuilder(Material.TRIPWIRE_HOOK)
                .setName("§1§lHolograma:")
                .addLore("", maquina.isHologramas() ? "§aHolograma ativo." : "§cHolograma desativado.")
                .addLore("")
                .addLore("§aClique parar ativar/desativar.")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(1, 1, ClickableItem.empty(dono));
        contents.set(1, 3, ClickableItem.of(status, e -> {
            if (!maquina.isAtiva()) {
                if (maquina.getLitros() >= maquina.getProps().getConsumo()) {
                    maquina.setAtiva(true);
                    maquina.start();
                    open();
                }
            }else{
                maquina.setAtiva(false);
                open();
            }
        }));
        contents.set(1, 5, ClickableItem.empty(stacks));
        contents.set(1, 7, ClickableItem.of(drops, e -> {
            if (maquina.getDrops() >= 1) {
                Maquinas.getEconomy().depositPlayer(player, maquina.getDrops() * maquina.getProps().getValor());
                maquina.setDrops(0);
                open();
            }
        }));
        contents.set(3, 1, ClickableItem.of(combustivel, e -> {
            for (Combustivel combustivel : this.maquina.getProps().getCombustiveis()) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
                        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
                        if (tag.hasKey("litros")) {
                            int litros = tag.getInt("litros");
                            combustivel.setLitros(litros);
                            ItemStack i = new ItemStackBuilder(combustivel.getItem().getType()).setDurability(combustivel.getItem().getDurability()).setName(combustivel.getItem().getItemMeta().getDisplayName().replace("{litros}", litros + "L")).build();
                            if (i.getType().equals(item.getType()) && i.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                                if (maquina.getLitros() < maquina.getProps().getLitrosMax()) {
                                    if ((maquina.getProps().getLitrosMax() - maquina.getLitros()) >= combustivel.getLitros()) {
                                        maquina.setLitros(maquina.getLitros() + combustivel.getLitros());
                                        if (item.getAmount() >= 2) {
                                            item.setAmount(item.getAmount() - 1);
                                        } else {
                                            player.getInventory().remove(item);
                                        }
                                        open();
                                        break;

                                    } else {
                                        int q = combustivel.getLitros() - (maquina.getProps().getLitrosMax() - maquina.getLitros());
                                        maquina.setLitros(maquina.getProps().getLitrosMax());
                                        ItemStack toad = new ItemStackBuilder(item.getType()).setDurability(item.getDurability()).setName(item.getItemMeta().getDisplayName().replace(litros + "L", q + "L")).setAmount(1).build();
                                        net.minecraft.server.v1_8_R3.ItemStack stack2 = CraftItemStack.asNMSCopy(toad);
                                        NBTTagCompound tag2 = stack2.getTag() != null ? stack2.getTag() : new NBTTagCompound();
                                        ItemStack toad2 = CraftItemStack.asCraftMirror(stack2);
                                        tag2.remove("litros");
                                        tag2.setInt("litros", q);
                                        if (item.getAmount() >= 2) {
                                            item.setAmount(item.getAmount() - 1);
                                        } else if (item.getAmount() == 1) {
                                            player.getInventory().remove(item);
                                        }
                                        player.getInventory().addItem(toad2);
                                        open();
                                        break;

                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(maquina.getLitros() == maquina.getProps().getLitrosMax()){
                maquina.setAtiva(true);
                maquina.start();
                open();
            }
        }));
        contents.set(3, 3, ClickableItem.of(addmigos, e -> {
            if(maquina.getDono().equals(player.getName())) {
                Cache.amiggosadding.put(player, maquina);
                close();
                player.sendMessage("§f[§6§lMaquinas§f] §aDigite o nome do jogador que quer adicionar ou remover da maquina.");
                player.sendMessage("§f[§6§lMaquinas§f] §aDigite §c§lcancelar §apara cancelar.");
            }else{
                player.sendMessage("§f[§6§lMaquinas§f] §cApenas o dono da maquina pode fazer isso!");
            }
        }));
        contents.set(3, 5, ClickableItem.of(notificacoes, e -> {
            if (!maquina.isNotificacoes()) {
                maquina.setNotificacoes(true);
            } else {
                maquina.setNotificacoes(false);
            }
            open();
        }));
        contents.set(3, 7, ClickableItem.of(holograma, e -> {
            if (!maquina.isHologramas()) {
                maquina.setHologramas(true);
                maquina.getHologram().getProps().getVisibilityManager().setVisibleByDefault(true);
            } else {
                maquina.setHologramas(false);
                maquina.getHologram().getProps().getVisibilityManager().setVisibleByDefault(false);
                maquina.getHologram().getProps().getVisibilityManager().resetVisibilityAll();

            }
            open();
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);

    }

    public void open() {
        SmartInventory.builder()
                .id(this.maquina.getsId())
                .provider(new EngineMenu(player, maquina))
                .size(5, 9)
                .title("[Maquina de " + this.maquina.getsId() + "]")
                .build()
                .open(this.player);
    }

    public void close() {
        SmartInventory.builder()
                .id(this.maquina.getsId())
                .provider(new EngineMenu(player, maquina))
                .size(5, 9)
                .title("[Maquina de " + this.maquina.getsId() + "]")
                .build()
                .close(this.player);
    }

}

