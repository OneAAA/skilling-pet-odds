package com.skillpetodds;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SkillPetOddsConfig.GROUP)
public interface SkillPetOddsConfig extends Config
{
	String GROUP = "skillpetsodds"; // approach from LootTrackerConfig.java in runelite
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello skillers!";
	}
}
