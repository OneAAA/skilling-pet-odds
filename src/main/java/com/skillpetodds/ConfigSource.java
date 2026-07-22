package com.skillpetodds;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

// Pet Roll Source Data Structure
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfigSource
{
    @EqualsAndHashCode.Include
    String source;
    List<Integer> levels;
    List<Integer> counts;

    // init with empty arrays for ints
    ConfigSource(String inSource)
    {
        this.source = inSource;
        this.levels = new ArrayList<Integer>(0);
        this.counts = new ArrayList<Integer>(0);
    }

    // add a roll of this source with particular level
    // succeed 0 indicates worked and incremented existing counts
    // succeed 1 indicates worked and added new level and count
    // error -1 indicates mismatched levels and counts arrays
    // error -2 indicates negative insert pos is outside levels range
    int addRoll(int level)
    {
        // Returns index of counts to increment, otherwise (-insertion point)-1
        if (this.levels == null)
        {
            this.levels = new ArrayList<Integer>(0);
        }
        if (this.counts == null)
        {
            this.counts = new ArrayList<Integer>(0);
        }
        int insertPos = Collections.binarySearch(this.levels, level);
        if (insertPos >= 0) // i.e. in levels list
        {
            if (insertPos > this.counts.size()-1)
            {
                // mismatched array lengths
                log.debug("Mismatched lengths; insertPos " + insertPos + " exceeds counts size-1 " + (this.counts.size()-1));
                return -1;
            }
            int newCount = this.counts.get(insertPos) + 1; // new count
            this.counts.set(insertPos, newCount);
            return 0;
        }
        else // i.e. not in levels list
        {
            insertPos = -(insertPos) - 1; // position at which to insert new element in both lists
            if (insertPos > this.levels.size()-1)
            {
                log.debug("Mismatched lengths; insertPos " + insertPos + " exceeds levels size-1 " + (this.levels.size()-1));
                return -2;
            }
            if (insertPos > this.counts.size()-1)
            {
                // mismatched with negative
                log.debug("Mismatched lengths; insertPos " + insertPos + " exceeds counts size-1 " + (this.counts.size()-1));
                return -1;
            }
            this.levels.add(insertPos, level);
            this.counts.add(insertPos, 1);
            return 1;
        }
    }

    void clearRolls()
    {
        this.levels = null; // trigger garbage collection
        this.counts = null;
    }
}