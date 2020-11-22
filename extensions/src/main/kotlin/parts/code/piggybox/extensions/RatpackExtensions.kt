package parts.code.piggybox.extensions

import org.slf4j.MDC
import ratpack.exec.Promise
import ratpack.guice.BindingsSpec
import ratpack.handling.Chain
import ratpack.handling.RequestId
import ratpack.health.HealthCheck
import ratpack.logging.MDCInterceptor
import ratpack.registry.RegistrySpec
import ratpack.server.ServerConfigBuilder

fun ServerConfigBuilder.yaml(): ServerConfigBuilder {
    yaml("application.yaml")

    if (build().isDevelopment) {
        yaml("application-dev.yaml")
    }

    return this
}

fun BindingsSpec.mdc(): BindingsSpec = bindInstance(MDCInterceptor.withInit { e ->
    e.maybeGet(RequestId::class.java).ifPresent { requestId ->
        MDC.put("requestId", requestId.toString())
    }
})
fun RegistrySpec.health(): RegistrySpec = add(HealthCheck.of("application") { Promise.value(HealthCheck.Result.healthy()) })
fun Chain.public(): Chain = files { it.dir("public") }
