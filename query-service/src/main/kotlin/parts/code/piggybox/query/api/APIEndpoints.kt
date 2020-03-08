package parts.code.piggybox.query.api

import parts.code.piggybox.query.api.handlers.CustomersGetBalanceHandler
import ratpack.func.Action
import ratpack.handling.Chain

class APIEndpoints : Action<Chain> {

    override fun execute(chain: Chain) {
        chain
            .get("customers.getBalance", CustomersGetBalanceHandler::class.java)
    }
}
