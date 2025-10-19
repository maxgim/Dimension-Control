package me.maxgim234.dimensioncontrol;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;

public final class DimensionControl extends JavaPlugin implements Listener {

    private Set<String> closedDimensions;

    @Override
    public void onEnable() {
        closedDimensions = new HashSet<>();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        closedDimensions.addAll(getConfig().getStringList("closed-dimensions"));

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("DimensionControl has been enabled!");
    }




    @Override
    public void onDisable() {
        getConfig().set("closed-dimensions", closedDimensions.stream().toList());
        saveConfig();

        getLogger().info("DimensionControl has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("end")) {
            if (!sender.hasPermission("dimensioncontrol.end")) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("§eUsage: /end <open|close|status>");
                return true;
            }

            handleDimensionCommand(sender, "end", args[0]);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("nether")) {
            if (!sender.hasPermission("dimensioncontrol.nether")) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("§eUsage: /nether <open|close|status>");
                return true;
            }

            handleDimensionCommand(sender, "nether", args[0]);
            return true;
        }

        return false;
    }

    private void handleDimensionCommand(CommandSender sender, String dimension, String action) {
        switch (action.toLowerCase()) {
            case "open":
                closedDimensions.remove(dimension);
                sender.sendMessage("§aThe " + dimension + " has been opened!");
                break;
            case "close":
                closedDimensions.add(dimension);
                sender.sendMessage("§aThe " + dimension + " has been closed!");
                break;
            case "status":
                boolean isClosed = closedDimensions.contains(dimension);
                sender.sendMessage("§eThe " + dimension + " is currently " + (isClosed ? "§cclosed" : "§aopen") + "§e.");
                break;
            default:
                sender.sendMessage("§eUsage: /" + dimension + " <open|close|status>");
                break;
        }
    }

    //bypass
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World.Environment toEnvironment = event.getTo().getWorld().getEnvironment();

        boolean hasBypass = player.hasPermission("dimensioncontrol.bypass") ||
                player.isOp();

        // End dimension check
        if (toEnvironment == World.Environment.THE_END && closedDimensions.contains("end")) {
            if (!hasBypass && !player.hasPermission("dimensioncontrol.bypass.end")) {
                event.setCancelled(true);
                player.sendMessage("§cThe End is currently closed!");
            }
        }
        // Nether dimension check
        else if (toEnvironment == World.Environment.NETHER && closedDimensions.contains("nether")) {
            if (!hasBypass && !player.hasPermission("dimensioncontrol.bypass.nether")) {
                event.setCancelled(true);
                player.sendMessage("§cThe Nether is currently closed!");
            }
        }
    }

}