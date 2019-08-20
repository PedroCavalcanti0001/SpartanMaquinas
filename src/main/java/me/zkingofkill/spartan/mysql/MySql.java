package me.zkingofkill.spartan.mysql;

import com.google.gson.Gson;
import me.zkingofkill.spartan.Maquinas;
import me.zkingofkill.spartan.objects.Maquina;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySql {
    public static String tabela = Maquinas.getInstance().getConfig().getString("Mysql.Table");

    public static Connection abrirConexao() {
        try {
            String password = Maquinas.getInstance().getConfig().getString("Mysql.Password");
            String user = Maquinas.getInstance().getConfig().getString("Mysql.User");
            String host = Maquinas.getInstance().getConfig().getString("Mysql.Host");
            String port = Maquinas.getInstance().getConfig().getString("Mysql.Port");
            String database = Maquinas.getInstance().getConfig().getString("Mysql.Database");
            String type = "jdbc:mysql://";
            String url = type + host + ":" + port + "/" + database+"?characterEncoding=utf8&useConfigs=maxPerformance";
            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            System.out.print("Ocorreu um erro no mysql:");
            e.printStackTrace();
        }
        return null;
    }

    public static void criarTabela() {
        try {
            Connection con = abrirConexao();
            PreparedStatement st = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tabela
                    + "(`id` INT NOT NULL AUTO_INCREMENT, `machine` LONGTEXT NOT NULL, PRIMARY KEY(`id`));");
            st.executeUpdate();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMaquina(Player p, int id) {
        try {
            Connection con = abrirConexao();
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + tabela + " WHERE owner = ? AND id = ?;");
            st.setString(1, p.getDisplayName().toLowerCase());
            st.setInt(2, id);
            ResultSet rs = st.executeQuery();
            int value = 0;
            if (rs.next()) {
                value = rs.getInt("Pontos");
            }
            con.close();
            return value;
        } catch (Exception e) {
            System.out.print("Ocorreu um erro no mysql:");
            e.printStackTrace();
        }
        return 0;
    }

    public static void carregarMaquinas() {
        try {
            Connection con = abrirConexao();
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + tabela);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                Maquina maquina = new Gson().fromJson(rs.getString("machine"), Maquina.class);
                maquina.getHologram().create();
                if (maquina.getLitros() >= maquina.getProps().getDelay()) {
                    maquina.setAtiva(true);
                    maquina.start();
                }
                maquina.setId(id);
                Cache.maquinasnochao.put(id, maquina);

            }
            System.out.print(Cache.maquinasnochao.size() + " maquinas foram carregadas.");
            con.close();

        } catch (Exception e) {
            System.out.print("Ocorreu um erro no mysql:");
            e.printStackTrace();
        }
    }

    public static void SalvarMaquinas() {
        try {
            Connection con = abrirConexao();;
            for (Maquina entry : Cache.maquinasnochao.values()) {
                PreparedStatement insert = con.prepareStatement("INSERT INTO " + tabela + "(machine) VALUES (?);");
                insert.setString(1, new Gson().toJson(entry));
                insert.executeUpdate();
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertMaquina(int id, Maquina maquina) {
        try {
            Connection con = abrirConexao();;
            PreparedStatement insert = con.prepareStatement("INSERT INTO " + tabela + "(id, machine) VALUES (?,?) ON DUPLICATE KEY UPDATE id = ?, machine = ?;");
            insert.setInt(1, id);
            insert.setString(2, new Gson().toJson(maquina));
            insert.setInt(3, id);
            insert.setString(4, new Gson().toJson(maquina));
            insert.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteMaquina(int id) {
        try {
            Connection con = abrirConexao();;
            PreparedStatement insert = con.prepareStatement("DELETE FROM " + tabela + " WHERE id = ?;");
            insert.setInt(1, id);
            insert.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean HasInDatabase(int id) {
        try {
            Connection con = abrirConexao();
            PreparedStatement st = con
                    .prepareStatement("SELECT * FROM " + tabela + " WHERE id = ?;");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            boolean result = rs.next();
            con.close();
            return result;
        } catch (Exception e) {
            System.out.println("erro aqui");
            e.printStackTrace();
        }
        return false;
    }

    public static void atualizaMaquina(int id, Maquina maquina) {
        try {
            Connection con = abrirConexao();
            PreparedStatement st = con
                    .prepareStatement("UPDATE " + tabela + " SET machine = ? WHERE id = ?;");
            st.setInt(2, id);
            st.setString(1, new Gson().toJson(maquina));
            st.executeUpdate();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
