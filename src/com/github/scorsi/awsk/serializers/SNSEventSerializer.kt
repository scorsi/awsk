package com.github.scorsi.awsk.serializers

import com.amazonaws.services.lambda.runtime.events.SNSEvent
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonInput
import kotlinx.serialization.json.JsonObject
import org.joda.time.DateTime

@Serializer(forClass = SNSEvent::class)
object SNSEventSerializer : KSerializer<SNSEvent> {
    override fun serialize(encoder: Encoder, value: SNSEvent) {
        TODO()
    }

    @ImplicitReflectionSerializer
    override fun deserialize(decoder: Decoder): SNSEvent {
        val input = decoder as? JsonInput
            ?: throw SerializationException("This class can be loaded only by Json")
        val json = input.json
        val tree = input.decodeJson() as? JsonObject
            ?: throw SerializationException("Expected JsonObject")

        return SNSEvent()
            .withRecords(
                (tree["Records"] as? JsonArray
                    ?: throw SerializationException("Expected JsonArray"))
                    .map { json.parse(SNSRecordsSerializer, it.toString()) }
            )
    }

    @Serializer(forClass = SNSEvent.SNSRecord::class)
    private object SNSRecordsSerializer : KSerializer<SNSEvent.SNSRecord> {
        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): SNSEvent.SNSRecord {
            val input = decoder as? JsonInput
                ?: throw SerializationException("This class can be loaded only by Json")
            val json = input.json
            val tree = input.decodeJson() as? JsonObject
                ?: throw SerializationException("Expected JsonObject")

            return SNSEvent.SNSRecord()
                .withEventSource(tree["EventSource"].toString())
                .withEventVersion(tree["EventVersion"].toString())
                .withEventSubscriptionArn(tree["EventSubscriptionArn"].toString())
                .withSns(json.parse(SNSSerializer, tree["Sns"].toString()))
        }
    }

    @Serializer(forClass = SNSEvent.SNS::class)
    private object SNSSerializer : KSerializer<SNSEvent.SNS> {
        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): SNSEvent.SNS {
            val input = decoder as? JsonInput
                ?: throw SerializationException("This class can be loaded only by Json")
            val json = input.json
            val tree = input.decodeJson() as? JsonObject
                ?: throw SerializationException("Expected JsonObject")

            return SNSEvent.SNS()
                .withType(tree["Type"].toString())
                .withMessageId(tree["MessageId"].toString())
                .withTopicArn(tree["TopicArn"].toString())
                .withSubject(tree["Subject"].toString())
                .withMessage(tree["Message"].toString())
                .withTimestamp(tree["Timestamp"].toString().replace("\"", "").let { println(it); DateTime.parse(it) })
                .withSignatureVersion(tree["SignatureVersion"].toString())
                .withSignature(tree["Signature"].toString())
                .withSigningCertUrl(tree["SigningCertUrl"].toString())
                .withUnsubscribeUrl(tree["UnsubscribeUrl"].toString())
                .withMessageAttributes(
                    (tree["MessageAttributes"] as? JsonObject
                        ?: throw SerializationException("Expected JsonObject"))
                        .map { (k, v) -> Pair(k, json.parse(MessageAttributeSerializer, v.toString())) }
                        .toMap()
                )
        }
    }

    @Serializer(forClass = SNSEvent.MessageAttribute::class)
    private object MessageAttributeSerializer : KSerializer<SNSEvent.MessageAttribute> {
        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): SNSEvent.MessageAttribute {
            val input = decoder as? JsonInput
                ?: throw SerializationException("This class can be loaded only by Json")
            val tree = input.decodeJson() as? JsonObject
                ?: throw SerializationException("Expected JsonObject")

            return SNSEvent.MessageAttribute()
                .withType(tree["Type"].toString())
                .withValue(tree["Value"].toString())
        }
    }
}