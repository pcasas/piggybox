package parts.code.piggybox.query.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.query.api.WebAPIEndpoints
import parts.code.piggybox.query.api.handlers.CustomersGetBalanceHandler
import parts.code.piggybox.query.api.handlers.CustomersGetHistoryHandler
import parts.code.piggybox.query.api.handlers.CustomersGetPreferencesHandler

class WebAPIModule : AbstractModule() {

    override fun configure() {
        bind(WebAPIEndpoints::class.java)
        bind(CustomersGetPreferencesHandler::class.java)
        bind(CustomersGetBalanceHandler::class.java)
        bind(CustomersGetHistoryHandler::class.java)
    }
}
