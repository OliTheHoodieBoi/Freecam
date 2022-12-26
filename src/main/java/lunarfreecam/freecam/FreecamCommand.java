package lunarfreecam.freecam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class FreecamCommand implements CommandExecutor {

    private final Main plugin;

    public FreecamCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.Color(plugin.getConfig().getString("freecam-not-player")));
            return true;
        }
        Player player = (Player) sender;

        switch (args.length) {
            case 0:
                if (!player.hasPermission("freecam.use")) {
                    player.sendMessage(Main.Color(plugin.getConfig().getString("no-permission")));
                    return true;
                }
                NpcManager.activateFreecam(player);
                return true;
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("freecam.reload")) {
                        plugin.reloadConfig();
                        player.sendMessage(Main.Color(plugin.getConfig().getString("reload")));
                    } else {
                        player.sendMessage(Main.Color(plugin.getConfig().getString("no-permission")));
                    }
                } else {
                    player.sendMessage(Main.Color(plugin.getConfig().getString("invalid-use")));
                }
                return true;
            default:
                player.sendMessage(Main.Color(plugin.getConfig().getString("too-many-arguments")));
                return true;
        }
    }
}
