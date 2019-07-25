package im.toss.maskking

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer

fun unmaskingModule(): SimpleModule {
    val module = SimpleModule()
    module.addSerializer(
        MaskedString::class.java,
        UnmaskSerializer(MaskedString::class.java)
    )
    return module
}

class UnmaskSerializer(t: Class<MaskedString>): StdSerializer<MaskedString>(t) {
    override fun serialize(
        value: MaskedString,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeString(value.unmasked())
    }
}

fun ObjectMapper.registerUnmaskingModule(): ObjectMapper = this.registerModule(
    unmaskingModule()
)
