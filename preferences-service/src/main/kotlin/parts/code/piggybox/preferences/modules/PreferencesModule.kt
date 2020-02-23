package parts.code.piggybox.preferences.modules

import com.google.inject.AbstractModule
import parts.code.piggybox.preferences.services.PreferencesService
import parts.code.piggybox.preferences.services.PreferencesStreamService

class PreferencesModule : AbstractModule() {

    override fun configure() {
        bind(PreferencesService::class.java)
        bind(PreferencesStreamService::class.java)
    }
}
