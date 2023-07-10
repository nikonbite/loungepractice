package me.nikonbite.whitelist.command;

import me.nikonbite.whitelist.LoungeWhitelist;
import me.nikonbite.whitelist.data.PlayerData;
import me.nikonbite.whitelist.data.PlayerDataEntry;
import me.nikonbite.whitelist.util.CC;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("deprecation")
public class WhitelistCommand extends Command {
    private final PlayerData playerData;

    public WhitelistCommand(PlayerData playerData) {
        super("whitelist", "loungewhitelist.whitelist", "wl", "lwl", "loungewl");

        this.playerData = playerData;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PlayerDataEntry dataEntry = playerData.getData().get(sender.getName());
        String target;
        LoungeWhitelist plugin;

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        switch (args[0]) {
            default:
                sendHelp(sender);
                break;
            case "add":
                if (sender instanceof ProxiedPlayer) {
                    if (dataEntry.getTokens() == 0) {
                        sender.sendMessage(CC.RED + "You do not have tokens!");
                        break;
                    }
                }

                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                if (playerData.getData().get(args[1]) != null && playerData.getData().get(args[1]).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player already whitelisted!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null) {
                    playerData.getData().put(target, new PlayerDataEntry(target, true, 1));
                } else {
                    playerData.getData().get(target).setWhitelisted(true);
                    playerData.getData().get(target).setTokens(1);
                }

                LoungeWhitelist.getInstance().getPlayerData().saveAll(playerData.getData());
                if (sender instanceof ProxiedPlayer) {
                    dataEntry.setTokens(dataEntry.getTokens() - 1);
                    sender.sendMessage(CC.BD_GREEN + target + CC.LIME + " has been added to the whitelist! " + CC.GRAY + "(Your tokens: " + dataEntry.getTokens() + ")");
                } else sender.sendMessage(CC.BD_GREEN + target + CC.LIME + " has been added to the whitelist!");

                break;

            case "tokens":
                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null || !playerData.getData().get(target).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player not whitelisted!");
                    break;
                }

                sender.sendMessage(
                        CC.BD_GREEN + target + CC.LIME + " has " + CC.BD_GREEN + playerData.getData().get(target).getTokens() + "x" + CC.LIME + " whitelist tokens."
                );

                break;

            case "addtokens":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null || !playerData.getData().get(target).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player not whitelisted!");
                    break;
                }

                if (args[2] == null) {
                    sender.sendMessage(CC.RED + "Please provide a tokens count!");
                    break;
                }

                playerData.getData().get(target).setTokens(playerData.getData().get(target).getTokens() + Integer.parseInt(args[2]));
                LoungeWhitelist.getInstance().getPlayerData().saveAll(playerData.getData());

                sender.sendMessage(
                        CC.LIME + "You successfully added " + CC.BD_GREEN + args[2] + "x " + CC.LIME + "tokens to " + CC.BD_GREEN + target + "'s " + CC.LIME + "account."
                );

                break;

            case "settokens":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null || !playerData.getData().get(target).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player not whitelisted!");
                    break;
                }

                if (args[2] == null) {
                    sender.sendMessage(CC.RED + "Please provide a tokens count!");
                    break;
                }

                playerData.getData().get(target).setTokens(Integer.parseInt(args[2]));
                LoungeWhitelist.getInstance().getPlayerData().saveAll(playerData.getData());

                sender.sendMessage(
                        CC.LIME + "You successfully setted " + CC.BD_GREEN + args[2] + "x " + CC.LIME + "tokens to " + CC.BD_GREEN + target + "'s " + CC.LIME + "account."
                );

                break;

            case "removetokens":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null || !playerData.getData().get(target).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player not whitelisted!");
                    break;
                }

                if (args[2] == null) {
                    sender.sendMessage(CC.RED + "Please provide a tokens count!");
                    break;
                }

                playerData.getData().get(target).setTokens(playerData.getData().get(target).getTokens() - Integer.parseInt(args[2]));
                LoungeWhitelist.getInstance().getPlayerData().saveAll(playerData.getData());

                sender.sendMessage(
                        CC.LIME + "You successfully removed " + CC.BD_GREEN + args[2] + "x " + CC.LIME + "tokens from " + CC.BD_GREEN + target + "'s " + CC.LIME + "account."
                );

                break;

            case "removeplayer":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                if (args[1] == null) {
                    sender.sendMessage(CC.RED + "Please provide a player name!");
                    break;
                }

                target = args[1];

                if (playerData.getData().get(target) == null || !playerData.getData().get(target).isWhitelisted()) {
                    sender.sendMessage(CC.RED + "Player not whitelisted!");
                    break;
                }

                playerData.getData().remove(target);
                LoungeWhitelist.getInstance().getPlayerData().saveAll(playerData.getData());

                if (LoungeWhitelist.getInstance().getProxy().getPlayer(target) != null && LoungeWhitelist.getInstance().getProxy().getPlayer(target).isConnected()) {
                    LoungeWhitelist.getInstance().getProxy().getPlayer(target).disconnect("You were remove from server whitelist!");
                }

                sender.sendMessage(
                        CC.LIME + "You successfully removed " + CC.BD_GREEN + target + "'s " + CC.LIME + "account from whitelist."
                );

                break;

            case "listplayers":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                sender.sendMessage(CC.LIME + "Whitelisted players " + CC.GRAY + "(" + CC.BD_GREEN + playerData.getData().size() + CC.GRAY + "):");
                playerData.getData().forEach((s, playerDataEntry) -> {
                    if (playerDataEntry.isWhitelisted()) {
                        if (LoungeWhitelist.getInstance().getProxy().getPlayer(s) != null && LoungeWhitelist.getInstance().getProxy().getPlayer(s).isConnected()) {
                            sender.sendMessage(CC.GRAY + " - " + CC.WHITE + s + CC.GRAY + " (Tokens: " + playerDataEntry.getTokens() + ")" + CC.LIME + " ●");
                        } else {
                            sender.sendMessage(CC.GRAY + " - " + CC.WHITE + s + CC.GRAY + " (Tokens: " + playerDataEntry.getTokens() + ")" + CC.RED + " ●");
                        }
                    }
                });
                break;

            case "toggle":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                plugin = LoungeWhitelist.getInstance();

                plugin.setWhitelist(!LoungeWhitelist.getInstance().isWhitelist());
                sender.sendMessage(CC.WHITE + "Whitelist has been turned " + (plugin.isWhitelist() ? CC.LIME + "on" : CC.RED + "off"));
                break;

            case "status":
                if (!sender.hasPermission("whitelist.admin")) {
                    sender.hasPermission(CC.RED + "No perms!");
                    return;
                }

                plugin = LoungeWhitelist.getInstance();

                sender.sendMessage(CC.YELLOW + "Whitelist status: " + (plugin.isWhitelist() ? CC.LIME + "ON" : CC.RED + "OFF"));
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(CC.LINE + "----------------------");
        sender.sendMessage(CC.BD_GREEN + "User Commands");
        sender.sendMessage(CC.GREEN + "/whitelist add <user>");
        sender.sendMessage(CC.GREEN + "/whitelist tokens <user>");
        if (sender.hasPermission("whitelist.admin")) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.BD_PURPLE + "Admin Commands");
            sender.sendMessage(CC.PURPLE + "/whitelist addtokens <user> <amount>");
            sender.sendMessage(CC.PURPLE + "/whitelist settokens <user> <amount>");
            sender.sendMessage(CC.PURPLE + "/whitelist removetokens <user> <amount>");
            sender.sendMessage(CC.PURPLE + "/whitelist removeplayer <user>");
            sender.sendMessage(CC.PURPLE + "/whitelist listplayers");
            sender.sendMessage(CC.PURPLE + "/whitelist toggle");
            sender.sendMessage(CC.PURPLE + "/whitelist status");
        }
        sender.sendMessage(CC.LINE + "----------------------");
    }
}