package org;


import java.util.ArrayList;

public class ModeManagerOLD<T> {
    public int mode;
    public int numberOfModes;
    public ArrayList<String> modeNames;

    public ArrayList<T> modeItems;

    public ModeManagerOLD() {
        mode = 0;
        numberOfModes = 0;
        modeNames = new ArrayList<>();
        modeItems = new ArrayList<>();
    }

    public void add(String modeName, T modeFrame) {
        modeNames.add(modeName);
        modeItems.add(modeFrame);
        numberOfModes += 1;
    }

    public void modeSwitchUp() {
        mode = (mode + 1) % numberOfModes;
    }

    public void modeSwitchDown() {
        mode = (mode - 1) % numberOfModes;
    }

    public boolean modeSet(int newMode) {
        if (newMode < 0 || numberOfModes <= newMode) return false;
        mode = newMode;
        return true;
    }
}
