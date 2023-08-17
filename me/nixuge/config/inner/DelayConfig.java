package me.nixuge.config.inner;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.nixuge.config.ConfigPart;

@Getter
public class DelayConfig extends ConfigPart {
    public DelayConfig(ConfigurationSection conf) {
        super(conf);
        ticksPerHungerRegain = getInt("ticksPerHalfHungerRegain", 10);
        spawnProtectionDuration = getInt("spawnProtectionDuration", 10);
    }
    
    private final int ticksPerHungerRegain;
    private final int spawnProtectionDuration;
}
