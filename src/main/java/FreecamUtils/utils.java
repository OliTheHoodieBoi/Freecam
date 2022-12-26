package FreecamUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class utils {
    public static String Color(String s){
        s = ChatColor.translateAlternateColorCodes('&',s);
        return s;
    }
}
