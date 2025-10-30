package fr.jesuistrolls.managers;

import fr.jesuistrolls.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class LeaderBoardRewardsManager {

    public static boolean GiveRewards(String leaderboardType, int leaderboardRank, OfflinePlayer playerName) {

        boolean hasTransferred = false;

        List<String> commands = Messages.getStringList("rewards-commands." + leaderboardType + "." + leaderboardRank + ".commands");

        for (String rewardCommand : commands) {
            Bukkit.getLogger().info(rewardCommand);
            String command = rewardCommand.replace("%player_name%", (CharSequence) playerName);

            if (!command.isEmpty()) { 
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        hasTransferred = true;
        return hasTransferred;
    }
}
