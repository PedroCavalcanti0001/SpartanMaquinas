package me.zkingofkill.spartan.objects;

import org.bukkit.inventory.ItemStack;

public class Combustivel implements Cloneable {
    private int id;
    private ItemStack item;
    private int litros;

    public int getLitros() {
        return litros;
    }

    public void setLitros(int litros) {
        this.litros = litros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Combustivel{" +
                "id=" + id +
                ", item=" + item +
                ", litros=" + litros +
                '}';
    }
    @Override
    public Combustivel clone() {
        try {
            return (Combustivel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Combustivel(int id, ItemStack item) {
        this.id = id;
        this.item = item;
    }

}
