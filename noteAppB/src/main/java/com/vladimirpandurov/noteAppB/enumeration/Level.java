package com.vladimirpandurov.noteAppB.enumeration;

public enum Level {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int level;

    Level(int level) {this.level = level;}

    public int getLevel() { return this.level;}
}
