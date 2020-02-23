package parts.code.piggybox.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.command.api.APIEndpoints
import parts.code.piggybox.command.config.KafkaConfig
import parts.code.piggybox.command.modules.APIModule
import parts.code.piggybox.command.modules.KafkaModule
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
                        .module(KafkaModule::class.java)
                        .module(APIModule::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
                .handlers { chain -> chain.prefix("api", APIEndpoints::class.java) }
        }
    }
}
