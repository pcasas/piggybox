package parts.code.piggybox.command.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.command.application.config.ApplicationModule
import parts.code.piggybox.command.application.config.KafkaConfig
import parts.code.piggybox.command.application.handlers.AddFundsHandler
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object CommandServiceApplication {

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
                        .module(ApplicationModule::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
                .handlers { chain ->
                    chain
                        .prefix("add-funds") {
                            it.post(AddFundsHandler::class.java)
                        }
                }
        }
    }
}
