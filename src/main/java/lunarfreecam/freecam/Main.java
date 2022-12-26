package lunarfreecam.freecam;

import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class Main extends JavaPlugin {
    public final static HashMap<UUID, LivingEntity> npcs = new HashMap<>();
    public final static ArrayList<Player> playersInFreecam = new ArrayList<>();
    public final static HashMap<UUID, PlayerState> previousState = new HashMap<>();
    public static ArrayList<Chunk> forceLoadedChunks = new ArrayList<>();

    public static String Color(String s){
        s = ChatColor.translateAlternateColorCodes('&',s);
        return s;
    }

    public void onEnable() {
        loadConfig();
        new Handler(this);

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
        playersInFreecam.forEach(NpcManager::exitFreecam);
    }

    private void registerCompletions(PluginCommand command) {
        Commodore commodore = CommodoreProvider.getCommodore(this);
        commodore.register(command, literal("freecam")
                .then(literal("reload")));
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }
}
