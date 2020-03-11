package parts.code.piggybox.preferences

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.modules.KafkaModule
import parts.code.piggybox.preferences.modules.PreferencesModule
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object PreferencesServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        RatpackServer.start { server ->
            server
                .serverConfig {
                    it
                        .baseDir(BaseDir.find())
                        .yaml("application.yaml")
                        .require("/kafka", KafkaConfig::class.java)
                        .jacksonModules(KotlinModule())
                }
                .registry(Guice.registry {
                    it
                        .module(KafkaModule::class.java)
                        .module(PreferencesModule::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
        }
    }
}
