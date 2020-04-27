package parts.code.piggybox.command.api.handlers

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import parts.code.money.Money
import parts.code.piggybox.command.config.KafkaConfig
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.toMoneyIDL
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status

class AddFundsHandler @Inject constructor(
    private val clock: Clock,
    private val config: KafkaConfig,
    private val producer: KafkaProducer<String, SpecificRecord>
) : Handler {

    private val logger = LoggerFactory.getLogger(AddFundsHandler::class.java)

    override fun handle(ctx: Context) {
        ctx.parse(AddFundsPayload::class.java).then {
            val record = AddFundsCommand(
                UUID.randomUUID().toString(),
                clock.instant(),
                it.customerId,
                it.money.toMoneyIDL()
            )

            val producerRecord = ProducerRecord(
                config.topics.preferencesAuthorization,
                it.customerId,
                record as SpecificRecord
            )

            producer.send(producerRecord).get()

            logger.info(
                "Sent ${record.schema.name} to topic: ${config.topics.preferencesAuthorization}" +
                        "\n\trecord: $record"
            )

            ctx.response.status(Status.ACCEPTED)
            ctx.render("")
        }
    }

    private data class AddFundsPayload(val customerId: String, val money: Money)
}
