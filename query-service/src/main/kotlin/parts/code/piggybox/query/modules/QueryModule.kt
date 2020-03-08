package parts.code.piggybox.query.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.query.services.QueryStreamService

class QueryModule : AbstractModule() {

    override fun configure() {
        bind(QueryStreamService::class.java)
    }
}
