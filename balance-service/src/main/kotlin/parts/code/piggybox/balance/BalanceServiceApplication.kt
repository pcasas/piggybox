package parts.code.piggybox.balance

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.balance.modules.BalanceModule
import parts.code.piggybox.balance.modules.KafkaModule
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object BalanceServiceApplication {

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
                        .module(BalanceModule::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
        }
    }
}
