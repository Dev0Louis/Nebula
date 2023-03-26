package dev.louis.nebula.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "Nebula")
public class NebulaConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = 1L, max = 100L)
    @Comment("In Ticks")
    int spellCooldown = 10;

    public int getSpellCooldown() {
        return spellCooldown;
    }
}
