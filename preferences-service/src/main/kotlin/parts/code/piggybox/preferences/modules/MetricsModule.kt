package parts.code.piggybox.preferences.modules

import com.codahale.metrics.MetricRegistry
import com.google.inject.AbstractModule
import com.google.inject.Provides
import javax.inject.Singleton
import parts.code.piggybox.extensions.cpu

class MetricsModule : AbstractModule() {

    override fun configure() {}

    @Provides
    @Singleton
    fun provideMetricRegistry(): MetricRegistry {
        val metrics = MetricRegistry()
        metrics.cpu()
        return metrics
    }
}
