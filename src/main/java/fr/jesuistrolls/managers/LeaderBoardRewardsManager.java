package fr.jesuistrolls.managers;

import fr.jesuistrolls.TrolliUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class LeaderBoardRewardsManager {

    public static boolean GiveRewards(String leaderboardType, int leaderboardRank, OfflinePlayer playerName) {

        FileConfiguration config = TrolliUtils.getInstance().getConfig();
        String configPath = "leaderboard-rewards.rewards-commands." + leaderboardType + "." + leaderboardRank + ".commands";

        List<String> commands = config.getStringList(configPath);

        if (commands.isEmpty()) {
            Bukkit.getLogger().warning("Aucune commande trouvée pour " + leaderboardType + " #" + leaderboardRank);
            return false;
        }

        for (String rewardCommand : commands) {
            Bukkit.getLogger().info(rewardCommand);
            String command = rewardCommand.replace("%player_name%", playerName.getName());

            if (!command.isEmpty()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        return true;
    }
}