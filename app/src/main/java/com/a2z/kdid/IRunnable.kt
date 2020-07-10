package com.a2z.kdid

import kotlinx.serialization.*

interface IRunnable {
    fun run()
}

@Serializable
class Horse : IRunnable {
    override fun run() {
        println("horse running")
    }
}

@Serializable
class Dog : IRunnable {
    override fun run() {
        println("dog running")
    }
}

@Serializer(forClass = Horse::class)
object HorseSerializer {
//    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Horse", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: Horse) {
        encoder.encodeString("H")
    }

    override fun deserialize(decoder: Decoder): Horse {
        val str = decoder.decodeString()
        return Horse()
    }
}

@Serializer(forClass = Dog::class)
object DogSerializer {
//    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Dog", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: Dog) {
        encoder.encodeString("D")
    }

    override fun deserialize(decoder: Decoder): Dog {
        val str = decoder.decodeString()
        return Dog()
    }
}

@Serializable
data class Zoo(val runnable : IRunnable)