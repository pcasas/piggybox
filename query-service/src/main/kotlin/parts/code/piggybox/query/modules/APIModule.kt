package parts.code.piggybox.query.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.query.api.APIEndpoints
import parts.code.piggybox.query.api.handlers.CustomersGetBalanceHandler

class APIModule : AbstractModule() {

    override fun configure() {
        bind(APIEndpoints::class.java)
        bind(CustomersGetBalanceHandler::class.java)
    }
}
