package org.inksnow.core.impl.data;

import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.impl.data.persistence.DataSerializers;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.schematic.Schematic;
import org.spongepowered.common.data.persistence.DataSerializers;
import org.spongepowered.common.data.persistence.NBTTranslator;
import org.spongepowered.common.world.schematic.SchematicTranslator;
import org.spongepowered.math.imaginary.Complexd;
import org.spongepowered.math.imaginary.Complexf;
import org.spongepowered.math.imaginary.Quaterniond;
import org.spongepowered.math.imaginary.Quaternionf;
import org.spongepowered.math.vector.Vector2d;
import org.spongepowered.math.vector.Vector2f;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector2l;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.math.vector.Vector3l;
import org.spongepowered.math.vector.Vector4d;
import org.spongepowered.math.vector.Vector4f;
import org.spongepowered.math.vector.Vector4i;
import org.spongepowered.math.vector.Vector4l;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class DataTranslatorProvider {

    public static final DataTranslatorProvider INSTANCE = new DataTranslatorProvider();
    private final Map<Class, DataTranslator> mappings = new IdentityHashMap<>();

    DataTranslatorProvider() {
        this.mappings.put(UUID.class, DataSerializers.UUID_DATA_SERIALIZER);
        this.mappings.put(LocalTime.class, DataSerializers.LOCAL_TIME_DATA_SERIALIZER);
        this.mappings.put(LocalDate.class, DataSerializers.LOCAL_DATE_DATA_SERIALIZER);
        this.mappings.put(LocalDateTime.class, DataSerializers.LOCAL_DATE_TIME_DATA_SERIALIZER);
        this.mappings.put(Instant.class, DataSerializers.INSTANT_DATA_SERIALIZER);
        this.mappings.put(ZonedDateTime.class, DataSerializers.ZONED_DATE_TIME_DATA_SERIALIZER);
        this.mappings.put(Month.class, DataSerializers.MONTH_DATA_SERIALIZER);
        this.mappings.put(RefNbtTagCompound.class, NBTTranslator.INSTANCE);
    }


    @SuppressWarnings("unchecked")
    public <T> Optional<DataTranslator<T>> getSerializer(Class clazz) {
        final DataTranslator dataTranslator = this.mappings.get(clazz);
        return Optional.ofNullable((DataTranslator<T>) dataTranslator);
    }

    public <T> void register(final Class<T> objectClass, final DataTranslator<T> translator) {
        if (!this.mappings.containsKey(objectClass)) {
            this.mappings.put(objectClass, translator);
        } else {
            throw new IllegalArgumentException("DataTranslator already registered for " + objectClass);
        }
    }
}
