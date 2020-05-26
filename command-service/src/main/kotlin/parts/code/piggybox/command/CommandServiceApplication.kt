package parts.code.piggybox.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Clock
import parts.code.piggybox.command.api.WebAPIEndpoints
import parts.code.piggybox.command.config.KafkaConfig
import parts.code.piggybox.command.modules.KafkaModule
import parts.code.piggybox.command.modules.WebAPIModule
import parts.code.piggybox.extensions.yaml
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object CommandServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        RatpackServer.start { server ->
            server
                .serverConfig {
                    it
                        .baseDir(BaseDir.find())
                        .yaml()
                        .require("/kafka", KafkaConfig::class.java)
                        .jacksonModules(KotlinModule())
                }
                .registry(Guice.registry {
                    it
                        .module(KafkaModule::class.java)
                        .module(WebAPIModule::class.java)
                        .bindInstance(Clock::class.java, Clock.systemUTC())
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
                .handlers {
                    it
                        .prefix("api", WebAPIEndpoints::class.java)
                }
        }
    }
}
