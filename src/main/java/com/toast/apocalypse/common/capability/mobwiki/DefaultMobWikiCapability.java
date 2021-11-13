package com.toast.apocalypse.common.capability.mobwiki;

public class DefaultMobWikiCapability implements IMobWikiCapability {

    private int[] entries = new int[] {};

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
}
