package me.destroy.quests.Handlers;

import me.destroy.quests.Quests;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private static File questFile;
    private static FileConfiguration QuestsConfig;


    //Quests
    public static void setupQuests() {
        questFile = new File(Quests.plugin.getDataFolder(), "Quests.yml");

        if (!questFile.exists()) {
            try {
                questFile.createNewFile();
            } catch (IOException e) {
                //
            }
        }
        QuestsConfig = YamlConfiguration.loadConfiguration(questFile);
    }

    public static FileConfiguration getQuests() {
        return QuestsConfig;
    }

    public static void saveQuests() {
        try {
            QuestsConfig.save(questFile);
        } catch (IOException e) {
            System.out.println("File Not Saved");
        }
    }

    public static void reloadQuests() {
        QuestsConfig = YamlConfiguration.loadConfiguration(questFile);
    }

    private static File questOptionsFile;
    private static FileConfiguration QuestOptionsConfig;


    //Quest Options
    public static void setupQuestOptions() {
        questOptionsFile = new File(Quests.plugin.getDataFolder(), "QuestOptions.yml");

        if (!questOptionsFile.exists()) {
            try {
                questOptionsFile.createNewFile();


             } catch (IOException e) {
                //
            }
        }
        QuestOptionsConfig = YamlConfiguration.loadConfiguration(questOptionsFile);
    }

    public static FileConfiguration getQuestOptions() {
        return QuestOptionsConfig;
    }

    public static void saveQuestOptions() {
        try {
            QuestOptionsConfig.save(questOptionsFile);
        } catch (IOException e) {
            System.out.println("File Not Saved");
        }
    }

    public static void reloadQuestOptions() {
        QuestOptionsConfig = YamlConfiguration.loadConfiguration(questOptionsFile);
    }

}
