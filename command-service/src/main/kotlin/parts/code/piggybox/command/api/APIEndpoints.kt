package parts.code.piggybox.command.api

import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler
import ratpack.func.Action
import ratpack.handling.Chain

class APIEndpoints : Action<Chain> {

    override fun execute(chain: Chain) {
        chain
            .post("balance.addFunds", AddFundsHandler::class.java)
            .post("preferences.create", CreatePreferencesHandler::class.java)
    }
}
