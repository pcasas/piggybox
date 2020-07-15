package parts.code.piggybox.query.api.handlers

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.BalanceState
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status
import ratpack.jackson.Jackson

class CustomersGetBalanceHandler @Inject constructor(
    private val config: KafkaConfig,
    private val streams: KafkaStreams
) : Handler {

    override fun handle(ctx: Context) {
        val customerId = ctx.request.queryParams["customerId"]

        val store = streams.store(
            config.stateStores.balanceReadModel,
            QueryableStoreTypes.keyValueStore<String, BalanceState>()
        )

        val balanceState = store.get(customerId)
        val balancePayload = if (balanceState == null) {
            BalancePayload(ZERO.setScale(2), 0)
        } else {
            BalancePayload(balanceState.amount.setScale(2), balanceState.version)
        }

        ctx.response.status(Status.OK)
        ctx.render(Jackson.json(balancePayload))
    }

    private data class BalancePayload(val amount: BigDecimal, val version: Long, val min: Int = 0, val max: Int = 500)
}
