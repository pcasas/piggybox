package parts.code.piggybox.kafka.init

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import parts.code.piggybox.kafka.init.config.KafkaConfig
import parts.code.piggybox.kafka.init.services.CreateTopicsService
import parts.code.piggybox.kafka.init.services.KafkaAdminClientService
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object KafkaInitServiceApplication {

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
                        .bind(KafkaAdminClientService::class.java)
                        .bind(CreateTopicsService::class.java)
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                })
        }
    }
}
