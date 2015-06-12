package com.tavalon.plugins.SprintingDead;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class RandomChestManager implements Listener {
    public static void openChestGUI(Main.Chest c,  Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Supply Crate Level " + c.level);
        ArrayList<ItemStack> items = new ArrayList<>();
        Random random = new Random();

        items.add(new ItemStack(Material.BOOKSHELF, 7));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 3));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.CARROT_ITEM, 1));
        items.add(new ItemStack(Material.BOOKSHELF, 7));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 3));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.CARROT_ITEM, 1));
        items.add(new ItemStack(Material.BOOKSHELF, 7));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 3));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.CARROT_ITEM, 1));
        items.add(new ItemStack(Material.BOOKSHELF, 7));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 3));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.CARROT_ITEM, 1));
        items.add(new ItemStack(Material.BOOKSHELF, 7));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 3));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.CARROT_ITEM, 1));
        items.add(new ItemStack(Material.BEACON, 36));

        for(ItemStack item : items) {
            boolean placed = false;
            while(!placed) {
                int rand = random.nextInt(53);
                if(inv.getItem(rand) == null ||  inv.getItem(rand).getType() == Material.AIR) {
                    inv.setItem(rand, item);
                    placed = true;
                }
            }
        }

        player.openInventory(inv);
        c.l.getWorld().getBlockAt(c.l).setType(Material.AIR);
        Main.makeChestEffect((c.l));
        c.time = 0;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();

        if(a == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
            if(Main.plugin.isChestAt(e.getClickedBlock().getLocation())) {
                openChestGUI(Main.plugin.getChestAt(e.getClickedBlock().getLocation()), e.getPlayer());
                e.setCancelled(true);
            }
        }
    }
}
