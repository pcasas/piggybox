package parts.code.piggybox.command.application.handlers

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import parts.code.piggybox.command.application.config.KafkaConfig
import parts.code.piggybox.schemas.CreatePreferencesCommand
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status

class CreatePreferencesHandler @Inject constructor(
    private val config: KafkaConfig,
    private val producer: KafkaProducer<String, SpecificRecord>
) : Handler {

    private val logger = LoggerFactory.getLogger(CreatePreferencesHandler::class.java)

    override fun handle(ctx: Context) {
        ctx.parse(CreatePreferencesPayload::class.java).then {
            val event = CreatePreferencesCommand(UUID.randomUUID().toString(), Instant.now(), it.customerId, it.currency)

            val record =
                ProducerRecord(config.topics.preferencesAuthorization, event.customerId, event as SpecificRecord)
            producer.send(record).get()
            logger.info("Event sent to ${config.topics.preferencesAuthorization} = $record")
            ctx.response.status(Status.ACCEPTED)
            ctx.render("")
        }
    }

    private data class CreatePreferencesPayload(val customerId: String, val currency: String)
}
