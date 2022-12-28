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

        switch (args.length) {
            case 0:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.message("freecam-not-player"));
                    return true;
                }
                Player player = (Player) sender;
                if (!player.hasPermission("freecam.use")) {
                    player.sendMessage(plugin.message("no-permission"));
                    return true;
                }
                NpcManager.activateFreecam(player);
                return true;
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("freecam.reload")) {
                        plugin.reloadConfig();
                        sender.sendMessage(plugin.message("reload"));
                    } else {
                        sender.sendMessage(plugin.message("no-permission"));
                    }
                } else {
                    sender.sendMessage(plugin.message("invalid-use"));
                }
                return true;
            default:
                 sender.sendMessage(plugin.message("too-many-arguments"));
                return true;
        }
    }
}
