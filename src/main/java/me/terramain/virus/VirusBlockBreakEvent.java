package me.terramain.virus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class VirusBlockBreakEvent implements Listener {


    @EventHandler
    public void breakBlock(BlockBreakEvent e){
        boolean flag = false;

        Virus breakVirus = null;
        for (Virus virus : VirusManager.virusList) {
            if (virus.getLocation().getBlock().equals( e.getBlock() )){
                flag = true;
                breakVirus = virus;
            }
        }
        if (!flag) return;
        VirusManager.virusList.remove(breakVirus);
    }
}
