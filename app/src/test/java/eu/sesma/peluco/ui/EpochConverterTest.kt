package eu.sesma.peluco.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class EpochConverterTest {

    internal var converter = EpochConverter()

    @Test
    fun convertTest() {
        converter.convert(1525110385)

        val result = converter.tm.toString()

        assertEquals("Tm{tm_sec=25, tm_min=46, tm_hour=17, tm_mday=30, tm_mon=4, tm_year=2018, tm_wday=1, tm_yday=120, tm_isdst=1}", result)
    }
}
