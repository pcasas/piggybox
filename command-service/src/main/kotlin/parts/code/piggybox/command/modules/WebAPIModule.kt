package parts.code.piggybox.command.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.command.api.WebAPIEndpoints
import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.BuyGameHandler
import parts.code.piggybox.command.api.handlers.ChangeCountryHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler

class WebAPIModule : AbstractModule() {

    override fun configure() {
        bind(WebAPIEndpoints::class.java)
        bind(AddFundsHandler::class.java)
        bind(BuyGameHandler::class.java)
        bind(ChangeCountryHandler::class.java)
        bind(CreatePreferencesHandler::class.java)
    }
}
