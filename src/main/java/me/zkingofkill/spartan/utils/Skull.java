package me.zkingofkill.spartan.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class Skull {
    public static ItemStack getSkull(String url) {

        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (url.isEmpty()) return item;


        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField = null;

        try {

            profileField = itemMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);

            profileField.set(itemMeta, profile);

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {

            e.printStackTrace();

        }

        item.setItemMeta(itemMeta);

        return item;

    }

}
