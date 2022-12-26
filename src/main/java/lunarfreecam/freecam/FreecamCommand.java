package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import FreecamUtils.utils;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class FreecamCommand implements CommandExecutor {

    private final Main plugin;
    private final NpcManager npcManager = new NpcManager();


    public FreecamCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(utils.Color(plugin.getConfig().getString("freecam-not-player")));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("freecam.use")) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
            return true;
        }
        switch (args.length) {
            case 0:
                activateFreecam(player);
                return true;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "stop":
                        if (Main.npcs.containsKey(player.getUniqueId()))
                            NpcManager.exitFreecam(player);
                        else
                            player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-no-use")));
                        return true;
                    case "reload":
                        if (player.hasPermission("freecam.reload")) {
                            plugin.reloadConfig();
                            player.sendMessage(utils.Color(plugin.getConfig().getString("reload")));
                        } else {
                            player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
                        }
                        return true;
                    default:
                        player.sendMessage(utils.Color(plugin.getConfig().getString("invalid-use")));
                        return true;
                }
            default:
                player.sendMessage(utils.Color(plugin.getConfig().getString("too-many-arguments")));
                return true;
        }
    }

    private void activateFreecam(Player player) {
        if (Main.npcs.containsKey(player.getUniqueId())) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("already-freecam")));
            return;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 2);
        Main.previousGamemode.put(player.getUniqueId(), player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        npcManager.createNpc(player);
        Main.playersInFreecam.add(player);
    }
}
