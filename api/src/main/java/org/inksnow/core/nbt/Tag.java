package org.inksnow.core.nbt;

/**
 * The base interface of all tags
 */
public interface Tag {
    byte TAG_END = 0;
    byte TAG_BYTE = 1;
    byte TAG_SHORT = 2;
    byte TAG_INT = 3;
    byte TAG_LONG = 4;
    byte TAG_FLOAT = 5;
    byte TAG_DOUBLE = 6;
    byte TAG_BYTE_ARRAY = 7;
    byte TAG_STRING = 8;
    byte TAG_LIST = 9;
    byte TAG_COMPOUND = 10;
    byte TAG_INT_ARRAY = 11;
    byte TAG_LONG_ARRAY = 12;
    byte TAG_ANY_NUMERIC = 99;

    /**
     * Get the type id of the tag
     *
     * @return the type id
     */
    byte getId();

    /**
     * Get the tag content as a string
     *
     * @return the string representation of the tag
     */
    String getAsString();

    /**
     * Copy the tag
     *
     * @return the copied tag
     */
    Tag copy();

    /**
     * Get the tag factory, same as {@link TagFactory#instance()}
     *
     * @return the tag factory
     */
    static TagFactory factory() {
        return TagFactory.instance();
    }
}
