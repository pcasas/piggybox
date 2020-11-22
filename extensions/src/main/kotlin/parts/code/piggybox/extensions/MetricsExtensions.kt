package parts.code.piggybox.extensions

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory

fun MetricRegistry.cpu(): MetricRegistry {
    val mxBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
    gauge("process.cpu.load") { Gauge<Double> { mxBean.processCpuLoad } }
    gauge("system.cpu.load") { Gauge<Double> { mxBean.systemCpuLoad } }
    return this
}
