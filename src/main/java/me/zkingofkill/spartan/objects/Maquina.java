package me.zkingofkill.spartan.objects;

import me.zkingofkill.spartan.engines.EngineHologram;
import me.zkingofkill.spartan.engines.EngineSchedule;
import me.zkingofkill.spartan.mysql.Cache;

import java.util.ArrayList;

public class Maquina {
    private int id;
    private String sId;
    private int drops;
    private String location;
    private int quantidade;
    private String dono;
    private long start;
    private boolean ativa;
    private int litros;
    private boolean notificacoes;
    private boolean hologramas;
    private ArrayList<String> amigos;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public long getTime() {
        return start;
    }

    public void setTime(long start) {
        this.start = start;
    }

    public int getDrops() {
        return drops;
    }

    public void setDrops(int drops) {
        this.drops = drops;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        this.dono = dono;
    }

    public int getLitros() {
        return litros;
    }

    public void setLitros(int litros) {
        this.litros = litros;
    }

    public boolean isNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(boolean notificacoes) {
        this.notificacoes = notificacoes;
    }

    public boolean isHologramas() {
        return hologramas;
    }

    public void setHologramas(boolean hologramas) {
        this.hologramas = hologramas;
    }

    public ArrayList<String> getAmigos() {
        return amigos;
    }

    public void setAmigos(ArrayList<String> amigos) {
        this.amigos = amigos;
    }

    public Props getProps() {
        return Cache.props.get(this.sId);
    }

    public void start() {
        this.start = System.currentTimeMillis();
        new EngineSchedule().start();
    }

    public EngineHologram getHologram() {
        return new EngineHologram(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Maquina{" +
                "id=" + id +
                ", sId='" + sId + '\'' +
                ", drops=" + drops +
                ", location='" + location + '\'' +
                ", quantidade=" + quantidade +
                ", dono='" + dono + '\'' +
                ", start=" + start +
                ", ativa=" + ativa +
                ", litros=" + litros +
                ", notificacoes=" + notificacoes +
                ", hologramas=" + hologramas +
                ", amigos=" + amigos +
                '}';
    }

    public Maquina(String sId, String location, String dono, int quantidade) {
        this.sId = sId;
        this.location = location;
        this.dono = dono;
        this.quantidade = quantidade;
        getHologram().create();
        this.amigos = new ArrayList<>();
    }
}
