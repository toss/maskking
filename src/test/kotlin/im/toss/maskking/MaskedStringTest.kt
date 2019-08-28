package im.toss.maskking

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import org.junit.jupiter.api.Test

class MaskedStringTest {

    @Test
    fun `toString()하면 마스킹된 결과가 나온다`() {
        MaskedString.of("abc").toString().equalsTo("***")

        MaskedString.of("", MaskingPattern.MIDDLE_HALF).toString().equalsTo("")
        MaskedString.of("a", MaskingPattern.MIDDLE_HALF).toString().equalsTo("*")
        MaskedString.of("ab", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a*")
        MaskedString.of("abc", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a*c")
        MaskedString.of("abcd", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a**d")
        MaskedString.of("abcde", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a***e")
        MaskedString.of("abcdef", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a****f")
        MaskedString.of("abcdefg", MaskingPattern.MIDDLE_HALF).toString().equalsTo("a*****g")
        MaskedString.of("abcdefgh", MaskingPattern.MIDDLE_HALF).toString().equalsTo("ab****gh")
        MaskedString.of("테스터", MaskingPattern.MIDDLE_HALF).toString().equalsTo("테*터")

        MaskedString.of("", MaskingPattern.LAST_HALF).toString().equalsTo("")
        MaskedString.of("a", MaskingPattern.LAST_HALF).toString().equalsTo("*")
        MaskedString.of("ab", MaskingPattern.LAST_HALF).toString().equalsTo("a*")
        MaskedString.of("abc", MaskingPattern.LAST_HALF) .toString().equalsTo("a**")
        MaskedString.of("abcd", MaskingPattern.LAST_HALF ).toString().equalsTo("ab**")
        MaskedString.of("abcde", MaskingPattern.LAST_HALF ).toString().equalsTo("ab***")
        MaskedString.of("abcdef", MaskingPattern.LAST_HALF ).toString().equalsTo("abc***")
        MaskedString.of("abcdefg", MaskingPattern.LAST_HALF ).toString().equalsTo("abc****")
        MaskedString.of("abcdefgh", MaskingPattern.LAST_HALF ).toString().equalsTo("abcd****")
        MaskedString.of("테스터", MaskingPattern.LAST_HALF).toString().equalsTo("테**")
        MaskedString.of("테스터", MaskingPattern.NONE).toString().equalsTo("테스터")
        MaskedString.none("테스터").toString().equalsTo("테스터")
    }

    @Test
    fun `MaskedString equals는 같은 타입이고 값이 같아야만 equal이다`() {
        val value = "테스트"
        MaskedString.of(value).doesNotEqualTo(value)
        MaskedString.of(value).doesNotEqualTo(1)
        MaskedString.of(value, MaskingPattern.LAST_HALF).doesNotEqualTo(value)
        MaskedString.of(value, MaskingPattern.LAST_HALF).equalsTo(MaskedString.of(value))
        MaskedString.format("%s value", value).equalsTo(MaskedString.format("%s value", value))
        MaskedString.format("%s value", value).equalsTo(MaskedString.format("%s value", MaskedString.of(value)))
        MaskedString.format("%s", value).doesNotEqualTo(value)
        MaskedString.format("%s", value ).doesNotEqualTo(MaskedString.of(value))
        MaskedString.format("%s", MaskedString.of(value)).doesNotEqualTo(value)
        MaskedString.format("%s1", MaskedString.of(value)).doesNotEqualTo(value)
        MaskedString.format("%s", MaskedString.of(value)).doesNotEqualTo(1)
    }

    @Test
    fun `MaskedString hashCode는 value의 hashCode와 같아야한다`() {
        val value = "테스트"
        assert(MaskedString.of(value).hashCode() == value.hashCode())
        assert(MaskedString.of(value, MaskingPattern.LAST_HALF).hashCode() == value.hashCode())
        assert(MaskedString.format("%s", value).hashCode() == value.hashCode())
        assert(MaskedString.format( "%s", MaskedString.of(value)).hashCode() == value.hashCode())
        assert(MaskedString.format( "%s1", MaskedString.of(value)).hashCode() != value.hashCode())
    }

    @Test
    fun `MaskedString CharSequence로 값에 접근하면 masked value를 반환한다`() {
        val value = "테스트"
        val maskedString = MaskedString.of(value)
        val formatedMaskedString = MaskedString.format("%s", maskedString)

        assert(maskedString.length == 3)
        assert(maskedString[0] == '*' && maskedString[1] == '*' && maskedString[2] == '*')
        assert(maskedString.subSequence(1,3) == "**")
        assert(formatedMaskedString.length == 3)
        assert(formatedMaskedString[0] == '*' && formatedMaskedString[1] == '*' && formatedMaskedString[2] == '*')
        assert(formatedMaskedString.subSequence(1,3) == "**")
    }

    @Test
    fun `unmasked()하면 언마스킹된 결과가 나온다`() {
        MaskedString.of("a").unmasked().equalsTo("a")
        MaskedString.of("ab", MaskingPattern.MIDDLE_HALF).unmasked().equalsTo("ab")
    }

    @Test
    fun `CharSequence타입에 대해 unmask한다`() {
        val name: String = "김토스"
        val maskedString1: CharSequence = MaskedString.of(name)
        val maskedString2: CharSequence = MaskedString.format("%s님", maskedString1)
        val maskedString3: CharSequence = MaskedString.format("%s, %s", maskedString2, maskedString2)

        StringBuilder(name).unmasked().equalsTo("김토스")
        maskedString1.unmasked().equalsTo("김토스")
        maskedString2.unmasked().equalsTo("김토스님")
        maskedString3.unmasked().equalsTo("김토스님, 김토스님")
        null.unmasked().equalsTo(null)
    }

    @Test
    fun `포매팅을 하면 masked string만 마스킹한다`() {
        val name = MaskedString.of("테스터")
        val weekday = "월요일"
        val hello =
                MaskedString.format("%s님, 안녕하세요. 오늘은 %s입니다. %s", name, weekday, null)

        hello.toString().equalsTo("***님, 안녕하세요. 오늘은 월요일입니다. null")
        hello.unmasked().equalsTo("테스터님, 안녕하세요. 오늘은 월요일입니다. null")
    }

    private val objectMapper = ObjectMapper().registerKotlinModule()

    data class Person(
        val id: Int,
        val name: MaskedString
    )

    @Test
    fun `jackson으로 읽으면 마스킹되어 읽힌다`() {
        val actual = objectMapper.readValue<Person>("""{"id":123,"name":"테스터"}""")
        actual.name.toString().equalsTo("***")
        actual.name.unmasked().equalsTo("테스터")
    }

    @Test
    fun `jackson으로 쓰면 마스킹되어 쓰인다`() {
        val person = Person(
            id = 123,
            name = MaskedString.of("테스터")
        )
        val actual = objectMapper.writeValueAsString(person)
        actual.equalsTo("""{"id":123,"name":"***"}""")
    }

    @Test
    fun `unmasking module을 등록한 jackson으로 쓰면 마스킹이 풀린채로 쓰인다`() {
        val person = Person(
            id = 123,
            name = MaskedString.of("테스터")
        )
        val actual = objectMapper.registerUnmaskingModule().writeValueAsString(person)
        actual.equalsTo("""{"id":123,"name":"테스터"}""")
    }

    @Test
    fun `MaskedString이 jackson으로 쓰면, String으로 쓰인다`() {
        val json1 = ObjectMapper().writeValueAsString(MaskedString.none("hello world"))
        val json2 = ObjectMapper().writeValueAsString(MaskedString.format("hello %s", "world"))
        json1.equalsTo("\"hello world\"")
        json2.equalsTo("\"hello world\"")
    }

    @Test
    fun `Supports user-defined formatter`() {
        val masked = MaskedString.format(
            "Hello",
            MaskedString.of("tester")
        ) { (greet, name) ->
            "$greet, $name"
        }

        masked.toString().equalsTo("Hello, ******")
        masked.unmasked().equalsTo("Hello, tester")
    }
}
