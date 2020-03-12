package parts.code.piggybox.integration.tests

import java.time.Duration
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer

fun <K, V> KafkaConsumer<K, V>.lastRecord(key: String, clazz: Class<out SpecificRecord>): ConsumerRecord<K, V> =
    this.lastRecord(key, Duration.ofSeconds(30).toMillis(), clazz)

fun <K, V> KafkaConsumer<K, V>.lastRecord(key: String, timeoutMillis: Long, clazz: Class<out SpecificRecord>): ConsumerRecord<K, V> {
    val consumer = this
    var event: ConsumerRecord<K, V>? = null

    try {
        runBlocking {
            withTimeout(timeoutMillis) {
                while (true) {
                    delay(50)
                    val events = consumer.poll(Duration.ZERO).filter { it.key() == key }.toList()

                    if (events.isNotEmpty() && clazz.isInstance(events.last().value())) {
                        event = events.last()
                        break
                    }
                }
            }
        }
    } catch (e: TimeoutCancellationException) {
        throw IllegalStateException("No records found for key $key after $timeoutMillis milliseconds")
    }

    return event!!
}
