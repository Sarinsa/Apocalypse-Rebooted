package com.toast.apocalypse.common.capability.mobwiki;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class MobWikiCapability implements IMobWikiCapability {

    private int[] entries = new int[]{};

    @Override
    public void addEntry(int entry) {
        for (int i : this.entries) {
            if (i == entry) {
                return;
            }
        }
        int oldSize = this.entries.length;
        int[] newContent = new int[oldSize + 1];
        System.arraycopy(entries, 0, newContent, 0, oldSize);
        newContent[oldSize] = entry;
        this.entries = newContent;
    }

    @Override
    public void setEntries(int[] entries) {
        this.entries = entries;
    }

    @Override
    public int[] getEntries() {
        return this.entries;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putIntArray("UnlockedEntries", entries);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("UnlockedEntries", Tag.TAG_INT_ARRAY))
            entries = nbt.getIntArray("UnlockedEntries");
    }
}
