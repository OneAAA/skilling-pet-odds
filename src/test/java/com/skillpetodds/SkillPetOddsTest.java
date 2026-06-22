package com.skillpetodds;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SkillPetOddsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SkillPetOddsPlugin.class);
		RuneLite.main(args);
	}
}