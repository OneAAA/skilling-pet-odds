package com.skillpetodds;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

// Pet Odds Data Structure
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfigPet
{
    @EqualsAndHashCode.Include
    String name;
    double trackedChanceNoPet; // this is the chance of no pet
    double trackedRate;
    List<ConfigSource> sources;

    ConfigPet(String inName)
    {
        this.name = inName;
        this.trackedChanceNoPet = 1;
        this.trackedRate = 0;
        this.sources = new ArrayList<ConfigSource>(0);
    }

    // add a pet roll from a given source at a given level
    // mirrors the returns from addRoll in ConfigSource
    int addRoll(String source, int level)
    {
        if (this.sources == null)
        {
            this.sources = new ArrayList<ConfigSource>(0);
        }
        // check for source in sources, indexOf uses the equals methods defined by lombok to only care for name
        ConfigSource newSource = new ConfigSource(source);
        int sourcePos = this.sources.indexOf(newSource);
        if (sourcePos < 0) // new ConfigSource needed
        {
            this.sources.add(newSource);
            sourcePos = this.sources.size() - 1; // update sourcePos to point to end (where newSource is)
        }
        // now run addRoll on the ConfigSource at sourcePos
        int returnVal = this.sources.get(sourcePos).addRoll(level);
        if (returnVal < 0) return returnVal;
        returnVal = this.updateOdds(source, level);
        return returnVal;
    }

    int updateOdds(String source, int level)
    {
        return 0; // unimplemented
    }

    void clearSources()
    {
        this.sources = null;
    }
}