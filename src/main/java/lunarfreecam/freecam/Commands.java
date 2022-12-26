package lunarfreecam.freecam;

import FreecamUtils.FreecamCountDown;
import FreecamUtils.NpcManager;

import FreecamUtils.utils;
import VaultUtils.VaultUtils;
import com.cryptomorin.xseries.XSound;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;


public class Commands implements CommandExecutor {

    private final String freecam = "freecam";
    private final Main plugin;
    private final VaultUtils vaultUtils;
    public final static HashMap<Player, GameMode> previousGamemode = new HashMap<>();
    private final NpcManager npcManager = new NpcManager();


    public Commands(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand(freecam).setExecutor(this);
        vaultUtils = new VaultUtils(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("freecam-not-player"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("freecam.use")) {
            player.sendMessage(utils.Color(plugin.getConfig().getString("no-permission-message")));
            return true;
        }
        if (args.length == 0) {
            //Activate freecam
            if (plugin.getConfig().getBoolean("freecam-charge-money")) {
                if (plugin.isVaultEnabled()) {
                    if (!vaultUtils.canPayForFreecam(player)) {
                        return true;
                    }
                } else {
                    player.sendMessage(utils.Color("&8[&5LunarFreecam&8] &cVault is needed because economy is enabled from the config!"));
                    plugin.getServer().getConsoleSender().sendMessage(utils.Color("&8[&5LunarFreecam&8] &4&lVault is needed because economy is enabled from the config!"));
                    return true;
                }
            }
            if (Main.npcs.containsKey(player.getUniqueId())) {
                player.sendMessage(utils.Color(plugin.getConfig().getString("already-freecam")));
                return true;
            }
            player.playSound(player.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 100, 2);
            previousGamemode.put(player, player.getGameMode());
            player.setGameMode(GameMode.SPECTATOR);
            npcManager.createNpc(player);
            BukkitTask task = new FreecamCountDown(player, plugin.getConfig().getInt("freecam-period"), plugin).runTaskTimer(plugin, 0, 20);
            Main.playersInFreecam.add(player);
        } else {
            if (args[0].equalsIgnoreCase("stop")) {
                if (Main.npcs.containsKey(player.getUniqueId())) {
                    npcManager.exitFreecam(player, previousGamemode.get(player));
                    return true;
                } else {
                    player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-no-use")));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("lunarfreecam.reload")) {
                    plugin.reloadConfig();
                    player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-reload")));
                    return true;
                }
            }
            player.sendMessage(utils.Color(plugin.getConfig().getString("too-many-arguments-message")));
        }
        return true;
    }
}
