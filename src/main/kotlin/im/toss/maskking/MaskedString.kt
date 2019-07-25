package im.toss.maskking

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

class MaskedStringImpl(
    private val value: String,
    private val pattern: MaskingPattern = MaskingPattern.ALL
): MaskedString {
    @JsonValue
    override fun toString(): String {
        return when (pattern) {
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

                return first + mid + last
            }
            MaskingPattern.LAST_HALF -> {
                val firstSize = value.length / 2
                val first = value.substring(0, firstSize)
                val last = "*".repeat(value.length - firstSize)

                return first + last
            }
        }
    }

    override fun unmasked() = value

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is String -> value == other
            is MaskedStringImpl -> value == other.value
            else -> return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override val length: Int
        @JsonIgnore
        get() = value.length

    override fun get(index: Int): Char {
        return value[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return value.subSequence(startIndex, endIndex)
    }
}

class MaskedFormattedString(
    private val format: String,
    vararg args: Any
): MaskedString {

    private val maskedArgs = args

    private val masked by lazy {
        String.format(format, *maskedArgs)
    }

    private val unmasked by lazy {
        val unmaskedArgs = maskedArgs.map {
            when(it) {
                is MaskedString -> it.unmasked()
                else -> it.toString()
            }
        }.toTypedArray()
        String.format(format, *unmaskedArgs)
    }

    @JsonValue
    override fun toString() = masked
    override fun unmasked() = unmasked

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is String -> unmasked.equals(other)
            is MaskedString -> unmasked.equals(other.unmasked())
            else -> unmasked.equals(other)
        }
    }

    override fun hashCode(): Int {
        return unmasked.hashCode()
    }

    override val length: Int
        @JsonIgnore
        get() = unmasked.length

    override fun get(index: Int): Char {
        return unmasked[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return unmasked.subSequence(startIndex, endIndex)
    }
}

interface MaskedString : CharSequence {
    fun unmasked(): String

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: String) = MaskedStringImpl(value)
        fun of(value: String, pattern: MaskingPattern) = MaskedStringImpl(value, pattern)
        fun format(format: String, vararg args: Any) = MaskedFormattedString(format, *args)
        fun none(value: String) = MaskedStringImpl(value, MaskingPattern.NONE)
    }
}

enum class MaskingPattern {
    /**
     * 전체 마스킹
     */
    ALL,

    /**
     * 가운데 부분 절반 이상 마스킹
     */
    MIDDLE_HALF,

    /**
     * 뒷부분 절반 이상 마스킹
     */
    LAST_HALF,

    /**
     * 마스킹 하지 않음
     */
    NONE
}
