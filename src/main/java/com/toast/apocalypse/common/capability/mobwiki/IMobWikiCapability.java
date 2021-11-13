package com.toast.apocalypse.common.capability.mobwiki;

public interface IMobWikiCapability {

    void addEntry(int entry);

    void setEntries(int[] entries);

    int[] getEntries();
}
