package parts.code.piggybox.query.api.handlers

import javax.inject.Inject
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.PreferencesState
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status
import ratpack.jackson.Jackson

class CustomersGetPreferencesHandler @Inject constructor(
    private val config: KafkaConfig,
    private val streams: KafkaStreams
) : Handler {

    override fun handle(ctx: Context) {
        ctx.parse(CustomersGetPreferencesPayload::class.java).then {
            val store = streams.store(
                config.stateStores.preferencesReadModel,
                QueryableStoreTypes.keyValueStore<String, PreferencesState>()
            )

            ctx.response.status(Status.OK)
            val preferences = store.get(it.customerId)
            ctx.render(Jackson.json(PreferencesPayload(preferences.currency, preferences.country)))
        }
    }

    private data class CustomersGetPreferencesPayload(val customerId: String)
    private data class PreferencesPayload(val currency: String, val country: String)
}
