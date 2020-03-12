package parts.code.piggybox.command.api

import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.BuyGameHandler
import parts.code.piggybox.command.api.handlers.ChangeCountryHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler
import ratpack.func.Action
import ratpack.handling.Chain

class WebAPIEndpoints : Action<Chain> {

    override fun execute(chain: Chain) {
        chain
            .post("preferences.create", CreatePreferencesHandler::class.java)
            .post("preferences.changeCountry", ChangeCountryHandler::class.java)
            .post("balance.addFunds", AddFundsHandler::class.java)
            .post("balance.buyGame", BuyGameHandler::class.java)
    }
}
