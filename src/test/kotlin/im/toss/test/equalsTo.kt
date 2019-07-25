package im.toss.test

import org.assertj.core.api.Assertions

fun Any?.equalsTo(other: Any?) {
    Assertions.assertThat(this).isEqualTo(other)
}

fun Any?.doesNotEqualTo(other: Any?) {
    Assertions.assertThat(this).isNotEqualTo(other)
}

fun Iterable<Any>?.shouldNotBeEmpty() {
    Assertions.assertThat(this).isNotEmpty
}

fun Iterable<Any>?.shouldBeEmpty() {
    Assertions.assertThat(this).isEmpty()
}

fun Boolean?.shouldBeTrue() {
    Assertions.assertThat(this).isTrue()
}

fun Boolean?.shouldBeFalse() {
    Assertions.assertThat(this).isFalse()
}
