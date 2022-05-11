package ru.skillbranch.common.extension

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


public class EnumTest {

    public enum class TestEnumBitsIntWithOffset(
        public override val offset: Int,
        public val desc: String
    ) : EnumBits<Int> {
        F0(1.shl(0), ""),
        F1(1.shl(1), ""),
        F2(1.shl(2), ""),
        F3(1.shl(3), ""),
        F4(1.shl(4), ""),
        F5(1.shl(5), ""),
        F6(1.shl(6), ""),
        F7(1.shl(7), ""),
        F8(1.shl(8), ""),
        F9(1.shl(9), ""),
        F10(1.shl(10), ""),
        F11(1.shl(11), ""),
        F12(1.shl(12), ""),
        F13(1.shl(13), ""),
        F14(1.shl(14), ""),
        F15(1.shl(15), ""),
        F16(1.shl(16), ""),
        F17(1.shl(17), ""),
        F18(1.shl(18), ""),
        F19(1.shl(19), ""),
        F20(1.shl(20), ""),
        F21(1.shl(21), ""),
        F22(1.shl(22), ""),
        F23(1.shl(23), ""),
        F24(1.shl(24), ""),
        F25(1.shl(25), ""),
        F26(1.shl(26), ""),
        F27(1.shl(27), ""),
        F28(1.shl(28), ""),
        F29(1.shl(29), ""),
        F30(1.shl(30), ""),
        F31(1.shl(31), "");
    }

    public enum class TestEnumBitsLongWithOffset(
        public override val offset: Long,
        public val desc: String
    ) : EnumBits<Long> {
        F0(1L.shl(0), ""),
        F1(1L.shl(1), ""),
        F2(1L.shl(2), ""),
        F3(1L.shl(3), ""),
        F4(1L.shl(4), ""),
        F5(1L.shl(5), ""),
        F6(1L.shl(6), ""),
        F7(1L.shl(7), ""),
        F8(1L.shl(8), ""),
        F9(1L.shl(9), ""),
        F10(1L.shl(10), ""),
        F11(1L.shl(11), ""),
        F12(1L.shl(12), ""),
        F13(1L.shl(13), ""),
        F14(1L.shl(14), ""),
        F15(1L.shl(15), ""),
        F16(1L.shl(16), ""),
        F17(1L.shl(17), ""),
        F18(1L.shl(18), ""),
        F19(1L.shl(19), ""),
        F20(1L.shl(20), ""),
        F21(1L.shl(21), ""),
        F22(1L.shl(22), ""),
        F23(1L.shl(23), ""),
        F24(1L.shl(24), ""),
        F25(1L.shl(25), ""),
        F26(1L.shl(26), ""),
        F27(1L.shl(27), ""),
        F28(1L.shl(28), ""),
        F29(1L.shl(29), ""),
        F30(1L.shl(30), ""),
        F31(1L.shl(31), ""),
        F32(1L.shl(32), ""),
        F33(1L.shl(33), ""),
        F34(1L.shl(34), ""),
        F35(1L.shl(35), ""),
        F36(1L.shl(36), ""),
        F37(1L.shl(37), ""),
        F38(1L.shl(38), ""),
        F39(1L.shl(39), ""),
        F40(1L.shl(40), ""),
        F41(1L.shl(41), ""),
        F42(1L.shl(42), ""),
        F43(1L.shl(43), ""),
        F44(1L.shl(44), ""),
        F45(1L.shl(45), ""),
        F46(1L.shl(46), ""),
        F47(1L.shl(47), ""),
        F48(1L.shl(48), ""),
        F49(1L.shl(49), ""),
        F50(1L.shl(50), ""),
        F51(1L.shl(51), ""),
        F52(1L.shl(52), ""),
        F53(1L.shl(53), ""),
        F54(1L.shl(54), ""),
        F55(1L.shl(55), ""),
        F56(1L.shl(56), ""),
        F57(1L.shl(57), ""),
        F58(1L.shl(58), ""),
        F59(1L.shl(59), ""),
        F60(1L.shl(60), ""),
        F61(1L.shl(61), ""),
        F62(1L.shl(62), ""),
        F63(1L.shl(63), "");
    }


    @Before
    public fun start() {

    }

    @After
    public fun stop() {

    }

    @Test
    public fun enumBitsIntWithOffsetTest() {
        val originalSet = setOf(
            TestEnumBitsIntWithOffset.F0,
            TestEnumBitsIntWithOffset.F4,
            TestEnumBitsIntWithOffset.F3,
            TestEnumBitsIntWithOffset.F2,
            TestEnumBitsIntWithOffset.F31
        )
        val asInt = originalSet.encodeToBitsByOffset()
        val restoredSet = asInt.decodeFromBitsByOffset<TestEnumBitsIntWithOffset>()
        assertEquals(restoredSet, originalSet)
    }

    @Test
    public fun enumBitsIntWithOffsetTestAsOrdinal() {
        val originalSet = setOf(
            TestEnumBitsIntWithOffset.F0,
            TestEnumBitsIntWithOffset.F4,
            TestEnumBitsIntWithOffset.F3,
            TestEnumBitsIntWithOffset.F2,
            TestEnumBitsIntWithOffset.F31
        )
        val asInt = originalSet.encodeToBitsByOrdinal32()
        val restoredSet = asInt.decodeFromBitsByOrdinal<TestEnumBitsIntWithOffset>()
        assertEquals(restoredSet, originalSet)
    }

    @Test
    public fun enumBitsLongWithOffsetTest() {
        val originalSet = setOf(
            TestEnumBitsLongWithOffset.F0,
            TestEnumBitsLongWithOffset.F4,
            TestEnumBitsLongWithOffset.F3,
            TestEnumBitsLongWithOffset.F2,
            TestEnumBitsLongWithOffset.F31,
            TestEnumBitsLongWithOffset.F63,
            TestEnumBitsLongWithOffset.F15
        )
        val asLong = originalSet.encodeToBitsByOffset()
        val restoredSet = asLong.decodeFromBitsByOffset<TestEnumBitsLongWithOffset>()
        assertEquals(restoredSet, originalSet)
    }

    @Test
    public fun enumBitsLongWithOffsetTestAsOrdinal() {
        val originalSet = setOf(
            TestEnumBitsLongWithOffset.F0,
            TestEnumBitsLongWithOffset.F4,
            TestEnumBitsLongWithOffset.F3,
            TestEnumBitsLongWithOffset.F2,
            TestEnumBitsLongWithOffset.F31,
            TestEnumBitsLongWithOffset.F63,
            TestEnumBitsLongWithOffset.F15
        )
        val asLong = originalSet.encodeToBitsByOrdinal64()
        val restoredSet = asLong.decodeFromBitsByOrdinal<TestEnumBitsLongWithOffset>()
        assertEquals(restoredSet, originalSet)
    }

    @Test(expected = IllegalArgumentException::class)
    public fun enumBitsLongWithOffsetTestAsOrdinalError() {
        val originalSet = setOf(
            TestEnumBitsLongWithOffset.F0,
            TestEnumBitsLongWithOffset.F4,
            TestEnumBitsLongWithOffset.F3,
            TestEnumBitsLongWithOffset.F2,
            TestEnumBitsLongWithOffset.F31,
            TestEnumBitsLongWithOffset.F63,
            TestEnumBitsLongWithOffset.F15
        )
        val asLong = originalSet.encodeToBitsByOrdinal32()
        val restoredSet = asLong.decodeFromBitsByOrdinal<TestEnumBitsLongWithOffset>()
        assertEquals(restoredSet, originalSet)
    }
}
