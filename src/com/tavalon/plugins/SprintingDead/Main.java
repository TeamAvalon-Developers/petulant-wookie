package com.tavalon.plugins.SprintingDead;

import com.tavalon.plugins.SprintingDead.listeners.ItemInteractListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    public static Main plugin;
    private ArrayList<Chest> randomChests;

    public static class Chest {
        public Location l;
        public int time;

        public Chest(Location l) {
            this.l = l;
            time = 0;
        }

        public Chest() {

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
        l.registerEvents(new ItemInteractListener(), this);
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
                                randomChests.add(new Chest(loc.clone()));
                                s.sendMessage(ChatColor.GREEN + "Successfully added a random chest at " + ChatColor.BLUE + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ChatColor.GREEN + ".");
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
        cInfo(s, "chest", "<add/remove/respawn/list/load/save>", "c", "Modifying the random chests that exist in the world.");
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
                    String worldName = line.substring(line.indexOf('W') + 1);

                    if(Bukkit.getWorld(worldName) != null) {
                        Chest c = new Chest(new Location(Bukkit.getWorld(worldName), x, y, z));
                        c.time = timer;
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
            pw.println(c.time + "X" + c.l.getBlockX() + "Y" + c.l.getBlockY() + "Z" + c.l.getBlockZ() + "W" + c.l.getWorld().getName());
        }

        pw.close();
    }
}
