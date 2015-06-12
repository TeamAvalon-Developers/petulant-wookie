package com.tavalon.plugins.SprintingDead;

import com.tavalon.plugins.SprintingDead.listeners.ServerTickListener;
import com.tavalon.plugins.SprintingDead.util.ParticleEffect;
import com.tavalon.plugins.SprintingDead.util.ReflectionUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    public static Main plugin;
    public ArrayList<Chest> randomChests;

    public void regenerateChest(Chest c) {
        Main.makeChestEffect((c.l));
        c.l.getWorld().getBlockAt(c.l).setType(Material.TRAPPED_CHEST);
        c.time = -1;
    }

    public Chest getChestAt(Location loc) {
        Chest c = new Chest();
        boolean found = false;
        for (Chest l : randomChests) {
            if (loc.getBlockX() == l.l.getBlockX() && loc.getBlockY() == l.l.getBlockY() && loc.getBlockZ() == l.l.getBlockZ()) {
                c = l;
                found = true;
            }
        }

        if(found) return c;
        else return null;
    }

    public boolean isChestAt(Location loc) {
        boolean found = false;
        for (Chest l : randomChests) {
            if (loc.getBlockX() == l.l.getBlockX() && loc.getBlockY() == l.l.getBlockY() && loc.getBlockZ() == l.l.getBlockZ()) {
                found = true;
            }
        }

        return found;
    }

    public static void makeChestEffect(Location loc) {
        for(int i = 0; i < 50; i++) {
            CraftWorld world = (CraftWorld) loc.getWorld();
            try {
                World.Spigot spigot = (CraftWorld.Spigot) ReflectionUtils.getValue(world, true, "spigot");
                spigot.playEffect(loc.clone().add(0.5f, 1f, 0.5f), Effect.TILE_BREAK, 11, 1, 0.0F, 0.0F, 0.0F, 1.0F, 1, 64);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        loc.getWorld().playSound(loc, Sound.DIG_STONE, 0.5f, 0.75f);
    }

    public static class Chest {
        public Location l;
        public int time;
        public int level;

        public Chest(Location l) {
            this.l = l;
            time = 0;
            level = 1;
        }

        public Chest(Location l, int level) {
            this.l = l;
            time = 0;
            this.level = level;
        }

        public Chest() {
            time = 0;
            level = 1;
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        randomChests = new ArrayList<Chest>();
        loadDataFromFile();
        registerListeners();
    }

    private void registerListeners() {
        PluginManager l = Bukkit.getServer().getPluginManager();
        l.registerEvents(new RandomChestManager(), this);
        new ServerTickListener(this);
    }

    @Override
    public void onDisable() {
        saveDataToFile();
    }

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("sprintdead") ) {
            if(s instanceof Player ? ((Player)s).isOp() : true) {
                if(args.length == 0) {
                    displayCommandHelp(s);
                } else if(args[0].equalsIgnoreCase("help")) {
                    displayCommandHelp(s);
                } else if(args[0].equalsIgnoreCase("chest") || args[0].equalsIgnoreCase("c")) {
                    if(args.length == 1) {
                        s.sendMessage(ChatColor.RED + "Please use more arguments!");
                    } else if(args[1].equalsIgnoreCase("add")) {
                        if(s instanceof Player) {
                            Player p = (Player) s;
                            Location loc = p.getLocation();
                            if (!containsLocation(randomChests, loc.clone())) {
                                if(args.length == 3) {
                                    randomChests.add(new Chest(loc.clone(), Integer.valueOf(args[2])));
                                    s.sendMessage(ChatColor.GREEN + "Successfully added a " + ChatColor.BLUE + "Lvl. " + Integer.valueOf(args[2]) + ChatColor.GREEN + " random chest at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.GREEN + ".");
                                } else {
                                    randomChests.add(new Chest(loc.clone()));
                                    s.sendMessage(ChatColor.GREEN + "Successfully added a random chest at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.GREEN + ".");
                                }

                            } else {
                                s.sendMessage(ChatColor.RED + "A random chest already exists at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                            }
                        } else sendConsoleError(s);
                    } else if(args[1].equalsIgnoreCase("list")) {
                        s.sendMessage(ChatColor.YELLOW + "List of coordinates of random chests:");
                        boolean first = true;
                        boolean last = false;
                        int count = 0;
                        for(Chest l : randomChests) {
                            if(first) {
                                s.sendMessage(ChatColor.GOLD + " {  " + ChatColor.BLUE + "X: " + l.l.getBlockX() + " Y: " + l.l.getBlockY() + " Z: " + l.l.getBlockZ() + ChatColor.GOLD + ",");
                                first = false;
                            } else if(last) {
                                s.sendMessage("    " + ChatColor.BLUE + "X: " + l.l.getBlockX() + " Y: " + l.l.getBlockY() + " Z: " + l.l.getBlockZ() + ChatColor.GOLD + "  }");
                            } else s.sendMessage("    " + ChatColor.BLUE + "X: " + l.l.getBlockX() + " Y: " + l.l.getBlockY() + " Z: " + l.l.getBlockZ() + ChatColor.GOLD + ",");
                            count++;
                            if(count == (randomChests.size() - 1)) {
                                last = true;
                            }
                        }
                    } else if(args[1].equalsIgnoreCase("remove")) {
                        if (s instanceof Player) {
                            Player p = (Player) s;
                            Location loc = p.getLocation();
                            if (containsLocation(randomChests, loc.clone())) {
                                Chest c = new Chest();
                                boolean found = false;
                                for (Chest l : randomChests) {
                                    if (loc.getBlockX() == l.l.getBlockX() && loc.getBlockY() == l.l.getBlockY() && loc.getBlockZ() == l.l.getBlockZ()) {
                                        c = l;
                                        found = true;
                                    }
                                }

                                if (found) {
                                    randomChests.remove(c);
                                    s.sendMessage(ChatColor.GREEN + "Successfully removed the random chest at " + ChatColor.BLUE + "X: " + c.l.getBlockX() + " Y: " + c.l.getBlockY() + " Z: " + c.l.getBlockZ() + ChatColor.GREEN + ".");
                                } else {
                                    s.sendMessage(ChatColor.RED + "No random chest was found at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                                }
                            } else
                                s.sendMessage(ChatColor.RED + "No random chest was found at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                        } else sendConsoleError(s);
                    } else if(args[1].equalsIgnoreCase("load")) {
                        reloadChestData();
                        s.sendMessage(ChatColor.GREEN + "Reloaded chest data from file.");
                    } else if(args[1].equalsIgnoreCase("save")) {
                        saveDataToFile();
                        s.sendMessage(ChatColor.GREEN + "Saved chest data to file.");
                    } else if(args[1].equalsIgnoreCase("regen")) {
                        if (s instanceof Player) {
                            Player p = (Player) s;
                            Location loc = p.getLocation();
                            if(isChestAt(loc)) {
                                Chest c = getChestAt(loc);
                                if(loc.getWorld().getBlockAt(loc).getType() == Material.TRAPPED_CHEST) {
                                    s.sendMessage(ChatColor.RED + "A random chest already exists at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                                } else {
                                    regenerateChest(c);
                                    s.sendMessage(ChatColor.GREEN + "Successfully regenerated the random chest at " + ChatColor.BLUE + "X: " + c.l.getBlockX() + " Y: " + c.l.getBlockY() + " Z: " + c.l.getBlockZ() + ChatColor.GREEN + ".");
                                }
                            } else
                                s.sendMessage(ChatColor.RED + "No random chest was found at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                        } else sendConsoleError(s);
                    } else if(args[1].equalsIgnoreCase("regenall")) {
                        s.sendMessage(ChatColor.GOLD + "Start of regenerating chests: {");
                        int counter = 0;
                        for(Chest c : randomChests) {
                            if(c.l.getWorld().getBlockAt(c.l).getType() == Material.TRAPPED_CHEST) {
                                s.sendMessage(ChatColor.RED + "A random chest already exists at " + ChatColor.BLUE + "X: " + c.l.getBlockX() + " Y: " + c.l.getBlockY() + " Z: " + c.l.getBlockZ() + ChatColor.RED + "!");
                            } else {
                                regenerateChest(c);
                                counter++;
                                s.sendMessage(ChatColor.GREEN + "Successfully regenerated the random chest at " + ChatColor.BLUE + "X: " + c.l.getBlockX() + " Y: " + c.l.getBlockY() + " Z: " + c.l.getBlockZ() + ChatColor.GREEN + ".");
                            }
                        }
                        s.sendMessage(ChatColor.GOLD + "} End of regenerating chests;" + ChatColor.GREEN + " Successfully regenerated " + counter + " chests.");
                    } else if(args[1].equalsIgnoreCase("info")) {
                        if (s instanceof Player) {
                            Player p = (Player) s;
                            Location loc = p.getLocation();
                            if(isChestAt(loc)) {
                                Chest c = getChestAt(loc);
                                s.sendMessage(ChatColor.GOLD + "Chest info for chest at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.GOLD + ": " + ChatColor.BLUE + "Level: " + c.level + " Time: " + c.time );
                            } else
                                s.sendMessage(ChatColor.RED + "No random chest was found at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.RED + "!");
                        } else sendConsoleError(s);
                    }
                }
                return true;
            }
            else {
                sendUnknownCommand(s);
                return true;
            }
        } else return false;
    }

    public void sendConsoleError(CommandSender cs) {
        cs.sendMessage(ChatColor.RED + "The console cannot preform this command!");
    }

    public void sendUnknownCommand(CommandSender s) {
        s.sendMessage("Unknown command. Type \"/help\" for help.");
    }

    public boolean containsLocation(ArrayList<Chest> list, Location loc) {
        System.out.println("{ Starting coord list: ");
        for(Chest l : list) {
            System.out.println("X: " + l.l.getBlockX() + " Y: " + l.l.getBlockY() + " Z: " + l.l.getBlockZ());
            System.out.println("Compared to: X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
            if(loc.getBlockX() == l.l.getBlockX() && loc.getBlockY() == l.l.getBlockY() && loc.getBlockZ() == l.l.getBlockZ()) {
                System.out.println("true");
                System.out.println("Ending coord list. }");
                return true;
            }
        }
        System.out.println("Ending coord list. }");
        return false;
    }

    public void saveDataToFile() {
        saveChestData();
    }

    public void loadDataFromFile() {
        reloadChestData();
    }

    public void displayCommandHelp(CommandSender s) {
        s.sendMessage(ChatColor.GRAY + "--------------- " + ChatColor.GOLD + "The Sprinting Dead" + ChatColor.GRAY + " -----------------");
        cInfo(s, "chest", "<add/remove/regen/list/load/save/regenall/info> [arg[1]=\"add\" ? level]", "c", "Modifying the random chests that exist in the world.");
    }

    public void cInfo(CommandSender s, String label, String args, String aliases, String description) {
        s.sendMessage(ChatColor.GOLD + "/sprintdead " + label + ChatColor.YELLOW + " " + args + ChatColor.GRAY + ": " + ChatColor.WHITE + description + ChatColor.BLUE + " Alias(es): " + ChatColor.YELLOW + aliases);
    }

    public void reloadChestData() {
        randomChests.clear();

        File dataFolder = getDataFolder();
        if(!dataFolder.exists())
        {
            dataFolder.mkdir();
        }

        File writeFrom = new File(getDataFolder(), "chests.dat");
        if(writeFrom.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(writeFrom))) {
                String line = br.readLine();

                while (line != null) {
                    int timer = Integer.valueOf(line.substring(0, line.indexOf('X')));
                    int x = Integer.valueOf(line.substring(line.indexOf('X') + 1, line.indexOf('Y')));
                    int y = Integer.valueOf(line.substring(line.indexOf('Y') + 1, line.indexOf('Z')));
                    int z = Integer.valueOf(line.substring(line.indexOf('Z') + 1, line.indexOf('W')));
                    String worldName = line.substring(line.indexOf('W') + 1, line.indexOf('L'));
                    int level = Integer.valueOf(line.substring(line.indexOf("L") + 1));

                    if(Bukkit.getWorld(worldName) != null) {
                        Chest c = new Chest(new Location(Bukkit.getWorld(worldName), x, y, z));
                        c.time = timer;
                        c.level = level;
                        randomChests.add(c);
                    }
                    line = br.readLine();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChestData() {
        File dataFolder = getDataFolder();
        if(!dataFolder.exists())
        {
            dataFolder.mkdir();
        }

        File saveTo = new File(getDataFolder(), "chests.dat");
        if(!saveTo.exists())
        {
            try {
                saveTo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(saveTo, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);

        for(Chest c : randomChests) {
            pw.println(c.time + "X" + c.l.getBlockX() + "Y" + c.l.getBlockY() + "Z" + c.l.getBlockZ() + "W" + c.l.getWorld().getName() + "L" + c.level);
        }

        pw.close();
    }
}
