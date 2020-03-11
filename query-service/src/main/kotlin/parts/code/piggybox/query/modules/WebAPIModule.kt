package parts.code.piggybox.query.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.query.api.WebAPIEndpoints
import parts.code.piggybox.query.api.handlers.CustomersGetBalanceHandler

class WebAPIModule : AbstractModule() {

    override fun configure() {
        bind(WebAPIEndpoints::class.java)
        bind(CustomersGetBalanceHandler::class.java)
    }
}
