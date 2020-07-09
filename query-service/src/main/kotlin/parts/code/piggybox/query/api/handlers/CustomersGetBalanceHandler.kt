package parts.code.piggybox.query.api.handlers

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
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

        val balance = if (store.get(customerId) == null) {
            BigDecimal.ZERO.setScale(2)
        } else {
            store.get(customerId).amount.setScale(2, HALF_EVEN)
        }

        ctx.response.status(Status.OK)
        ctx.render(Jackson.json(BalancePayload(balance)))
    }

    private data class BalancePayload(val amount: BigDecimal, val min: Int = 0, val max: Int = 500)
}
