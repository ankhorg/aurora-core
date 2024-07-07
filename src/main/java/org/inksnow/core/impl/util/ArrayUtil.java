package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ArrayUtil {
    public static byte[] byteListToArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Byte obyte = list.get(i);

            bytes[i] = (obyte == null) ? 0 : obyte;
        }

        return bytes;
    }

    public static int[] intListToArray(List<Integer> list) {
        int[] ints = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Integer oint = list.get(i);

            ints[i] = (oint == null) ? 0 : oint;
        }

        return ints;
    }

    public static long[] longListToArray(List<Long> list) {
        long[] longs = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long olong = list.get(i);

            longs[i] = (olong == null) ? 0 : olong;
        }

        return longs;
    }
}
