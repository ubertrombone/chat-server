@file:OptIn(ExperimentalSerializationApi::class)

package com.joshrose.util

import com.joshrose.chat_model.Functions.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

object GroupAsStringSerializer : KSerializer<Group> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Group", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Group) = encoder.encodeString(Json.encodeToString(value))
    override fun deserialize(decoder: Decoder): Group = Json.decodeFromString(decoder.decodeString())
}

object IndividualAsStringSerializer : KSerializer<Individual> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Individual", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Individual) = encoder.encodeString(Json.encodeToString(value))
    override fun deserialize(decoder: Decoder): Individual = Json.decodeFromString(decoder.decodeString())
}

object JoinAsStringSerializer : KSerializer<Join> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Join", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Join) = encoder.encodeString(Json.encodeToString(value))
    override fun deserialize(decoder: Decoder): Join = Json.decodeFromString(decoder.decodeString())
}

object LeaveAsStringSerializer : KSerializer<Leave> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Leave", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Leave) = encoder.encodeString(Json.encodeToString(value))
    override fun deserialize(decoder: Decoder): Leave = Json.decodeFromString(decoder.decodeString())
}

object ErrorAsStringSerializer : KSerializer<Error> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Error", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Error) {
        println("VALUE: $value")
        Json.encodeToString(value)
    }
    override fun deserialize(decoder: Decoder): Error = Json.decodeFromString(decoder.decodeString())
}