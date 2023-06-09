package me.terramain.virus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new VirusBlockBreakEvent() ,this);
        new CMD(this);
        try {
            VirusManager.loadVirusBlocks();
        } catch (IOException e) {e.printStackTrace();}

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, VirusManager::allMutation,VirusManager.tickSpeed,VirusManager.tickSpeed);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, VirusManager::reTick,5,5);
    }

    @Override
    public void onDisable() {

        try {
            VirusManager.saveVirusBlocks();
        } catch (IOException e) {e.printStackTrace();}

    }


    public static double getDistance(Location loc1, Location loc2){
        double result;

        double xr = (loc1.getX()-loc2.getX())*(loc1.getX()-loc2.getX());
        double yr = (loc1.getY()-loc2.getY())*(loc1.getY()-loc2.getY());
        double zr = (loc1.getZ()-loc2.getZ())*(loc1.getZ()-loc2.getZ());

        result = xr+yr+zr;

        result = Math.sqrt(result);

        return result;
    }


}
