package org.inksnow.core.impl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TestPosUtil {
    @Test
    public void test_chunkX_regionX() {
        Assertions.assertEquals(0, PosUtil.chunkX_regionX(0));
        Assertions.assertEquals(1, PosUtil.chunkX_regionX(32));
        Assertions.assertEquals(-1, PosUtil.chunkX_regionX(-32));
        Assertions.assertEquals(0, PosUtil.chunkX_regionX(31));
        Assertions.assertEquals(-1, PosUtil.chunkX_regionX(-31));
        Assertions.assertEquals(1, PosUtil.chunkX_regionX(33));
        Assertions.assertEquals(-2, PosUtil.chunkX_regionX(-33));
    }

    @Test
    public void test_chunkZ_regionZ() {
        Assertions.assertEquals(0, PosUtil.chunkZ_regionZ(0));
        Assertions.assertEquals(1, PosUtil.chunkZ_regionZ(32));
        Assertions.assertEquals(-1, PosUtil.chunkZ_regionZ(-32));
        Assertions.assertEquals(0, PosUtil.chunkZ_regionZ(31));
        Assertions.assertEquals(-1, PosUtil.chunkZ_regionZ(-31));
        Assertions.assertEquals(1, PosUtil.chunkZ_regionZ(33));
        Assertions.assertEquals(-2, PosUtil.chunkZ_regionZ(-33));
    }

    @Test
    public void test_regionXZ_regionId() {
        Assertions.assertEquals(0, PosUtil.regionXZ_regionId(0, 0));
        Assertions.assertEquals(1, PosUtil.regionXZ_regionId(1, 0));
        Assertions.assertEquals(0x100000000L, PosUtil.regionXZ_regionId(0, 1));
        Assertions.assertEquals(0x100000001L, PosUtil.regionXZ_regionId(1, 1));
    }

    @Test
    public void test_chunkX_regionLocalX() {
        Assertions.assertEquals(0, PosUtil.chunkX_regionLocalX(0));
        Assertions.assertEquals(31, PosUtil.chunkX_regionLocalX(31));
        Assertions.assertEquals(0, PosUtil.chunkX_regionLocalX(32));
        Assertions.assertEquals(1, PosUtil.chunkX_regionLocalX(33));
        Assertions.assertEquals(31, PosUtil.chunkX_regionLocalX(-1));
        Assertions.assertEquals(0, PosUtil.chunkX_regionLocalX(-32));
        Assertions.assertEquals(31, PosUtil.chunkX_regionLocalX(-33));
    }

    @Test
    public void test_chunkZ_regionLocalZ() {
        Assertions.assertEquals(0, PosUtil.chunkZ_regionLocalZ(0));
        Assertions.assertEquals(31, PosUtil.chunkZ_regionLocalZ(31));
        Assertions.assertEquals(0, PosUtil.chunkZ_regionLocalZ(32));
        Assertions.assertEquals(1, PosUtil.chunkZ_regionLocalZ(33));
        Assertions.assertEquals(31, PosUtil.chunkZ_regionLocalZ(-1));
        Assertions.assertEquals(0, PosUtil.chunkZ_regionLocalZ(-32));
        Assertions.assertEquals(31, PosUtil.chunkZ_regionLocalZ(-33));
    }

    @Test
    public void test_chunkXZ_regionLocalId() {
        Assertions.assertEquals(0, PosUtil.chunkXZ_regionId(0, 0));
        Assertions.assertEquals(1, PosUtil.chunkXZ_regionId(32, 0));
        Assertions.assertEquals(0x100000000L, PosUtil.chunkXZ_regionId(0, 32));
        Assertions.assertEquals(0x100000001L, PosUtil.chunkXZ_regionId(32, 32));

        Assertions.assertEquals(-1, PosUtil.chunkXZ_regionId(-32, -32));
        Assertions.assertEquals(-4294967296L, PosUtil.chunkXZ_regionId(0, -32));
        Assertions.assertEquals(4294967295L, PosUtil.chunkXZ_regionId(-32, 0));
    }

    @Test
    public void test_regionId_regionX() {
        Assertions.assertEquals(0, PosUtil.regionId_regionX(0));
        Assertions.assertEquals(1, PosUtil.regionId_regionX(1));
        Assertions.assertEquals(0, PosUtil.regionId_regionX(0x100000000L));
        Assertions.assertEquals(1, PosUtil.regionId_regionX(0x100000001L));

        Assertions.assertEquals(0, PosUtil.regionId_regionX(-4294967296L));
        Assertions.assertEquals(-1, PosUtil.regionId_regionX(-1));
        Assertions.assertEquals(-1, PosUtil.regionId_regionX(4294967295L));
    }

    @Test
    public void test_regionId_regionZ() {
        Assertions.assertEquals(0, PosUtil.regionId_regionZ(0));
        Assertions.assertEquals(0, PosUtil.regionId_regionZ(1));
        Assertions.assertEquals(1, PosUtil.regionId_regionZ(0x100000000L));
        Assertions.assertEquals(1, PosUtil.regionId_regionZ(0x100000001L));

        Assertions.assertEquals(-1, PosUtil.regionId_regionZ(-4294967296L));
        Assertions.assertEquals(-1, PosUtil.regionId_regionZ(-1));
        Assertions.assertEquals(0, PosUtil.regionId_regionZ(4294967295L));
    }
}
