package me.terramain.virus;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirusManager {
    public static String virusPluginFailsCatalog = "plugins"+ File.separatorChar+"virusPlugin"+File.separatorChar;

    public static String virusDataFailPath = virusPluginFailsCatalog + "virusBlocks.data";
    public static String configFailPath = virusPluginFailsCatalog + "setting.data";
    public static Map<Player,Integer> virusedPlayers = new HashMap<>();



    public static List<Virus> virusList = new ArrayList<>();
    public static boolean isPause = false;
    public static Material virusBlockType = Material.GLASS;
    public static int tickSpeed = 5;


    public static void saveVirusBlocks() throws IOException {
        File dir = new File(virusPluginFailsCatalog);
        if (!dir.exists()) dir.mkdir();

        File file = new File(virusDataFailPath);
        if (file.exists()) file.delete();
        file.createNewFile();

        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file) );

        for (Virus virus : virusList) {
            bufferedWriter.write(virus.toString());
            bufferedWriter.newLine();
        }

        bufferedWriter.flush();
        bufferedWriter.close();
    }
    public static void loadVirusBlocks() throws IOException {
        File file = new File(virusDataFailPath);
        if (!file.exists()) return;
        BufferedReader bufferedReader = new BufferedReader( new FileReader(file) );


        String line = "";
        while ( ( line=bufferedReader.readLine() ) != null) {
            if (!line.equals("")) virusList.add(new Virus(line));
        }
        bufferedReader.close();
    }

    public static void saveCFG() throws IOException {
        File dir = new File(virusPluginFailsCatalog);
        if (!dir.exists()) dir.mkdir();

        File file = new File(configFailPath);
        if (file.exists()) file.delete();
        file.createNewFile();

        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file) );

        bufferedWriter.write(keyAndValueToStr( "virus_block", virusBlockType.toString() ));
        bufferedWriter.write(keyAndValueToStr( "tick_speed", tickSpeed+"" ));


        bufferedWriter.flush();
        bufferedWriter.close();
    }
    public static String keyAndValueToStr(String key, String value){return key + ":" + value;}
    public static void loadCFG() throws IOException {
        File file = new File(configFailPath);
        if (!file.exists()) return;
        BufferedReader bufferedReader = new BufferedReader( new FileReader(file) );


        String line = "";
        while ( ( line=bufferedReader.readLine() ) != null) {
            if (!line.equals("")) {
                String[] args = line.split(":");

                if (args[0].equals("virus_block")) virusBlockType = Material.getMaterial(args[1]);
                if (args[0].equals("tick_speed")) tickSpeed = Integer.parseInt(args[1]);
            }
        }
        bufferedReader.close();
    }



    public static void reTick(){
        for (int i = 0; i < virusList.size(); i++) {
            Virus virus = virusList.get(i);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ( Main.getDistance(virus.getLocation(),player.getLocation()) <= 1.5){
                    PotionEffect potionEffect1 = new PotionEffect(PotionEffectType.BLINDNESS,10*20,1);
                    PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.POISON,13*20,1);

                    player.addPotionEffect(potionEffect1);
                    player.addPotionEffect(potionEffect2);

                    if ( ((int)(Math.random()*100)) <= 8 && player.getGameMode()!=GameMode.CREATIVE && player.getGameMode()!=GameMode.SPECTATOR){
                        virusedPlayers.put(player,0);
                    }
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Integer playerVirusedTime = virusedPlayers.get(player);
            if (playerVirusedTime != null) {
                virusedPlayers.remove(player);
                if (playerVirusedTime <= 5 * 60) {
                    virusedPlayers.put(player, playerVirusedTime + 5);
                }

                if ( ((int)(Math.random()*100)) <= 1){ // 1/0 = 2%
                    Block block = player.getTargetBlock(null,7);
                    if (block.getType() != Material.AIR) {
                        virusList.add(new Virus(block.getLocation(), false, 0));
                    }
                }
            }
        }
    }
    public static void allMutation(){
        if (isPause) return;

        int size = virusList.size();
        if (size==0) return;

        else if (size < 10){
            mutation();
        }
        else if (size < 100){
            mutation();
            mutation();
            mutation();
        }
        else {
            int reMut = Math.min(800 , (int)(size/100)+1);
            for (int i = 0; i < reMut; i++) mutation();
        }

        for (int i = 0; i < size-1; i++) {
            Virus virus = virusList.get(i);
            virus.setLiveOfSeconds( virus.getLiveOfSeconds()+3 );
            virusList.set(i,virus);

            if (virus.getLiveOfSeconds() > 500){
                if ( (((int)(Math.random()*100)) < 50 && virus.isIncreased()) || (((int)(Math.random()*100)) < 10 && !virus.isIncreased()) ) {
                    virusList.remove(i);
                    virus.getLocation().getBlock().setType(Material.AIR);
                    i--;
                    size--;
                }
            }
        }
    }

    public static void mutation(){
        Virus virus = virusList.get( (int)(Math.random()*virusList.size()) );

        Location mutLoc = mutationLocation(virus.getLocation());
        if (virus.getLocation().getBlock().getType() != VirusManager.virusBlockType){
            virusList.remove(virus);
            return;
        }
        if (mutLoc==virus.getLocation()) {return;}
        if (mutLoc.getBlock().getType()==Material.AIR) {return;}

        virusList.remove(virus);
        virus.setIncreased(true);
        virusList.add(virus);

        int live = 0;
        if (mutLoc.getBlock().getType()==Material.WATER) live+=350;
        if (mutLoc.getBlock().getType()==Material.LAVA) live+=200;
        if (mutLoc.getBlock().getType()==Material.DIRT) live-=100;
        if (mutLoc.getBlock().getType()==Material.GRASS_BLOCK) live-=100;
        virusList.add(new Virus(mutLoc,false,live));
    }

    public static Location mutationLocation(Location location){
        Location mutLoc = location.getBlock().getLocation();

        double minPlayerDistance = 99999999;
        Location playerLoc = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = Main.getDistance(location,player.getLocation());
            if (distance < minPlayerDistance && player.getGameMode() != GameMode.SPECTATOR){
                minPlayerDistance = distance;
                playerLoc = player.getLocation();
            }
        }

        int chanceXP = 30;
        int chanceXM = 30;
        int chanceYP = 30;
        int chanceYM = 30;
        int chanceZP = 30;
        int chanceZM = 30;

        if (playerLoc != null){
            boolean xP = (playerLoc.getX() > location.getX());
            boolean xM = (playerLoc.getX() < location.getX());
            boolean yP = (playerLoc.getY() > location.getY());
            boolean yM = (playerLoc.getY() < location.getY());
            boolean zP = (playerLoc.getZ() > location.getZ());
            boolean zM = (playerLoc.getZ() < location.getZ());

            if (xP) {chanceXP=70;}
            if (xM) {chanceXM=70;}
            if (yP) {chanceYP=70;}
            if (yM) {chanceYM=70;}
            if (zP) {chanceZP=70;}
            if (zM) {chanceZM=70;}
        }



        if ( ((int)(Math.random()*100)) <= chanceXP) mutLoc.setX(location.getX()+1);
        if ( ((int)(Math.random()*100)) <= chanceYP) mutLoc.setY(location.getY()+1);
        if ( ((int)(Math.random()*100)) <= chanceZP) mutLoc.setZ(location.getZ()+1);

        if ( ((int)(Math.random()*100)) <= chanceXM) mutLoc.setX(location.getX()-1);
        if ( ((int)(Math.random()*100)) <= chanceYM) mutLoc.setY(location.getY()-1);
        if ( ((int)(Math.random()*100)) <= chanceZM) mutLoc.setZ(location.getZ()-1);

        return mutLoc;
    }

    public static Location relative(Location location, int x, int y, int z){
        location.setX( location.getX()+x );
        location.setY( location.getY()+y );
        location.setZ( location.getZ()+z );
        return location;
    }
}
