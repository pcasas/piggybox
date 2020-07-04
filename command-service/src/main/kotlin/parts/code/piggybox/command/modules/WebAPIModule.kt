package parts.code.piggybox.command.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.command.api.WebAPIEndpoints
import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.ChangeCountryHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler
import parts.code.piggybox.command.api.handlers.WithdrawFundsHandler

class WebAPIModule : AbstractModule() {

    override fun configure() {
        bind(WebAPIEndpoints::class.java)
        bind(AddFundsHandler::class.java)
        bind(WithdrawFundsHandler::class.java)
        bind(ChangeCountryHandler::class.java)
        bind(CreatePreferencesHandler::class.java)
    }
}
