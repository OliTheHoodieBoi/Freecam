package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class Main extends JavaPlugin {
    public final static HashMap<UUID, LivingEntity> npcs = new HashMap<>();
    public final static ArrayList<Player> playersInFreecam = new ArrayList<>();
    public final static HashMap<UUID, GameMode> previousGamemode = new HashMap<>();
    public static ArrayList<Chunk> forceLoadedChunks = new ArrayList<>();
    public static String version = null;
    public static int pluginID = 81104;

    public void onEnable() {
        loadConfig();
        new Handler(this);
        new NpcManager();

        // Register command
        PluginCommand freecamCommand = getCommand("freecam");
        assert freecamCommand != null;
        freecamCommand.setExecutor(new FreecamCommand(this));

        if (CommodoreProvider.isSupported())
            registerCompletions(freecamCommand);
    }

    /**
     * Return all players to their last locations if server stops or restarts!
     */
    public void onDisable() {
        npcs.forEach((key, value) -> {
            Player player = Bukkit.getPlayer(key);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(Main.npcs.get(player.getUniqueId()).getLocation());
            value.remove();
        });
    }

    private void registerCompletions(PluginCommand command) {
        Commodore commodore = CommodoreProvider.getCommodore(this);
        commodore.register(command, literal("freecam")
                .then(literal("reload")).then(literal("stop")));
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }
}
