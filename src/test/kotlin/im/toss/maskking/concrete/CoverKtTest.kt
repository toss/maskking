package im.toss.maskking.concrete

import im.toss.maskking.log.cover
import im.toss.test.equalsTo
import org.junit.jupiter.api.Test

internal class CoverKtTest {

    @Test
    fun `cover() just runs the given function`() {
        var invoked = false
        cover { invoked = true}
        invoked.equalsTo(true)
    }
}
