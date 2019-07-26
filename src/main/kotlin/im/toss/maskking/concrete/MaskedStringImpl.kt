package im.toss.maskking.concrete

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import im.toss.maskking.MaskedString
import im.toss.maskking.MaskingPattern

class MaskedStringImpl(
        private val value: String,
        private val pattern: MaskingPattern = MaskingPattern.ALL
): MaskedString {
    private val masked by lazy {
        when (pattern) {
            MaskingPattern.NONE -> value
            MaskingPattern.ALL -> "*".repeat(value.length)
            MaskingPattern.MIDDLE_HALF -> {
                val firstSize = when(value.length) {
                    0 -> 0
                    1 -> 0
                    2 -> 1
                    else -> Math.max(value.length / 4, 1)
                }
                val lastSize = when(value.length) {
                    0 -> 0
                    1 -> 0
                    2 -> 0
                    else -> firstSize
                }
                val first = value.substring(0, firstSize)
                val last = value.substring(value.length - lastSize)
                val mid = "*".repeat(value.length - firstSize - lastSize)

                first + mid + last
            }
            MaskingPattern.LAST_HALF -> {
                val firstSize = value.length / 2
                val first = value.substring(0, firstSize)
                val last = "*".repeat(value.length - firstSize)

                first + last
            }
        }
    }

    @JsonValue
    override fun toString(): String {
        return masked
    }

    override fun unmasked() = value

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is MaskedStringImpl -> value == other.value
            else -> return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override val length: Int
        @JsonIgnore
        get() = masked.length

    override fun get(index: Int): Char {
        return masked[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return masked.subSequence(startIndex, endIndex)
    }
}
