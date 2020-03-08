package parts.code.piggybox.balance.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.balance.services.BalanceService
import parts.code.piggybox.balance.services.BalanceStreamService

class BalanceModule : AbstractModule() {

    override fun configure() {
        bind(BalanceService::class.java)
        bind(BalanceStreamService::class.java)
    }
}
