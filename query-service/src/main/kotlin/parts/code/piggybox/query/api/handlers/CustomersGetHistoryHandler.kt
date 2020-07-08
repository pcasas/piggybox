package parts.code.piggybox.query.api.handlers

import java.time.format.DateTimeFormatter
import javax.inject.Inject
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.HistoryState
import parts.code.piggybox.schemas.TransactionType
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status
import ratpack.jackson.Jackson

class CustomersGetHistoryHandler @Inject constructor(
    private val config: KafkaConfig,
    private val streams: KafkaStreams
) : Handler {

    override fun handle(ctx: Context) {
        val customerId = ctx.request.queryParams["customerId"]

        val store = streams.store(
            config.stateStores.historyReadModel,
            QueryableStoreTypes.keyValueStore<String, HistoryState>()
        )

        if (store.get(customerId) == null) {
            ctx.response.status(Status.NOT_FOUND).send()
        } else {
            ctx.response.status(Status.OK)
            val transactions = store.get(customerId).transactions.map {
                val amount = (if (it.type == TransactionType.FUNDS_WITHDRAWN) "-" else "") + it.amount.toPlainString()
                TransactionPayload(
                    it.description,
                    it.date.format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                    it.type.name,
                    amount
                )
            }
            ctx.render(Jackson.json(HistoryPayload(transactions)))
        }
    }

    private data class HistoryPayload(val transactions: List<TransactionPayload>)
    private data class TransactionPayload(
        val description: String,
        val date: String,
        val type: String,
        val amount: String
    )
}
