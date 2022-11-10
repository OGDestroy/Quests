package me.destroy.quests;

import me.destroy.quests.Handlers.Config;
import me.destroy.quests.Handlers.GUI;
import me.destroy.quests.Handlers.QuestHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;


import java.util.*;

public final class Quests extends JavaPlugin implements CommandExecutor, TabCompleter {

    public static Quests plugin;
    public QuestHandler questHandler;
    public GUI gui;

    public Timer timer = new Timer();
    @Override
    public void onEnable() {
        plugin = this;
        gui = new GUI(new QuestHandler());
        questHandler = new QuestHandler();

        saveDefaultConfig();
        Config.setupQuests();
        Config.setupQuestOptions();

        getServer().getPluginManager().registerEvents(gui,this);
        getServer().getPluginManager().registerEvents(new QuestHandler(),this);

        getCommand("QuestOptions").setExecutor(this);
        getCommand("Quests").setExecutor(this);
        getCommand("QuestOptions").setTabCompleter(this);

        timer.scheduleAtFixedRate(new SchedularTask(),getDate(24,0),1000*24*60*60);

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player p) {
            Random random = new Random();
            switch (cmd.getName().toLowerCase()) {
                case "quests" -> {
                    Config.reloadQuests();
                    Inventory inv = Bukkit.createInventory(null, 9, (Component.text("Upgrades", NamedTextColor.GREEN)));
                    p.openInventory(gui.addItems(inv, ipString(p)));
                    gui.inventorys.put(p.getUniqueId(), inv);
                    return true;
                }
                case "questoptions" -> {
                    if (args.length == 0) return true;
                    switch (args[0].toLowerCase()) {
                        case "reload" -> {
                            reloadConfig();
                            Config.reloadQuests();
                            Config.reloadQuestOptions();
                            return true;
                        }
                        case "save" -> {
                            saveConfig();
                            Config.saveQuests();
                            Config.saveQuestOptions();
                            return true;
                        }
                        case "setlocation" -> {
                            p.getInventory().addItem(easyQuestVillager());
                            p.getInventory().addItem(mediumQuestVillager());
                            p.getInventory().addItem(hardQuestVillager());
                            return true;
                        }
                        case "changequest" -> {
                            if (args.length < 2) return true;
                            Config.reloadQuestOptions();
                            switch (args[1].toLowerCase()) {
                                case "1" -> {
                                    ConfigurationSection section = Config.getQuestOptions().getConfigurationSection("Quests");
                                    if (section.getKeys(false).isEmpty()) return true;
                                    int rand = random.nextInt(section.getKeys(false).size());
                                    int easy = Config.getQuestOptions().getInt("Quests." + rand + ".QuestEasy");
                                    int medium = Config.getQuestOptions().getInt("Quests." + rand + ".QuestMedium");
                                    int hard = Config.getQuestOptions().getInt("Quests." + rand + ".QuestHard");
                                    ItemStack type = Config.getQuestOptions().getItemStack("Quests." + rand + ".Type");

                                    getConfig().set("QuestMaterial1", type);
                                    getConfig().set("QuestEasy1", easy);
                                    getConfig().set("QuestMedium1", medium);
                                    getConfig().set("QuestHard1", hard);
                                    saveConfig();
                                    p.sendMessage(ChatColor.GREEN + "Quest 1 Type Changed Successfully");
                                }
                                case "2" -> {
                                    ConfigurationSection section = Config.getQuestOptions().getConfigurationSection("Quests");
                                    if (section.getKeys(false).isEmpty()) return true;
                                    int rand = random.nextInt(section.getKeys(false).size());
                                    int easy = Config.getQuestOptions().getInt("Quests." + rand + ".QuestEasy");
                                    int medium = Config.getQuestOptions().getInt("Quests." + rand + ".QuestMedium");
                                    int hard = Config.getQuestOptions().getInt("Quests." + rand + ".QuestHard");
                                    ItemStack type = Config.getQuestOptions().getItemStack("Quests." + rand + ".Type");

                                    getConfig().set("QuestMaterial2", type);
                                    getConfig().set("QuestEasy2", easy);
                                    getConfig().set("QuestMedium2", medium);
                                    getConfig().set("QuestHard2", hard);

                                    saveConfig();
                                    p.sendMessage(ChatColor.GREEN + "Quest 2 Type Changed Successfully");
                                }
                            }
                            for (String s : Config.getQuests().getStringList("Players")) {
                                resetPlayersQuests(s);
                            }
                            return true;
                        }
                        case "additem" -> {
                            if (args.length < 4) return true;
                            int amountEasy = Integer.parseInt(args[1]);
                            int amountMedium = Integer.parseInt(args[2]);
                            int amountHard = Integer.parseInt(args[3]);
                            if (!Config.getQuestOptions().contains("Quests")) {
                                Config.getQuestOptions().createSection("Quests");
                                Config.saveQuestOptions();
                            }
                            ConfigurationSection section = Config.getQuestOptions().getConfigurationSection("Quests");
                            int length = section.getKeys(false).size();

                            Config.getQuestOptions().set("Quests." + length + ".QuestEasy", amountEasy);
                            Config.getQuestOptions().set("Quests." + length + ".QuestMedium", amountMedium);
                            Config.getQuestOptions().set("Quests." + length + ".QuestHard", amountHard);
                            Config.getQuestOptions().set("Quests." + length + ".Count", 0);
                            Config.getQuestOptions().set("Quests." + length + ".Complete", false);
                            Config.getQuestOptions().set("Quests." + length + ".Type", p.getInventory().getItemInMainHand().asQuantity(1));
                            Config.saveQuestOptions();
                            saveConfig();
                            p.sendMessage(ChatColor.GREEN + "Quest Item Added Successfully");
                        }
                    }
                }
            }
        }
        return true;
    }
    public void resetPlayersQuests(String id){

        if(Config.getQuests().contains(id.toString())){
            Config.getQuests().set(id+ ".Quest1.Count",0);
            Config.getQuests().set(id+ ".Quest2.Count",0);
            Config.getQuests().set(id+ ".Quest1.Complete",false);
            Config.getQuests().set(id+ ".Quest2.Complete",false);
            Config.getQuests().set(id+ ".Quest1.Difficulty",0);
            Config.getQuests().set(id+ ".Quest2.Difficulty",0);
            Config.saveQuests();
        }
        else{
            QuestHandler.initializePlayer(id);
        }
    }

    public static ArrayList<String> tabCompleter(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Reload");
        list.add("Save");
        list.add("SetLocation");
        list.add("AddItem");
        list.add("ChangeQuest");
        return list;
    }
    public static ArrayList<String> tabCompleter2(){
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        return list;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if(cmd.getName().equalsIgnoreCase("questoptions")) {
            if(args.length == 1){
                return StringUtil.copyPartialMatches(args[0], tabCompleter(), new ArrayList<>());
            }
            if(args.length == 2){
                return StringUtil.copyPartialMatches(args[1], tabCompleter2(), new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }
    public ItemStack easyQuestVillager(){
        ItemStack item = new ItemStack(Material.VILLAGER_SPAWN_EGG,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Quests Villager Easy");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "- Easy");
        lore.add("Place me to set a location ");
        lore.add("for quest items to be dropped");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack mediumQuestVillager(){
        ItemStack item = new ItemStack(Material.VILLAGER_SPAWN_EGG,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Quests Villager Medium");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "- Medium");
        lore.add("Place me to set a location ");
        lore.add("for quest items to be dropped");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack hardQuestVillager(){
        ItemStack item = new ItemStack(Material.VILLAGER_SPAWN_EGG,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Quests Villager Hard");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "- Hard");
        lore.add("Place me to set a location ");
        lore.add("for quest items to be dropped");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static String ipString(Player p){
        if(p.getAddress() == null)return null;
        String address = p.getAddress().getHostString();
        String[] addressArray= address.split("\\.");
        StringBuilder builder = new StringBuilder();
        for(String s:addressArray){
            builder.append(s);
        }
        return builder.toString();
    }
    public void schedular(){
        TimeZone zone = TimeZone.getTimeZone("CST");

        Calendar calendar = Calendar.getInstance(zone);
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR,12);
        date.set(Calendar.MINUTE,25);
        date.set(Calendar.SECOND,0);
        date.set(Calendar.AM_PM,Calendar.PM);
    }

    public class SchedularTask extends TimerTask{

        @Override
        public void run() {
            Bukkit.broadcastMessage(ChatColor.GREEN + (ChatColor.BOLD + "Quests Reset"));
            for(Player p:Bukkit.getOnlinePlayers()){
                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1F,1F);
            }
            resetQuest();
        }
    }

    private static Date getDate(int hour,int minutes) {
        Calendar date = new GregorianCalendar();
        Calendar result = new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DATE),hour,
                minutes);
        result.setTimeZone(TimeZone.getTimeZone("EST"));
        return result.getTime();
    }

    public void resetQuest(){
        Config.reloadQuestOptions();

        Random random = new Random();
        ConfigurationSection section = Config.getQuestOptions().getConfigurationSection("Quests");
        int rand = random.nextInt(section.getKeys(false).size());
        int easy = Config.getQuestOptions().getInt("Quests." + rand + ".QuestEasy");
        int medium = Config.getQuestOptions().getInt("Quests." + rand + ".QuestMedium");
        int hard = Config.getQuestOptions().getInt("Quests." + rand + ".QuestHard");
        ItemStack type = Config.getQuestOptions().getItemStack("Quests." + rand + ".Type");

        getConfig().set("QuestMaterial1", type);
        getConfig().set("QuestEasy1", easy);
        getConfig().set("QuestMedium1", medium);
        getConfig().set("QuestHard1", hard);
        saveConfig();
        //p.sendMessage(ChatColor.GREEN + "Quest 1 Type Changed Successfully");

        section = Config.getQuestOptions().getConfigurationSection("Quests");
        rand = random.nextInt(section.getKeys(false).size());
        easy = Config.getQuestOptions().getInt("Quests." + rand + ".QuestEasy");
        medium = Config.getQuestOptions().getInt("Quests." + rand + ".QuestMedium");
        hard = Config.getQuestOptions().getInt("Quests." + rand + ".QuestHard");
        type = Config.getQuestOptions().getItemStack("Quests." + rand + ".Type");

        getConfig().set("QuestMaterial2", type);
        getConfig().set("QuestEasy2", easy);
        getConfig().set("QuestMedium2", medium);
        getConfig().set("QuestHard2", hard);

        saveConfig();
        //p.sendMessage(ChatColor.GREEN + "Quest 2 Type Changed Successfully");


        for (String s : Config.getQuests().getStringList("Players")) {
            resetPlayersQuests(s);
        }
    }


}
