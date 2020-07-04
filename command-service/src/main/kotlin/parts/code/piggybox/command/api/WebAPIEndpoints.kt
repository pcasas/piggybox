package parts.code.piggybox.command.api

import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.ChangeCountryHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler
import parts.code.piggybox.command.api.handlers.WithdrawFundsHandler
import ratpack.func.Action
import ratpack.handling.Chain

class WebAPIEndpoints : Action<Chain> {

    override fun execute(chain: Chain) {
        chain
            .post("preferences.create", CreatePreferencesHandler::class.java)
            .post("preferences.changeCountry", ChangeCountryHandler::class.java)
            .post("balance.addFunds", AddFundsHandler::class.java)
            .post("balance.withdrawFunds", WithdrawFundsHandler::class.java)
    }
}
