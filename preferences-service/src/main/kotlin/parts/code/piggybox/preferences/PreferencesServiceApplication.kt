package parts.code.piggybox.preferences

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.modules.KafkaModule
import parts.code.piggybox.preferences.services.PreferencesStreamService
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object PreferencesServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        RatpackServer.start { server ->
            server
                .serverConfig { config ->
                    config
                        .baseDir(BaseDir.find())
                        .yaml("application.yaml")
                        .require("/kafka", KafkaConfig::class.java)
                        .jacksonModules(KotlinModule())
                }
                .registry(Guice.registry { bindings ->
                    bindings
                        .module(KafkaModule::class.java)
                        .bind(PreferencesStreamService::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
        }
    }
}
