package parts.code.piggybox.preferences.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.preferences.services.BalanceService
import parts.code.piggybox.preferences.services.PreferencesService
import parts.code.piggybox.preferences.services.PreferencesStreamService

class PreferencesModule : AbstractModule() {

    override fun configure() {
        bind(BalanceService::class.java)
        bind(PreferencesService::class.java)
        bind(PreferencesStreamService::class.java)
    }
}
