package me.zkingofkill.spartan.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Props {

    private String sId;
    private String nome;
    private ItemStack item;
    private int litrosMax;
    private int stackMax;
    private int DropsQnt;
    private double delay;
    private double valor;
    private List<Combustivel> combustiveis;
    private int consumo;
    private int rankMinimo;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getLitrosMax() {
        return litrosMax;
    }

    public void setLitrosMax(int litrosMax) {
        this.litrosMax = litrosMax;
    }

    public int getStackMax() {
        return stackMax;
    }

    public void setStackMax(int stackMax) {
        this.stackMax = stackMax;
    }

    public int getDropsQnt() {
        return DropsQnt;
    }

    public void setDropsQnt(int dropsQnt) {
        DropsQnt = dropsQnt;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<Combustivel> getCombustiveis() {
        return combustiveis;
    }

    public void setCombustiveis(List<Combustivel> combustiveis) {
        this.combustiveis = combustiveis;
    }

    public int getConsumo() { return consumo; }

    public void setConsumo(int consumo) { this.consumo = consumo; }

    public int getRankMinimo() {
        return rankMinimo;
    }

    public void setRankMinimo(int rankMinimo) {
        this.rankMinimo = rankMinimo;
    }

    public Props(String sId, String nome, ItemStack item, int litrosMax, int stackMax, int dropsQnt, double delay, double valor, List<Combustivel> combustiveis, int consumo, int rankMinimo) {
        this.sId = sId;
        this.nome = nome;
        this.item = item;
        this.litrosMax = litrosMax;
        this.stackMax = stackMax;
        DropsQnt = dropsQnt;
        this.delay = delay;
        this.valor = valor;
        this.combustiveis = combustiveis;
        this.consumo = consumo;
        this.rankMinimo = rankMinimo;
    }
}
