package parts.code.piggybox.command.api.handlers

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import parts.code.piggybox.command.config.KafkaConfig
import parts.code.piggybox.schemas.commands.AddFundsCommand
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status

class AddFundsHandler @Inject constructor(
    private val config: KafkaConfig,
    private val producer: KafkaProducer<String, SpecificRecord>
) : Handler {

    private val logger = LoggerFactory.getLogger(AddFundsHandler::class.java)

    override fun handle(ctx: Context) {
        ctx.parse(AddFundsPayload::class.java).then {
            val event = AddFundsCommand(UUID.randomUUID().toString(), Instant.now(), it.customerId, it.amount)

            val record =
                ProducerRecord(config.topics.preferencesAuthorization, event.customerId, event as SpecificRecord)
            producer.send(record).get()
            logger.info("Event sent to ${config.topics.preferencesAuthorization}: $record")
            ctx.response.status(Status.ACCEPTED)
            ctx.render("")
        }
    }

    private data class AddFundsPayload(val customerId: String, val amount: BigDecimal)
}
