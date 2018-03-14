package eu.sesma.peluco.bt

import android.util.Log

class RingBuffer<T>(capacity: Int) {

    companion object {
        private val TAG = RingBuffer::class.java.simpleName
    }

    private val buffer: Array<T?> = arrayOfNulls<Any>(capacity) as Array<T?>  // queue elements
    private var count = 0          // number of elements on queue
    private var indexOut = 0       // index of first element of queue
    private var indexIn = 0       // index of next available slot

    val isEmpty: Boolean
        get() = count == 0

    val isFull: Boolean
        get() = count == buffer.size

    fun size() = count

    fun clear() {
        count = 0
    }

    fun push(item: T) {
        if (count == buffer.size) {
            Log.e(TAG, "Ring buffer overflow")
            return
        }
        buffer[indexIn] = item
        indexIn = (indexIn + 1) % buffer.size // wrap-around
        if (count++ == buffer.size) {
            count = buffer.size
        }
    }

    fun pop(): T? {
        if (isEmpty) {
            Log.e(TAG, "Ring buffer pop underflow")
            return null
        }
        val item = buffer[indexOut]
        buffer[indexOut] = null // to help with garbage collection
        if (count-- == 0) {
            count = 0
        }
        indexOut = (indexOut + 1) % buffer.size // wrap-around
        return item
    }

    operator fun next(): T? {
        if (isEmpty) {
            Log.e(TAG, "Ring buffer next underflow")
            return null
        }
        return buffer[indexOut]
    }
}