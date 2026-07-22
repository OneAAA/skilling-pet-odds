package com.skillpetodds;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfile;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.events.SessionClose;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.google.gson.Gson;
import com.skillpetodds.ConfigSource;

// Much of the approach is taken from LootTrackerPlugin.java in runelite
@Slf4j
@PluginDescriptor(
	name = "Skilling Pet Odds Tracker"
)
public class SkillPetOddsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SkillPetOddsConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private SessionManager sessionManager;

	@Inject
	private Gson gson;

	private String profileKey;

	@Override
	protected void startUp() throws Exception
	{
		profileKey = null;
		// panel stuff on startup
		// icon stuff
		// toolbar stuff
		AccountSession accountSession = sessionManager.getAccountSession();
		if (accountSession != null)
		{
			// uuid to be used in future, nothing for now.
		}
		profileKey = configManager.getRSProfileKey();
		if (profileKey != null)
		{
			switchProfile(profileKey); // setup stuff more?
		}
	}

	@Subscribe
	public void onSessionOpen(SessionOpen sessionOpen)
	{
		// none yet, uuid things
	}

	@Subscribe
	public void onSessionClose(SessionClose sessionClose)
	{
		// none yet, uuid things
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		// future once configs added, panel stuff and untracked odds
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("SkillPetOdds stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	// This copied from LootTrackerPlugin.java
	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e)
	{
		final String profileKey = configManager.getRSProfileKey();
		if (profileKey == null)
		{
			return;
		}
		if (profileKey.equals(this.profileKey))
		{
			return;
		}
		switchProfile(profileKey);
	}

	@Provides
	SkillPetOddsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SkillPetOddsConfig.class);
	}

	// onPetRolled(String pet, String source, int level)
	// records a pet roll in the configs.
	// OFI: this naive approach pulls from configs every time, but could use a local store of all pet objects;
	// TBD if change is necessary
	public void onPetRolled(String pet, String source, int level)
	{
		// will assume that it is called with good pet name and source, and increment the level here.
		String profile = profileKey;
		if (Strings.isNullOrEmpty(profile))
		{
			log.debug("Cannot track pet roll without profile key");
			return;
		}
		String json = configManager.getConfiguration(SkillPetOddsConfig.GROUP, profile, "pets_" + pet);
		ConfigPet petObject;
		if (json == null)
		{
			log.debug("No json found for " + pet + ", making new configuration.");
			petObject = new ConfigPet(pet);
		}
		else
		{
			petObject = gson.fromJson(json, ConfigPet.class);
		}

		// pet grabbed, so apply the addRoll method.
		int addRollReturn = petObject.addRoll(source, level);
		// currently odds intended to be modified in ConfigPet.addRoll, not implemented yet
		if (addRollReturn < 0)
		{
			log.debug("onPetRolled failed, will not write to configs");
			return;
		}
		String newJson = gson.toJson(petObject);
		configManager.setConfiguration(SkillPetOddsConfig.GROUP, profile, "pets_" + pet, newJson);
	}

	// switchProfile to a particular key, loading relevant data from configs for side panel
	private void switchProfile(String profileKey)
	{
		return; // will need panel changes implemented here, i.e. loading different profile settings.
	}

	public void removePet(String pet)
	{
		String profile = profileKey;
		if (Strings.isNullOrEmpty(profile))
		{
			log.debug("Cannot remove pet without profile key");
			return;
		}
		configManager.unsetConfiguration(SkillPetOddsConfig.GROUP, profile, "pets_" + pet);
	}

	public void removeAllPets()
	{
		String profile = profileKey;
		if (Strings.isNullOrEmpty(profile))
		{
			log.debug("Cannot remove pet without profile key");
			return;
		}
		for (String key : configManager.getRSProfileConfigurationKeys(SkillPetOddsConfig.GROUP, profile, "pets_"))
		{
			configManager.unsetConfiguration(SkillPetOddsConfig.GROUP, profile, key);
		}
	}
}
