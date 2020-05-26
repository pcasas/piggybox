package parts.code.piggybox.extensions

import ratpack.server.ServerConfigBuilder

fun ServerConfigBuilder.yaml(): ServerConfigBuilder {
    yaml("application.yaml")

    if (build().isDevelopment) {
        yaml("application-dev.yaml")
    }

    return this
}
