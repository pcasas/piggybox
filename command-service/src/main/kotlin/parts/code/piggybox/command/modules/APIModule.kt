package parts.code.piggybox.command.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.command.api.APIEndpoints
import parts.code.piggybox.command.api.handlers.AddFundsHandler
import parts.code.piggybox.command.api.handlers.BuyGameHandler
import parts.code.piggybox.command.api.handlers.CreatePreferencesHandler

class APIModule : AbstractModule() {

    override fun configure() {
        bind(APIEndpoints::class.java)
        bind(AddFundsHandler::class.java)
        bind(BuyGameHandler::class.java)
        bind(CreatePreferencesHandler::class.java)
    }
}
