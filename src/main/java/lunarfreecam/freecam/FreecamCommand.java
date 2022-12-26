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

        switch (args.length) {
            case 0:
                if (!player.hasPermission("freecam.use")) {
                    player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
                    return true;
                }
                activateFreecam(player);
                return true;
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("freecam.reload")) {
                        plugin.reloadConfig();
                        player.sendMessage(utils.Color(plugin.getConfig().getString("reload")));
                    } else {
                        player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
                    }
                } else {
                    player.sendMessage(utils.Color(plugin.getConfig().getString("invalid-use")));
                }
                return true;
            default:
                player.sendMessage(utils.Color(plugin.getConfig().getString("too-many-arguments")));
                return true;
        }
    }

    private void activateFreecam(Player player) {
        if (Main.npcs.containsKey(player.getUniqueId())) {
            // Exit freecam
            NpcManager.exitFreecam(player);
            return;
        }
        // Enter freecam
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("spectator")));
            return;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 2);
        Main.previousState.put(player.getUniqueId(), new PlayerState(player));
        player.setGameMode(GameMode.SPECTATOR);
        npcManager.createNpc(player);
        Main.playersInFreecam.add(player);
    }
}
