package org.inksnow.core.impl.data.persistence;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.exception.InvalidDataException;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.impl.util.Constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.function.Supplier;

public final class DataSerializers {

    public static final DataTranslator<UUID> UUID_DATA_SERIALIZER;
    public static final DataTranslator<LocalTime> LOCAL_TIME_DATA_SERIALIZER;
    public static final DataTranslator<LocalDate> LOCAL_DATE_DATA_SERIALIZER;
    public static final DataTranslator<LocalDateTime> LOCAL_DATE_TIME_DATA_SERIALIZER;
    public static final DataTranslator<Instant> INSTANT_DATA_SERIALIZER;
    public static final DataTranslator<ZonedDateTime> ZONED_DATE_TIME_DATA_SERIALIZER;
    public static final DataTranslator<Month> MONTH_DATA_SERIALIZER;


    static {
        UUID_DATA_SERIALIZER = new DataTranslator<UUID>() {

            final TypeToken<UUID> token = TypeToken.get(UUID.class);

            @Override
            public TypeToken<UUID> token() {
                return this.token;
            }

            @Override
            public UUID translate(DataView view) throws InvalidDataException {
                // fix for API7 notifier/creator uuids
                if (view.contains(Constants.UUID_LEAST_QUERY) && view.contains(Constants.UUID_MOST_QUERY)) {
                    final long most = view.getLong(Constants.UUID_MOST_QUERY).get();
                    final long least = view.getLong(Constants.UUID_LEAST_QUERY).get();
                    return new UUID(most, least);
                }

                final long most = view.getLong(Queries.UUID_MOST).orElseThrow(DataSerializers.invalidDataQuery(Queries.UUID_MOST));
                final long least = view.getLong(Queries.UUID_LEAST).orElseThrow(DataSerializers.invalidDataQuery(Queries.UUID_LEAST));
                return new UUID(most, least);
            }

            @Override
            public DataContainer translate(UUID obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Queries.UUID_MOST, obj.getMostSignificantBits())
                        .set(Queries.UUID_LEAST, obj.getLeastSignificantBits());
            }

            @Override
            public DataView addTo(UUID obj, DataView dataView) {
                return dataView
                    .set(Queries.UUID_LEAST, obj.getLeastSignificantBits())
                    .set(Queries.UUID_MOST, obj.getMostSignificantBits());
            }
        };
        LOCAL_TIME_DATA_SERIALIZER = new DataTranslator<LocalTime>() {
            final TypeToken<LocalTime> token = TypeToken.get(LocalTime.class);

            @Override
            public TypeToken<LocalTime> token() {
                return this.token;
            }

            @Override
            public LocalTime translate(DataView view) throws InvalidDataException {
                final int hour = view.getInt(Constants.DataSerializers.LOCAL_TIME_HOUR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_HOUR));
                final int minute = view.getInt(Constants.DataSerializers.LOCAL_TIME_MINUTE).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_MINUTE));
                final int second = view.getInt(Constants.DataSerializers.LOCAL_TIME_SECOND).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_SECOND));
                final int nano = view.getInt(Constants.DataSerializers.LOCAL_TIME_NANO).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_NANO));
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalTime.of(hour, minute, second, nano);
            }

            @Override
            public DataContainer translate(LocalTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                        .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano());
            }

            @Override
            public DataView addTo(LocalTime obj, DataView dataView) {
                return dataView.set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                    .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                    .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                    .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano());
            }
        };
        LOCAL_DATE_DATA_SERIALIZER = new DataTranslator<LocalDate>() {
            final TypeToken<LocalDate> token = TypeToken.get(LocalDate.class);

            @Override
            public TypeToken<LocalDate> token() {
                return this.token;
            }

            @Override
            public LocalDate translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(Constants.DataSerializers.LOCAL_DATE_YEAR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_YEAR));
                final int month = view.getInt(Constants.DataSerializers.LOCAL_DATE_MONTH).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_MONTH));
                final int day = view.getInt(Constants.DataSerializers.LOCAL_DATE_DAY).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_DAY));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                return LocalDate.of(year, month, day);
            }

            @Override
            public DataContainer translate(LocalDate obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                        .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                        .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth());
            }

            @Override
            public DataView addTo(LocalDate obj, DataView dataView) {
                return dataView.set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                    .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                    .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth());
            }
        };
        LOCAL_DATE_TIME_DATA_SERIALIZER = new DataTranslator<LocalDateTime>() {
            final TypeToken<LocalDateTime> token = TypeToken.get(LocalDateTime.class);

            @Override
            public TypeToken<LocalDateTime> token() {
                return this.token;
            }

            @Override
            public LocalDateTime translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(Constants.DataSerializers.LOCAL_DATE_YEAR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_YEAR));
                final int month = view.getInt(Constants.DataSerializers.LOCAL_DATE_MONTH).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_MONTH));
                final int day = view.getInt(Constants.DataSerializers.LOCAL_DATE_DAY).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_DAY));
                final int hour = view.getInt(Constants.DataSerializers.LOCAL_TIME_HOUR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_HOUR));
                final int minute = view.getInt(Constants.DataSerializers.LOCAL_TIME_MINUTE).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_MINUTE));
                final int second = view.getInt(Constants.DataSerializers.LOCAL_TIME_SECOND).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_SECOND));
                final int nano = view.getInt(Constants.DataSerializers.LOCAL_TIME_NANO).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_NANO));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalDateTime.of(year, month, day, hour, minute, second, nano);
            }

            @Override
            public DataContainer translate(LocalDateTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                        .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                        .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth())
                        .set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                        .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano());
            }

            @Override
            public DataView addTo(LocalDateTime obj, DataView dataView) {
                return dataView.set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                    .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                    .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth())
                    .set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                    .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                    .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                    .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano());
            }
        };
        ZONED_DATE_TIME_DATA_SERIALIZER = new DataTranslator<ZonedDateTime>() {
            final TypeToken<ZonedDateTime> token = TypeToken.get(ZonedDateTime.class);

            @Override
            public TypeToken<ZonedDateTime> token() {
                return this.token;
            }

            @Override
            public ZonedDateTime translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(Constants.DataSerializers.LOCAL_DATE_YEAR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_YEAR));
                final int month = view.getInt(Constants.DataSerializers.LOCAL_DATE_MONTH).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_MONTH));
                final int day = view.getInt(Constants.DataSerializers.LOCAL_DATE_DAY).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_DAY));
                final int hour = view.getInt(Constants.DataSerializers.LOCAL_TIME_HOUR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_HOUR));
                final int minute = view.getInt(Constants.DataSerializers.LOCAL_TIME_MINUTE).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_MINUTE));
                final int second = view.getInt(Constants.DataSerializers.LOCAL_TIME_SECOND).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_SECOND));
                final int nano = view.getInt(Constants.DataSerializers.LOCAL_TIME_NANO).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_NANO));
                final String zoneId = view.getString(Constants.DataSerializers.ZONE_TIME_ID).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.ZONE_TIME_ID));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                if (!ZoneId.getAvailableZoneIds().contains(zoneId)) {
                    throw new InvalidDataException("Unrecognized ZoneId: " + zoneId);
                }
                return ZonedDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute, second, nano), ZoneId.of(zoneId));
            }

            @Override
            public DataContainer translate(ZonedDateTime obj) throws InvalidDataException {
                return DataContainer.createNew()
                        .set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                        .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                        .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth())
                        .set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                        .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                        .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                        .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano())
                        .set(Constants.DataSerializers.ZONE_TIME_ID, obj.getZone().getId());
            }

            @Override
            public DataView addTo(ZonedDateTime obj, DataView dataView) {
                return dataView.set(Constants.DataSerializers.LOCAL_DATE_YEAR, obj.getYear())
                    .set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getMonthValue())
                    .set(Constants.DataSerializers.LOCAL_DATE_DAY, obj.getDayOfMonth())
                    .set(Constants.DataSerializers.LOCAL_TIME_HOUR, obj.getHour())
                    .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, obj.getMinute())
                    .set(Constants.DataSerializers.LOCAL_TIME_SECOND, obj.getSecond())
                    .set(Constants.DataSerializers.LOCAL_TIME_NANO, obj.getNano())
                    .set(Constants.DataSerializers.ZONE_TIME_ID, obj.getZone().getId());
            }
        };
        INSTANT_DATA_SERIALIZER = new DataTranslator<Instant>() {
            final TypeToken<Instant> token = TypeToken.get(Instant.class);

            @Override
            public TypeToken<Instant> token() {
                return this.token;
            }

            @Override
            public Instant translate(DataView view) throws InvalidDataException {
                final int year = view.getInt(Constants.DataSerializers.LOCAL_DATE_YEAR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_YEAR));
                final int month = view.getInt(Constants.DataSerializers.LOCAL_DATE_MONTH).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_MONTH));
                final int day = view.getInt(Constants.DataSerializers.LOCAL_DATE_DAY).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_DAY));
                final int hour = view.getInt(Constants.DataSerializers.LOCAL_TIME_HOUR).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_HOUR));
                final int minute = view.getInt(Constants.DataSerializers.LOCAL_TIME_MINUTE).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_MINUTE));
                final int second = view.getInt(Constants.DataSerializers.LOCAL_TIME_SECOND).orElseThrow(DataSerializers.invalidDataQuery(
                    Constants.DataSerializers.LOCAL_TIME_SECOND));
                final int nano = view.getInt(Constants.DataSerializers.LOCAL_TIME_NANO).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_TIME_NANO));
                if (!ChronoField.YEAR.range().isValidValue(year)) {
                    throw new InvalidDataException("Invalid year: " + year);
                }
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                if (!ChronoField.DAY_OF_MONTH.range().isValidValue(day)) {
                    throw new InvalidDataException("Invalid day of month: " + day);
                }
                if (!ChronoField.HOUR_OF_DAY.range().isValidValue(hour)) {
                    throw new InvalidDataException("Invalid hour of day: " + hour);
                }
                if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
                    throw new InvalidDataException("Invalid minute of hour: " + minute);
                }
                if (!ChronoField.SECOND_OF_MINUTE.range().isValidValue(second)) {
                    throw new InvalidDataException("Invalid second of minute: " + second);
                }
                if (!ChronoField.NANO_OF_SECOND.range().isValidValue(nano)) {
                    throw new InvalidDataException("Invalid nanosecond of second: " + nano);
                }
                return LocalDateTime.of(year, month, day, hour, minute, second, nano).toInstant(ZoneOffset.UTC);
            }

            @Override
            public DataContainer translate(Instant obj) throws InvalidDataException {
                final LocalDateTime local = obj.atZone(ZoneOffset.UTC).toLocalDateTime();
                return DataContainer.createNew()
                        .set(Constants.DataSerializers.LOCAL_DATE_YEAR, local.getYear())
                        .set(Constants.DataSerializers.LOCAL_DATE_MONTH, local.getMonthValue())
                        .set(Constants.DataSerializers.LOCAL_DATE_DAY, local.getDayOfMonth())
                        .set(Constants.DataSerializers.LOCAL_TIME_HOUR, local.getHour())
                        .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, local.getMinute())
                        .set(Constants.DataSerializers.LOCAL_TIME_SECOND, local.getSecond())
                        .set(Constants.DataSerializers.LOCAL_TIME_NANO, local.getNano());
            }

            @Override
            public DataView addTo(Instant obj, DataView dataView) {
                final LocalDateTime local = obj.atZone(ZoneOffset.UTC).toLocalDateTime();
                return dataView.set(Constants.DataSerializers.LOCAL_DATE_YEAR, local.getYear())
                    .set(Constants.DataSerializers.LOCAL_DATE_MONTH, local.getMonthValue())
                    .set(Constants.DataSerializers.LOCAL_DATE_DAY, local.getDayOfMonth())
                    .set(Constants.DataSerializers.LOCAL_TIME_HOUR, local.getHour())
                    .set(Constants.DataSerializers.LOCAL_TIME_MINUTE, local.getMinute())
                    .set(Constants.DataSerializers.LOCAL_TIME_SECOND, local.getSecond())
                    .set(Constants.DataSerializers.LOCAL_TIME_NANO, local.getNano());
            }
        };
        MONTH_DATA_SERIALIZER = new DataTranslator<Month>() {
            final TypeToken<Month> token = TypeToken.get(Month.class);

            @Override
            public TypeToken<Month> token() {
                return this.token;
            }

            @Override
            public Month translate(DataView view) throws InvalidDataException {
                final int month = view.getInt(Constants.DataSerializers.LOCAL_DATE_MONTH).orElseThrow(DataSerializers.invalidDataQuery(Constants.DataSerializers.LOCAL_DATE_MONTH));
                if (!ChronoField.MONTH_OF_YEAR.range().isValidValue(month)) {
                    throw new InvalidDataException("Invalid month of year: " + month);
                }
                return Month.of(month);
            }

            @Override
            public DataContainer translate(Month obj) throws InvalidDataException {
                return DataContainer.createNew().set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getValue());
            }

            @Override
            public DataView addTo(Month obj, DataView dataView) {
                return dataView.set(Constants.DataSerializers.LOCAL_DATE_MONTH, obj.getValue());
            }
        };
    }

    static Supplier<InvalidDataException> invalidDataQuery(final DataQuery query) {
        return () -> {
            throw new InvalidDataException("Invalid data located at: " + query.toString());
        };
    }
}
