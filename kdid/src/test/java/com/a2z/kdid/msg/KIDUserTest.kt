package com.a2z.kdid.msg

import com.a2z.kdid.KIDUser
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test

class KIDUserTest {
    @ImplicitReflectionSerializer
    @Test
    fun publish_test() {
        val user = KIDUser(
            "권용석",
            "M",
            "19741205",
            "+821033838706",
            "7412051398915"
        )

        user.publish()
    }
}