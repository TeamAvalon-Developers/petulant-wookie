package com.tavalon.plugins.SprintingDead.listeners;

import com.tavalon.plugins.SprintingDead.Main;
import com.tavalon.plugins.SprintingDead.reference.Reference;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ServerTickListener  implements Listener {
    public ServerTickListener(JavaPlugin plugin) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            ServerTickListener.onServerTick();
        }, 0L, 1L);
    }

    public static void onServerTick() {
        for(Main.Chest c : Main.plugin.randomChests) {
            if(c.time != -1) {
                c.time++;
            }

            if(c.time >= Reference.CHEST_TIME) {
                Main.plugin.regenerateChest(c);
                c.time = -1;
            }
        }
    }
}
