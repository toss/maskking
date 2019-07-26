package im.toss.maskking.concrete

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import im.toss.maskking.MaskedString


class MaskedFormattedString(
        private val format: String,
        vararg args: Any?
): MaskedString {

    private val maskedArgs = args

    private val masked by lazy {
        String.format(format, *maskedArgs)
    }

    private val unmasked by lazy {
        val unmaskedArgs = maskedArgs.map {
            when(it) {
                is MaskedString -> it.unmasked()
                else -> it
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
