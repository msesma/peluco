package eu.sesma.peluco.ui

import org.junit.Test

class EpochConverterTest {


    internal var converter = EpochConverter()

    @Test
    fun convertTest() {
        converter.convert(1525110385)

        val result = converter.tm.toString()

        println(result)
    }


}
