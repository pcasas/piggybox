package parts.code.piggybox.balance

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Clock
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.balance.modules.BalanceModule
import parts.code.piggybox.balance.modules.KafkaModule
import parts.code.piggybox.balance.modules.MetricsModule
import parts.code.piggybox.extensions.health
import parts.code.piggybox.extensions.mdc
import parts.code.piggybox.extensions.public
import parts.code.piggybox.extensions.yaml
import ratpack.dropwizard.metrics.DropwizardMetricsConfig
import ratpack.dropwizard.metrics.DropwizardMetricsModule
import ratpack.dropwizard.metrics.MetricsPrometheusHandler
import ratpack.guice.Guice
import ratpack.health.HealthCheckHandler
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

object BalanceServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        RatpackServer.start { server ->
            server
                .serverConfig {
                    it
                        .baseDir(BaseDir.find())
                        .yaml()
                        .require("/metrics", DropwizardMetricsConfig::class.java)
                        .require("/kafka", KafkaConfig::class.java)
                        .jacksonModules(KotlinModule())
                }
                .registry(Guice.registry {
                    it
                        .module(DropwizardMetricsModule::class.java)
                        .module(MetricsModule::class.java)
                        .module(KafkaModule::class.java)
                        .module(BalanceModule::class.java)
                        .bindInstance(Clock::class.java, Clock.systemUTC())
                        .bindInstance(ObjectMapper::class.java, ObjectMapper().registerModule(KotlinModule()))
                        .mdc()
                        .health()
                })
                .handlers {
                    it
                        .get("metrics", MetricsPrometheusHandler())
                        .get("health", HealthCheckHandler())
                        .public()
                }
        }
    }
}
