package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import FreecamUtils.utils;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class FreecamCommand implements CommandExecutor {

    private final Main plugin;
    public final static HashMap<Player, GameMode> previousGamemode = new HashMap<>();
    private final NpcManager npcManager = new NpcManager();


    public FreecamCommand(Main plugin) {
        this.plugin = plugin;
        PluginCommand freecamCommand = plugin.getCommand("freecam");
        freecamCommand.setExecutor(this);

        if (CommodoreProvider.isSupported())
            registerCompletions(freecamCommand, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("freecam-not-player"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("freecam.use")) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
            return true;
        }
        switch (args.length) {
            case 0 -> activateFreecam(player);
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "stop" -> {
                        if (Main.npcs.containsKey(player.getUniqueId()))
                            npcManager.exitFreecam(player, previousGamemode.get(player));
                        else
                            player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-no-use")));
                    }
                    case "reload" -> {
                        if (player.hasPermission("freecam.reload")) {
                            plugin.reloadConfig();
                            player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-reload")));
                        } else {
                            player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission")));
                        }
                    }
                }
            }
            default -> player.sendMessage(utils.Color(plugin.getConfig().getString("too-many-arguments")));
        }
        return true;
    }

    private void activateFreecam(Player player) {
        if (Main.npcs.containsKey(player.getUniqueId())) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("already-freecam")));
            return;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 2);
        previousGamemode.put(player, player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        npcManager.createNpc(player);
        Main.playersInFreecam.add(player);
    }

    private void registerCompletions(PluginCommand command, Plugin plugin) {
        Commodore commodore = CommodoreProvider.getCommodore(plugin);
        commodore.register(command, literal("freecam")
                .then(literal("reload")).then(literal("stop")));
    }
}
