package im.toss.maskking

import com.fasterxml.jackson.annotation.JsonCreator
import im.toss.maskking.concrete.MaskedFormattedString
import im.toss.maskking.concrete.MaskedStringImpl

interface MaskedString : CharSequence {
    fun unmasked(): String

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: String): MaskedString = MaskedStringImpl(value)
        fun of(value: String, pattern: MaskingPattern): MaskedString = MaskedStringImpl(value, pattern)
        fun format(format: String, vararg args: Any?): MaskedString = MaskedFormattedString(args.toList()) {
            String.format(format, *((it as List<Any?>).toTypedArray()))
        }
        fun format(vararg args: Any?, formatter: (args: List<Any?>) -> String): MaskedString = MaskedFormattedString(args.toList()) {
            formatter.invoke(it as List<Any?>)
        }
        fun format(args: Map<*, *>, formatter: (args: Map<*, *>) -> String): MaskedString = MaskedFormattedString(args) {
            formatter.invoke(it as Map<*, *>)
        }
        fun none(value: String): MaskedString = MaskedStringImpl(value, MaskingPattern.NONE)
    }
}

fun CharSequence?.unmasked(): String? {
    return when(this) {
        null -> null
        is MaskedString -> this.unmasked()
        else -> this.toString()
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
