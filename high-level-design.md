# High-Level Design of SkillPetOdds plugin

## Purpose

Document the design decisions for the core architecture of the plugin, for personal reference.

## Requirements

Plugin needs to reliably detect pet roll actions, update chance of pet, and present this in overlay and side panel.
Needs to be configurable and scalable to allow for new pets to be added. Needs to store the number of rolls from each
source at each level, which will be done in JSON to integrate with RuneLite's ConfigManager easily. Will use ConfigManager
to store data, no direct local file management expected.

## Plan

Start with detecting beaver from oaks and yews (what I am likely to want to track) and expand from there. Configurability
and side panel to be added later once core detection and tracking is done for one pet.

## Exact Database design

JSON is stored by ConfigManager.setConfiguration(SkillPetOddsConfig.GROUP, profile, "pets_"+pet_name,json). This is
written to on every pet roll, unless experience shows that this is computationally restrictive. Options for if this is
the case are to split this JSON into multiple files per pet source to reduce the number of writes, or enable a config
setting for only storing chance and rate.

Example JSON is shown below for illustration. Numbers are int for level and count, and double for chance and rate.
```
{
    "pet": "beaver",
    "chance": 0.50,
    "rate": 0.66,
    "sources": [
        {
            "source": "oak",
            "rolls": [
                {
                    "level": 50,
                    "count": 100,
                },
            ]
        },
    ]
}       
```

## Basic Functional Flow

The basic idea is to separate the pet roll detection functions (which are bound to be a bit messy due to weird quirks of
the pet rolls) from the chance recalc and storage.

onGameMessage/other raw detections do prelim checks then call a pattern matcher to determine the exact pet roll.
Then this calls a synchronized onPetRolled(pet, source, level) which stores the roll and updates the stack store of pet
roll. The overlay renderer accesses this store.

This will get more complex as I further develop this plugin. I upfronted the design work because it is easier for me
than programming (I am smart but wanted to do work from my phone).

## Advanced Features

Things like session stats, untracked odds manual entry, untracked odds estimation (including accounting for mobile
sessions with similar stats), csv/json import and export, and multiple session stats are all hopeful features. Order
of implementation to be decided by my whims, unless the plugin has a large enough user base (>10) to warrant voting on
feature order. All/most of these would be after implementing all skilling pets (tbd by feasibility).

## Justification for Selection of Double for Odds Tracking

The key requirement of the odds tracking that I decided was about 0.01% error in probability of not getting pet at 10x
dry. This is confirmed achievable with 49 bits fixed point, so a double precision (64-bit) floating point was
deemed sufficient for my purposes as this has 48 bits of value (and 16 bits of magnitude). 