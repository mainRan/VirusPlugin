package me.terramain.virus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CMD implements TabExecutor {
    public CMD(Main plugin){
        plugin.getCommand("virus").setExecutor(this::onCommand);
        plugin.getCommand("virus").setTabCompleter(this::onTabComplete);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());

        if(!player.isOp()){
            sender.sendMessage("вы не админ!");
            return true;
        }

        if (args.length==0){
            sender.sendMessage("/virus help");
            return true;
        }

        if (args[0].equals("help")){
            sender.sendMessage(
                    "/virus help - получить подсказки.\n" +
                    "/virus create - создать на своём месте вирус.\n" +
                    "/virus list - список всех блоков (не рекомендуется делать когда вирусов уже много! Чат взорвётся!!!)\n" +
                    "/virus pause/begin - остановка/запуск "
            );
        }
        if (args[0].equals("create")){
            sender.sendMessage("создан новый блок вируса");
            VirusManager.virusList.add(new Virus(player.getLocation(),false,0));
            return true;
        }
        if (args[0].equals("list")){
            for (Virus virus : VirusManager.virusList) {
                sender.sendMessage(virus.toString());
            }
            sender.sendMessage("всего блоков вируса: " + VirusManager.virusList.size());
        }
        if (args[0].equals("pause")){
            sender.sendMessage("вирус заморожен.");
            VirusManager.isPause=true;
            return true;
        }
        if (args[0].equals("begin")){
            sender.sendMessage("вирус опять ползёт!");
            VirusManager.isPause=false;
            return true;
        }
        if (args[0].equals("removeall")){
            sender.sendMessage("все блоки вируса убраны.");
            for (Virus virus:VirusManager.virusList) {
                virus.getLocation().getBlock().setType(Material.AIR);
            }
            VirusManager.virusList = new ArrayList<>();
            return true;
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) return List.of("help","create","list","pause","begin","removeall");

        return new ArrayList<>();
    }
}
