package org.inksnow.core.impl.data;

import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.impl.data.persistence.DataSerializers;
import org.inksnow.core.impl.data.persistence.NBTTranslator;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.nbt.CompoundTag;

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
        this.mappings.put(CompoundTag.class, NBTTranslator.INSTANCE);
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
