package fr.jesuistrolls.commands.player;

import fr.jesuistrolls.configurations.Messages;
import fr.jesuistrolls.managers.LeaderBoardRewardsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardRewardsCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.PLAYER_ONLY.send(sender);
            return true;
        }

        if (!player.hasPermission(Messages.LEADERBOARD_PERMISSION.getString())) {
            Messages.NO_PERMISSION.send(sender);
            return true;
        }

        if(args.length != 3) {
            Messages.sendBrute(sender, "<red>Usage: /leaderboardrewards <leaderboard_type> <leaderboard_rank> <player_name>");
            return true;
        }

        String leaderboardType = args[0];
        int leaderboardRank = Integer.parseInt(args[1]);
        OfflinePlayer playerName = Bukkit.getOfflinePlayer(args[2]);
        if (!leaderboardType.equalsIgnoreCase("caps") || leaderboardType.equalsIgnoreCase("box")) {
            Messages.sendBrute(sender, "<red>Usage: <leaderboard_type> must be caps or box");
            Messages.sendBrute(sender, "<red>Usage: /leaderboardrewards <leaderboard_type> <leaderboard_rank> <player_name>");
            return true;
        }
            try {
                boolean success = LeaderBoardRewardsManager.GiveRewards(leaderboardType, leaderboardRank, playerName);
                if (success) {

                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("%leaderboard_type%", leaderboardType.toUpperCase());
                    replacements.put("%leaderboard_rank%", String.valueOf(leaderboardRank));

                    Messages.LEADERBOARD_SUCCESS.sendReplace(playerName.getPlayer(), replacements);
                } else {
                    Messages.ERROR_COMMAND.send(sender);
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        return true;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tempArgs = new ArrayList<>();
        switch (args.length) {
            case 1:
                tempArgs.addAll(List.of("caps", "box"));
                break;
            case 2:
                tempArgs.addAll(List.of("all", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                break;
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        tempArgs.add(player.getName());
                    }
                }
                break;
            default:
                return List.of();
        }
        return tempArgs;
    }
}
