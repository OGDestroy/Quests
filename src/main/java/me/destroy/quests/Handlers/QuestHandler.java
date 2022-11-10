package me.destroy.quests.Handlers;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.destroy.quests.Quests;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestHandler implements Listener {

    public static void initializePlayer(String ip){

        if(!Config.getQuests().contains(ip.toString())){
            Config.getQuests().createSection(ip.toString());
            Config.getQuests().set(ip+ ".Quest1.Count",0);
            Config.getQuests().set(ip+ ".Quest2.Count",0);
            Config.getQuests().set(ip+ ".Quest1.Complete",false);
            Config.getQuests().set(ip+ ".Quest2.Complete",false);
            Config.getQuests().set(ip + ".Quest1.Difficulty",0);
            Config.getQuests().set(ip+ ".Quest2.Difficulty",0);

            List<String> list = Config.getQuests().getStringList("Players");
            list.add(ip.toString());
            Config.getQuests().set("Players",list);
            Config.saveQuests();
        }
    }
    public static int getQuest1(String ip){
        return Config.getQuests().getInt(ip.toString()  + ".Quest1.Count");
    }
    public static int getQuest2(String ip){
        return Config.getQuests().getInt(ip.toString()  + ".Quest2.Count");
    }

    public void setQuest(String ip,UUID id,int add,int type) {
        int status = 0;
        switch (type) {
            case 1 -> {
                status = Config.getQuests().getInt(ip.toString()  + ".Quest1.Count");
                Config.getQuests().set(ip + ".Quest1.Count", status + add);
                Config.saveQuests();
                int max = getDifficulty1(ip);
                if (status + add >= max) {
                    rewardQuest1(id,getDifficulty1Level(ip));
                    Config.getQuests().set(ip + ".Quest1.Count", 0);
                    Config.getQuests().set(ip + ".Quest1.Complete", true);
                    Config.saveQuests();
                }
            }
            case 2 -> {
                status = Config.getQuests().getInt(ip.toString()  + ".Quest2.Count");
                Config.getQuests().set(ip + ".Quest2.Count", status + add);
                Config.saveQuests();
                int max = getDifficulty2(ip);
                if (status + add >= max) {
                    rewardQuest2(id,getDifficulty2Level(ip));
                    Config.getQuests().set(ip + ".Quest2.Count", 0);
                    Config.getQuests().set(ip + ".Quest2.Complete", true);
                    Config.saveQuests();
                }
            }
        }
    }

    public static void rewardQuest1(UUID id,int difficulty){
        //Bukkit.broadcastMessage(ChatColor.GOLD + "Reward 1");
        Player p = Bukkit.getPlayer(id);
        if(p == null)return;
        fireworks(p.getLocation());

        switch (difficulty){
            case 1 -> {
                p.getInventory().addItem(createComp3(1));
            }
            case 2 -> {
                p.getInventory().addItem(createComp3(2));
            }
            case 3 -> {
                p.getInventory().addItem(createComp2(1));
            }
        }
    }

    public static void rewardQuest2(UUID id,int difficulty){
        //Bukkit.broadcastMessage(ChatColor.GOLD + "Reward 2");
        Player p = Bukkit.getPlayer(id);
        if(p == null)return;
        fireworks(p.getLocation());
        switch (difficulty){
            case 1 -> {
                p.getInventory().addItem(createComp3(1));
            }
            case 2 -> {
                p.getInventory().addItem(createComp3(2));
            }
            case 3 -> {
                p.getInventory().addItem(createComp2(1));
            }
        }
    }

    public static boolean checkCompletion(String ip,int quest){
        boolean complete;
        switch (quest){
            case 1 -> {
                complete = Config.getQuests().getBoolean(ip.toString()  + ".Quest1.Complete");
                return complete;
            }
            case 2 -> {
                complete = Config.getQuests().getBoolean(ip.toString()  + ".Quest2.Complete");
                return complete;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Player p = e.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();
            if(item.getItemMeta() == null || item.getItemMeta().getLore() == null)return;
            if(item.getItemMeta().getLore().contains("Place me to set a location ")){
                e.setCancelled(true);
                Location loc = e.getInteractionPoint();
                if(loc == null)return;
                loc.setX(loc.getBlockX());
                loc.setZ(loc.getBlockZ());
                loc.setY(loc.getBlockY());
                Location newLoc = loc.toCenterLocation();
                newLoc.setY(newLoc.getY() - 1);
                if(item.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "- Easy")){
                    if(locationsEasy().contains(loc))return;
                    UUID id = spawnVillagerEasy(loc);
                    newLoc.getBlock().setType(Material.OBSIDIAN);
                    int length = 0;
                    ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Easy");
                    if(section != null) {
                        length = section.getKeys(false).size();
                    }
                    Quests.plugin.getConfig().set("Locations.Easy." + length,loc);
                    Quests.plugin.getConfig().set("Villagers.Easy." + length,id.toString());
                    Quests.plugin.saveConfig();
                }
                if(item.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "- Medium")){
                    if(locationsMedium().contains(loc))return;
                    UUID id = spawnVillagerMedium(loc);
                    newLoc.getBlock().setType(Material.OBSIDIAN);
                    int length = 0;
                    ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Medium");
                    if(section != null) {
                        length = section.getKeys(false).size();
                    }
                    Quests.plugin.getConfig().set("Locations.Medium." + length,loc);
                    Quests.plugin.getConfig().set("Villagers.Medium." + length,id.toString());
                    Quests.plugin.saveConfig();
                }
                if(item.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "- Hard")){
                    if(locationsHard().contains(loc))return;
                    UUID id = spawnVillagerHard(loc);
                    newLoc.getBlock().setType(Material.OBSIDIAN);
                    int length = 0;
                    ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Hard");
                    if(section != null) {
                        length = section.getKeys(false).size();
                    }
                    Quests.plugin.getConfig().set("Locations.Hard." + length,loc);
                    Quests.plugin.getConfig().set("Villagers.Hard." + length,id.toString());
                    Quests.plugin.saveConfig();
                }

                if(item.getAmount() == 1) item.setAmount(0);
                else item.setAmount(item.getAmount() - 1);
            }
        }
    }


    @EventHandler
    public void onSpawn(EntityAddToWorldEvent e){
        if(!(e.getEntity() instanceof Villager))return;
        if(e.getEntity().isCustomNameVisible() && e.getEntity().getCustomName() != null){
            Villager villager = (Villager) e.getEntity();
            if(villager.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Quests Cleric Easy") ||
                    villager.getCustomName().equalsIgnoreCase( ChatColor.YELLOW + "Quests Cleric Medium" ) ||
                    villager.getCustomName().equalsIgnoreCase( ChatColor.RED + "Quests Cleric Hard")){
                //Bukkit.broadcastMessage("Quest Villager Loaded");
                villager.setCollidable(false);
            }
        }
    }
    public static UUID spawnVillagerEasy(Location loc){
        loc = loc.toCenterLocation();
        loc.setY(loc.getBlockY());
        Villager villager = loc.getWorld().spawn(loc,Villager.class);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        villager.setHealth(100D);
        villager.setVelocity(villager.getVelocity().multiply(0));
        villager.setCanPickupItems(false);
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        villager.setCollidable(false);
        villager.setPersistent(true);
        villager.setGravity(false);
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100);
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.GREEN + "Quests Cleric Easy");
        return villager.getUniqueId();
    }
    public static UUID spawnVillagerMedium(Location loc){
        loc = loc.toCenterLocation();
        loc.setY(loc.getBlockY());
        Villager villager = loc.getWorld().spawn(loc,Villager.class);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        villager.setHealth(100D);
        villager.setCanPickupItems(false);
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        villager.setGravity(false);
        villager.setCollidable(false);
        villager.setPersistent(true);
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100);
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.YELLOW + "Quests Cleric Medium");
        return villager.getUniqueId();
    }
    public static UUID spawnVillagerHard(Location loc){
        loc = loc.toCenterLocation();
        loc.setY(loc.getBlockY());
        Villager villager = loc.getWorld().spawn(loc,Villager.class);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        villager.setHealth(100D);
        villager.setCanPickupItems(false);
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        villager.setGravity(false);
        villager.setCollidable(false);
        villager.setPersistent(true);
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100);
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.RED + "Quests Cleric Hard");
        return villager.getUniqueId();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        if(entity.getKiller() == null)return;
        if(entity.isCustomNameVisible() && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Quests Cleric Easy")){
            ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Villagers.Easy");
            if(section != null) {
                for (int i = 0; i < section.getKeys(false).size(); i++) {
                    String uuidS = Quests.plugin.getConfig().getString("Villagers.Easy." + i);
                    if(uuidS != null) {
                        UUID uuid = UUID.fromString(uuidS);
                        if(entity.getUniqueId().equals(uuid)) {
                            Quests.plugin.getConfig().set("Villagers.Easy." + i, null);
                            Quests.plugin.getConfig().set("Locations.Easy." + i, null);
                            Quests.plugin.saveConfig();
                        }
                    }
                }
            }
            return;
        }
        if(entity.isCustomNameVisible() && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(ChatColor.YELLOW + "Quests Cleric Medium")){
            ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Medium");
            if(section != null) {
                for (int i = 0; i < section.getKeys(false).size(); i++) {
                    String uuidS = Quests.plugin.getConfig().getString("Villagers.Medium." + i);
                    if(uuidS != null) {
                        UUID uuid = UUID.fromString(uuidS);
                        if(entity.getUniqueId().equals(uuid)) {
                            Quests.plugin.getConfig().set("Villagers.Medium." + i, null);
                            Quests.plugin.getConfig().set("Locations.Medium." + i, null);
                            Quests.plugin.saveConfig();
                        }
                    }
                }
            }
            return;
        }
        if(entity.isCustomNameVisible() && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(ChatColor.RED + "Quests Cleric Hard")){
            ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Hard");
            if(section != null) {
                for (int i = 0; i < section.getKeys(false).size(); i++) {
                    String uuidS = Quests.plugin.getConfig().getString("Villagers.Hard." + i);
                    if(uuidS != null) {
                        UUID uuid = UUID.fromString(uuidS);
                        if(entity.getUniqueId().equals(uuid)) {
                            Quests.plugin.getConfig().set("Villagers.Hard." + i, null);
                            Quests.plugin.getConfig().set("Locations.Hard." + i, null);
                            Quests.plugin.saveConfig();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e){

        ItemStack item = e.getItemDrop().getItemStack();
        int amount = item.getAmount();
        int status = 0;
        int quest = 0;
        int difficulty = 0;
        int questMaxAmount = 0;
        List<Location> locations;
        //UUID id = e.getPlayer().getUniqueId();
        if(e.getPlayer().getAddress() == null)return;
        String ip = Quests.ipString(e.getPlayer());
        if(ip == null)return;

        ItemStack questType1 = Quests.plugin.getConfig().getItemStack("QuestMaterial1");
        ItemStack questType2 = Quests.plugin.getConfig().getItemStack("QuestMaterial2");

        if(questType1 == null || questType2 == null)return;
        if(!(item.getType().equals(Material.SHULKER_BOX) || item.getType().equals(questType1.getType()) || item.getType().equals(questType2.getType())))return;

        Config.reloadQuests();

        BlockStateMeta im;
        ShulkerBox shulkerBox = null;

        if(item.getType().equals(Material.SHULKER_BOX)){
            im = (BlockStateMeta) item.getItemMeta();
            shulkerBox = (ShulkerBox) im.getBlockState();
            if(shulkerBox.getInventory().contains(questType1.getType()) && shulkerBox.getInventory().contains(questType2.getType())){
                //e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "The shulker contains two different quest items"));
                return;
            }
            if(!shulkerBox.getInventory().contains(questType1.getType()) && !shulkerBox.getInventory().contains(questType2.getType())){
                //e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "The shulker contains no quest items"));
                return;
            }
            if(shulkerBox.getInventory().contains(questType2.getType())){
                quest = 2;
                questMaxAmount = getDifficulty2(ip);
                amount = amountOfType(shulkerBox.getInventory(),questType2);
                if(amount == 0){
                    e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "Shulker must contain only the required Materials"));
                    return;
                }
                difficulty = getDifficulty2Level(ip);
                status = getQuest2(ip);
            }
            if(shulkerBox.getInventory().contains(questType1.getType())){
                quest = 1;
                questMaxAmount = getDifficulty1(ip);
                amount = amountOfType(shulkerBox.getInventory(),questType1);
                if(amount == 0) {
                    e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "Shulker must contain only the required Materials"));
                    return;
                }
                difficulty = getDifficulty1Level(ip);
                status = getQuest1(ip);
            }
        }
        if(item.getType().equals(questType1.getType())){
            status = getQuest1(ip);
            quest = 1;
            difficulty = getDifficulty1Level(ip);
            questMaxAmount = getDifficulty1(ip);
        }
        if(item.getType().equals(questType2.getType())){
            status = getQuest2(ip);
            quest = 2;
            difficulty = getDifficulty2Level(ip);
            questMaxAmount = getDifficulty2(ip);
        }

        switch (difficulty){
            case 1 -> {
                locations = locationsEasy();
            }
            case 2 -> {
                locations = locationsMedium();
            }
            case 3 -> {
                locations = locationsHard();
            }
            default -> locations = new ArrayList<>();
        }


        final int finalQuest = quest;
        final int finalStatus = status;
        final int finalQuestMaxAmount = questMaxAmount;
        final int finalAmount = amount;
        ShulkerBox finalShulkerBox = shulkerBox;
        final List<Location> finalLocations = locations;
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = e.getItemDrop().getLocation();

                for(Location locList: finalLocations){
                    if (locList.distance(loc) <= 1.25) {
                        if (finalShulkerBox != null) {
                            if (finalQuestMaxAmount == 0) {
                                e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "You must pick a difficulty level first"));
                                return;
                            }
                            if (checkCompletion(ip, finalQuest)) {
                                e.getPlayer().sendActionBar(Component.text("Status: " + "Complete"));
                                return;
                            }
                            item.setAmount(0);
                            int count = finalStatus + finalAmount;
                            e.getPlayer().sendActionBar(Component.text("Status: " + count + "/" + finalQuestMaxAmount));
                            setQuest(ip,e.getPlayer().getUniqueId(), finalAmount, finalQuest);
                        } else {
                            if (finalQuestMaxAmount == 0) {
                                e.getPlayer().sendActionBar(Component.text(ChatColor.RED + "You must pick a difficulty level first"));
                                return;
                            }
                            if (checkCompletion(ip, finalQuest)) {
                                e.getPlayer().sendActionBar(Component.text("Status: " + "Complete"));
                                return;
                            }
                            item.setAmount(0);
                            int count = finalStatus + finalAmount;
                            e.getPlayer().sendActionBar(Component.text("Status: " + count + "/" + finalQuestMaxAmount));
                            setQuest(ip,e.getPlayer().getUniqueId(), finalAmount, finalQuest);
                        }
                    }
                }
            }
        }.runTaskLater(Quests.plugin,10);
    }
    public int getDifficulty1(String id){
        int difficulty = Config.getQuests().getInt(id.toString() + ".Quest1.Difficulty");
        switch (difficulty){
            case 1 -> {
                return Quests.plugin.getConfig().getInt("QuestEasy1");
            }
            case 2 -> {
                return Quests.plugin.getConfig().getInt("QuestMedium1");
            }
            case 3 -> {
                return Quests.plugin.getConfig().getInt("QuestHard1");
            }
            default -> {
                return 0;
            }
        }
    }
    public int getDifficulty2(String id) {
        int difficulty = Config.getQuests().getInt(id.toString() + ".Quest2.Difficulty");
        switch (difficulty) {
            case 1 -> {
                return Quests.plugin.getConfig().getInt("QuestEasy2");
            }
            case 2 -> {
                return Quests.plugin.getConfig().getInt("QuestMedium2");
            }
            case 3 -> {
                return Quests.plugin.getConfig().getInt("QuestHard2");
            }
            default -> {
                return 0;
            }
        }
    }

    public int getDifficulty1Level(String id){
        return Config.getQuests().getInt(id.toString() + ".Quest1.Difficulty");
    }
    public int getDifficulty2Level(String id) {
        return Config.getQuests().getInt(id.toString() + ".Quest2.Difficulty");
    }
    public int amountOfType(Inventory inv, ItemStack type){
        int amount = 0;
        for(ItemStack item:inv.getContents()){
            if(item != null && !item.getType().equals(type.getType()))return 0;
            if (item != null && item.getType().equals(type.getType())) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e){

        new BukkitRunnable(){
            @Override
            public void run() {
                String ip = Quests.ipString(e.getPlayer());
                if(ip == null)return;
                initializePlayer(ip);
            }
        }.runTaskLater(Quests.plugin,5);

    }

    private static ItemStack createComp1(int amount) {
        ItemStack item = new ItemStack(Material.DRAGON_EGG, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName (ChatColor.BLUE +("Black Gem"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Shaped by a Dragon!");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
    private static ItemStack createComp3(int amount) {
        ItemStack item = new ItemStack(Material.LAPIS_LAZULI, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName (ChatColor.BLUE +("Zircon"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "A jewel of unknown worth...");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
    private static ItemStack createComp2(int amount) {
        ItemStack item = new ItemStack(Material.NETHERITE_INGOT, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName (ChatColor.BLUE +("Forged Tungsten"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "A gift from the Master Smith.");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
    private static ItemStack createComp4(int amount) {
        ItemStack item = new ItemStack(Material.REDSTONE, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName (ChatColor.BLUE +("Blood Stone"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Blood has been spilled");
        lore.add(ChatColor.DARK_PURPLE + "this night.");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public List<Location> locationsEasy(){
        List<Location> list = new ArrayList<>();
        int length = 0;
        ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Easy");
        if(section != null) {
            length = section.getKeys(false).size();
        }
        for(int i = 0;i<length;i++) {
            list.add(Quests.plugin.getConfig().getLocation("Locations.Easy." + i));
        }
        return list;
    }
    public List<Location> locationsMedium(){
        List<Location> list = new ArrayList<>();
        int length = 0;
        ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Medium");
        if(section != null) {
            length = section.getKeys(false).size();
        }
        for(int i = 0;i<length;i++) {
            list.add(Quests.plugin.getConfig().getLocation("Locations.Medium." + i));
        }
        return list;
    }
    public List<Location> locationsHard(){
        List<Location> list = new ArrayList<>();
        int length = 0;
        ConfigurationSection section =  Quests.plugin.getConfig().getConfigurationSection("Locations.Hard");
        if(section != null) {
            length = section.getKeys(false).size();
        }
        for(int i = 0;i<length;i++) {
            list.add(Quests.plugin.getConfig().getLocation("Locations.Hard." + i));
        }
        return list;
    }
    public static void fireworks(Location loc){
        //loc.setY(loc.getY());
        Firework firework = loc.getWorld().spawn(loc,Firework.class);
        FireworkMeta data = firework.getFireworkMeta();
        data.addEffect(FireworkEffect.builder().withColor(Color.PURPLE).withFade(Color.AQUA).with(FireworkEffect.Type.BALL).build());
        data.addEffect(FireworkEffect.builder().withColor(Color.PURPLE).withFade(Color.AQUA).with(FireworkEffect.Type.BURST).build());
        data.addEffect(FireworkEffect.builder().withColor(Color.BLUE).withFade(Color.AQUA).with(FireworkEffect.Type.STAR).withFlicker().build());
        firework.setVelocity(firework.getVelocity().multiply(0.5));
        firework.setFireworkMeta(data);
    }

}