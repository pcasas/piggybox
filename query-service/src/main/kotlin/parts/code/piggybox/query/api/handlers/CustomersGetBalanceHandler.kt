package parts.code.piggybox.query.api.handlers

import java.math.BigDecimal
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

        ctx.response.status(Status.OK)
        ctx.render(Jackson.json(BalancePayload(store.get(customerId).amount)))
    }

    private data class BalancePayload(val amount: BigDecimal)
}
