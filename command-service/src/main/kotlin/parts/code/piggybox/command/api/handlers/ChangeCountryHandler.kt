package parts.code.piggybox.command.api.handlers

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import parts.code.piggybox.command.config.KafkaConfig
import parts.code.piggybox.schemas.ChangeCountryCommand
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status

class ChangeCountryHandler @Inject constructor(
    private val config: KafkaConfig,
    private val producer: KafkaProducer<String, SpecificRecord>
) : Handler {

    private val logger = LoggerFactory.getLogger(ChangeCountryHandler::class.java)

    override fun handle(ctx: Context) {
        ctx.parse(ChangeCountryPayload::class.java).then {
            val command = ChangeCountryCommand(
                UUID.randomUUID().toString(),
                Instant.now(),
                it.customerId,
                it.country
            )

            val record = ProducerRecord(
                config.topics.preferencesAuthorization,
                command.customerId,
                command as SpecificRecord
            )
            producer.send(record).get()

            logger.info(
                "Sent ${command.schema.name} to topic: ${config.topics.preferencesAuthorization}" +
                        "\n\trecord: $command"
            )

            ctx.response.status(Status.ACCEPTED)
            ctx.render("")
        }
    }

    private data class ChangeCountryPayload(val customerId: String, val country: String)
}
