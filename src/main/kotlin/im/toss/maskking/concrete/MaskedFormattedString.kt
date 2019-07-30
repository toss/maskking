package im.toss.maskking.concrete

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import im.toss.maskking.MaskedString

internal class MaskedFormattedString(
    private val args: List<Any?>,
    private val formatter: (args: List<Any?>) -> String
): MaskedString {

    private val maskedArgs = args

    private val masked by lazy {
        formatter.invoke(maskedArgs)
    }

    private val unmasked by lazy {
        val unmaskedArgs = args.map {
            when(it) {
                is MaskedString -> it.unmasked()
                else -> it
            }
        }
        formatter.invoke(unmaskedArgs)
    }

    override fun unmasked() = unmasked

    @JsonValue
    override fun toString() = masked

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is MaskedFormattedString -> unmasked.equals(other.unmasked())
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return unmasked.hashCode()
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
