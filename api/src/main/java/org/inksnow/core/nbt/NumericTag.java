package org.inksnow.core.nbt;

public interface NumericTag extends Tag {
    long getAsLong();

    int getAsInt();

    short getAsShort();

    byte getAsByte();

    double getAsDouble();

    float getAsFloat();

    Number getAsNumber();
}
