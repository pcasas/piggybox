package parts.code.piggybox.query.api

import parts.code.piggybox.query.api.handlers.CustomersGetBalanceHandler
import parts.code.piggybox.query.api.handlers.CustomersGetPreferencesHandler
import ratpack.func.Action
import ratpack.handling.Chain

class WebAPIEndpoints : Action<Chain> {

    override fun execute(chain: Chain) {
        chain
            .get("customers.getPreferences", CustomersGetPreferencesHandler::class.java)
            .get("customers.getBalance", CustomersGetBalanceHandler::class.java)
    }
}
