package me.terramain.virus;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Virus {
    private Location location;
    private boolean isIncreased;
    private long liveOfSeconds;


    public Virus(Location location, boolean isIncreased, long liveOfSeconds) {
        this.location = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        this.isIncreased = isIncreased;
        this.liveOfSeconds = liveOfSeconds;
        spawn();
    }
    public Virus(String virusText){
        String[] args = virusText.split(" ");
        location = new Location(Bukkit.getWorld(args[3]),Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        isIncreased = Boolean.parseBoolean(args[4]);
        liveOfSeconds = Long.parseLong(args[5]);
        spawn();
    }

    public void spawn(){
        location.getBlock().setType(VirusManager.virusBlockType);
    }


    public Location getLocation() {return location;}
    public void setLocation(Location location) {this.location = location;}

    public boolean isIncreased() {return isIncreased;}
    public void setIncreased(boolean increased) {isIncreased = increased;}

    public long getLiveOfSeconds() {return liveOfSeconds;}
    public void setLiveOfSeconds(long liveOfSeconds) {this.liveOfSeconds = liveOfSeconds;}

    @Override
    public String toString() {
        return  location.getBlockX()+" "+
                location.getBlockY()+" "+
                location.getBlockZ()+" "+
                location.getWorld().getName()+ " "+
                isIncreased+" "+
                liveOfSeconds;
    }
}
