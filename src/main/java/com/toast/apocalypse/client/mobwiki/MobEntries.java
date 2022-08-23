package com.toast.apocalypse.client.mobwiki;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.MobWikiIndexes;
import com.toast.apocalypse.common.util.References;

import java.util.Map;
import java.util.TreeMap;

public final class MobEntries {

    public static Map<Integer, MobEntry> ENTRIES = new TreeMap<>();


    public static final MobEntry EMPTY = new MobEntry.Builder()
            .build();

    public static final MobEntry GHOST = new MobEntry.Builder()
            .mobName(ApocalypseEntities.GHOST.getId())
            .mobDescription(References.GHOST_WIKI_DESC)
            .mobTexture(Apocalypse.resourceLoc("textures/mobwiki/mob/ghost_entry.png"))
            .mobType(MobEntry.MobType.FULL_MOON)
            .build();

    public static final MobEntry BREECHER = new MobEntry.Builder()
            .mobName(ApocalypseEntities.BREECHER.getId())
            .mobDescription(References.GHOST_WIKI_DESC)
            .mobTexture(Apocalypse.resourceLoc("textures/mobwiki/mob/breecher_entry.png"))
            .mobType(MobEntry.MobType.FULL_MOON)
            .build();

    public static final MobEntry GRUMP = new MobEntry.Builder()
            .mobName(ApocalypseEntities.GRUMP.getId())
            .mobDescription(References.GHOST_WIKI_DESC)
            .mobTexture(Apocalypse.resourceLoc("textures/mobwiki/mob/grump_entry.png"))
            .mobType(MobEntry.MobType.FULL_MOON)
            .build();

    public static final MobEntry SEEKER = new MobEntry.Builder()
            .mobName(ApocalypseEntities.SEEKER.getId())
            .mobDescription(References.GHOST_WIKI_DESC)
            .mobTexture(Apocalypse.resourceLoc("textures/mobwiki/mob/seeker_entry.png"))
            .mobType(MobEntry.MobType.FULL_MOON)
            .build();

    public static final MobEntry DESTROYER = new MobEntry.Builder()
            .mobName(ApocalypseEntities.DESTROYER.getId())
            .mobDescription(References.GHOST_WIKI_DESC)
            .mobTexture(Apocalypse.resourceLoc("textures/mobwiki/mob/destroyer_entry.png"))
            .mobType(MobEntry.MobType.FULL_MOON)
            .build();


    public static void init() {
        ENTRIES.put(MobWikiIndexes.GHOST_INDEX, GHOST);
        ENTRIES.put(MobWikiIndexes.BREECHER_INDEX, BREECHER);
        ENTRIES.put(MobWikiIndexes.GRUMP_INDEX, GRUMP);
        ENTRIES.put(MobWikiIndexes.SEEKER_INDEX, SEEKER);
        ENTRIES.put(MobWikiIndexes.DESTROYER_INDEX, DESTROYER);
    }

    private MobEntries() {}
}
