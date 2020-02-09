package parts.code.piggybox.command.application.handlers

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import parts.code.piggybox.command.application.config.KafkaConfig
import parts.code.piggybox.schemas.AddFundsRequested
import ratpack.handling.Context
import ratpack.handling.Handler
import java.math.BigDecimal
import javax.inject.Inject

class AddFundsHandler @Inject constructor(
    private val config: KafkaConfig,
    private val producer: KafkaProducer<String, SpecificRecord>
) : Handler {

    private val logger = LoggerFactory.getLogger(AddFundsHandler::class.java)

    override fun handle(ctx: Context) {
        ctx.parse(AddFundsPayload::class.java).then {
            val event = AddFundsRequested.newBuilder()
                .setCustomerId(it.customerId)
                .setAmount(it.amount)
                .build()

            val record = ProducerRecord(config.topics.fundsAuthorization, event.customerId, event as SpecificRecord)
            producer.send(record).get()
            logger.info("Event sent to ${config.topics.fundsAuthorization} = $record")
            ctx.render("")
        }
    }

    private data class AddFundsPayload(val customerId: String, val amount: BigDecimal)
}
