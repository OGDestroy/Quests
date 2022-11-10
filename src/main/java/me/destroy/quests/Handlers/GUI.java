package me.destroy.quests.Handlers;

import me.destroy.quests.Quests;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GUI implements Listener {

    public final QuestHandler questHandler;
    public GUI(QuestHandler questHandler) {
        this.questHandler = questHandler;
    }
    public final HashMap<UUID, Inventory> inventorys = new HashMap<>();

    public Inventory addItems(Inventory inv,String id){

        if(!QuestHandler.checkCompletion(id,1)) {
            inv.setItem(0, easy1());
            inv.setItem(1, medium1());
            inv.setItem(2, hard1());
        }else{
            inv.setItem(0,nullItem());
            inv.setItem(1,nullItem());
            inv.setItem(2,nullItem());
        }

        inv.setItem(3,nullItem());
        inv.setItem(4,nullItem());
        inv.setItem(5,nullItem());

        if(!QuestHandler.checkCompletion(id,2)) {
            inv.setItem(6,easy2());
            inv.setItem(7,medium2());
            inv.setItem(8,hard2());
        }else{
            inv.setItem(6,nullItem());
            inv.setItem(7,nullItem());
            inv.setItem(8,nullItem());
        }

        return inv;
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        inventorys.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        Inventory inv = inventorys.get(p.getUniqueId());

        String ip = Quests.ipString(p);
        if (!e.getInventory().equals(inv)) return;
        if(Objects.equals(e.getClickedInventory(), p.getInventory())){
            e.setResult(Event.Result.DENY);
            return;
        }
        e.setCancelled(true);
        final ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;
        if(item.getItemMeta() == null)return;

        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 1 Easy", NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest1.Difficulty",1);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 1 Level Set To Easy");
        }
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 2 Easy", NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest2.Difficulty",1);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 2 Level Set To Easy");
        }
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 1 Medium", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest1.Difficulty",2);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 1 Level Set To Medium");
        }
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 2 Medium", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest2.Difficulty",2);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 2 Level Set To Medium");
        }
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 1 Hard", NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest1.Difficulty",3);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 1 Level Set To Hard");
        }
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("Quest 2 Hard", NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))) {
            Config.getQuests().set(ip + ".Quest2.Difficulty",3);
            inventorys.remove(p.getUniqueId());
            Config.saveQuests();
            e.getInventory().close();
            p.sendMessage(ChatColor.GREEN + "Quest 2 Level Set To Hard");
        }
        e.getInventory().close();
    }

    // Cancel dragging in the inventory
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        Inventory inv = inventorys.get(e.getWhoClicked().getUniqueId());
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
        if (e.getInventory().equals(e.getWhoClicked().getInventory())) {
            e.setCancelled(true);
        }
    }
    public ItemStack easy1(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial1");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestEasy1") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 1 Easy", NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public ItemStack easy2(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial2");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestEasy2") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 2 Easy", NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public ItemStack medium1(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial1");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestMedium1") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 1 Medium", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public ItemStack medium2(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial2");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestMedium2") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 2 Medium", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public ItemStack hard1(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial1");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestHard1") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 1 Hard", NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public ItemStack hard2(){
        ItemStack item = Quests.plugin.getConfig().getItemStack("QuestMaterial2");
        if(item == null)return null;
        item = new ItemStack(item.getType(),1);
        ItemMeta meta1 = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + "Collect " + Quests.plugin.getConfig().getInt("QuestHard2") + " " + item.getType()));
        meta1.displayName(Component.text("Quest 2 Hard", NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta1.lore(lore);
        item.setItemMeta(meta1);
        return item;
    }
    public static ItemStack nullItem(){
        return new ItemStack(Material.GLASS_PANE,1);
    }
}
