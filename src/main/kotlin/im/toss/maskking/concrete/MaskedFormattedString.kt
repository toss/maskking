package im.toss.maskking.concrete

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import im.toss.maskking.MaskedString
import im.toss.maskking.log.cover
import mu.KotlinLogging

private val logger = KotlinLogging.logger(cover {})

internal class MaskedFormattedString(
    args: Any?,
    private val formatter: (args: Any?) -> String
): MaskedString {

    private val maskedArgs = args

    private val masked by lazy {
        try {
            formatter.invoke(maskedArgs)
        } catch (e: Exception) {
            logger.error("Failed to format with masked arguments", e)
            ""
        }
    }

    private val unmasked by lazy {
        try {
            formatter.invoke(unmaskedValue(args))
        } catch (e: Exception) {
            logger.error("Failed to format with unmasked arguments", e)
            ""
        }
    }

    private fun unmaskedMap(args: Map<*, *>) =
        args.map { pair ->
            pair.key to unmaskedValue(pair.value)
        }.toMap()

    private fun unmaskedCollection(args: Collection<*>) =
        args.map { unmaskedValue(it) }

    private fun unmaskedValue(value: Any?): Any? =
        when(value) {
            is MaskedString -> value.unmasked()
            is Map<*, *> -> unmaskedMap(value)
            is Collection<*> -> unmaskedCollection(value)
            else -> value
        }

    override fun unmasked() = unmasked

    @JsonValue
    override fun toString() = masked

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is MaskedFormattedString -> unmasked == other.unmasked()
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
