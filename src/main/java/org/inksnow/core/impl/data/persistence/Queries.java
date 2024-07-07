package org.inksnow.core.impl.data.persistence;

import org.inksnow.core.data.persistence.DataQuery;

import static org.inksnow.core.data.persistence.DataQuery.of;


@SuppressWarnings("BadImport")
public final class Queries {

    // Content version
    public static final DataQuery CONTENT_VERSION = of("ContentVersion");

    // Transaction
    public static final DataQuery TYPE_CLASS = of("TypeClass");
    public static final DataQuery ORIGINAL = of("Original");
    public static final DataQuery DEFAULT_REPLACEMENT = of("DefaultReplacement");
    public static final DataQuery CUSTOM_REPLACEMENT = of("CustomReplacement");
    public static final DataQuery FINAL_REPLACEMENT = of("FinalReplacement");
    public static final DataQuery VALID = of("IsValid");
    public static final DataQuery BLOCK_OPERATION = of("Operation");

    // WeightedSerializableObject
    public static final DataQuery WEIGHTED_SERIALIZABLE = of("DataSerializable");
    public static final DataQuery WEIGHTED_SERIALIZABLE_WEIGHT = of("Weight");

    // Item Enchantment
    public static final DataQuery ENCHANTMENT_ID = of("Enchantment");
    public static final DataQuery LEVEL = of("Level");

    // WeightedItem
    public static final DataQuery WEIGHTED_ITEM_QUANTITY = of("Quantity");

    // Location
    public static final DataQuery WORLD_KEY = of("WorldKey");
    public static final DataQuery POSITION_X = of("X");
    public static final DataQuery POSITION_Y = of("Y");
    public static final DataQuery POSITION_Z = of("Z");

    // Variable
    public static final DataQuery VARIABLE_CHANCE = of("Chance");
    public static final DataQuery VARIABLE_BASE = of("Base");
    public static final DataQuery VARIABLE_VARIANCE = of("Variance");
    public static final DataQuery VARIABLE_AMOUNT = of("Amount");

    // Color
    public static final DataQuery COLOR_RED = of("Red");
    public static final DataQuery COLOR_BLUE = of("Blue");
    public static final DataQuery COLOR_GREEN = of("Green");

    // Tracking
    public static final DataQuery CREATOR_ID = of("Creator");
    public static final DataQuery NOTIFIER_ID = of("Notifier");

    // Text
    public static final DataQuery JSON = of("JSON");
    public static final DataQuery TEXT_TITLE = of("TextTitle");
    public static final DataQuery TEXT_AUTHOR = of("TextAuthor");
    public static final DataQuery TEXT_PAGE_LIST = of("TextPageList");

    // RespawnLocation
    public static final DataQuery FORCED_SPAWN = of("ForcedSpawn");

    // UUID
    public static final DataQuery UUID_LEAST = of("UuidLeast");
    public static final DataQuery UUID_MOST = of("UuidMost");
    public static final DataQuery POSITION = of("Pos");

    // ProfileProperty
    public static final DataQuery PROPERTY_NAME = of("name");
    public static final DataQuery PROPERTY_VALUE = of("value");
    public static final DataQuery PROPERTY_SIGNATURE = of("signature");

    // Suppress default constructor to ensure non-instantiability.
    private Queries() {
        throw new AssertionError("You should not be attempting to instantiate this class.");
    }

}
