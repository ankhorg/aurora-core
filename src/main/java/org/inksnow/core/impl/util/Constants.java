package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;
import org.inksnow.core.data.persistence.DataQuery;

import static org.inksnow.core.data.persistence.DataQuery.of;

@UtilityClass
public final class Constants {

    public static final String UUID = "UUID";
    public static final String UUID_MOST = "UUIDMost";
    public static final DataQuery UUID_MOST_QUERY = of(Constants.UUID_MOST);
    public static final String UUID_LEAST = "UUIDLeast";
    public static final DataQuery UUID_LEAST_QUERY = of(Constants.UUID_LEAST);

    public static final class DataSerializers {

        // Java API Queries for DataSerializers
        public static final DataQuery LOCAL_TIME_HOUR = of("LocalTimeHour");
        public static final DataQuery LOCAL_TIME_MINUTE = of("LocalTimeMinute");
        public static final DataQuery LOCAL_TIME_SECOND = of("LocalTimeSecond");
        public static final DataQuery LOCAL_TIME_NANO = of("LocalTimeNano");
        public static final DataQuery LOCAL_DATE_YEAR = of("LocalDateYear");
        public static final DataQuery LOCAL_DATE_MONTH = of("LocalDateMonth");
        public static final DataQuery LOCAL_DATE_DAY = of("LocalDateDay");
        public static final DataQuery ZONE_TIME_ID = of("ZoneDateTimeId");
        public static final DataQuery X_POS = of("x");
        public static final DataQuery Y_POS = of("y");
        public static final DataQuery Z_POS = of("z");
        public static final DataQuery W_POS = of("w");
        public static final DataQuery DIRECTION = of("Direction");
    }
}
