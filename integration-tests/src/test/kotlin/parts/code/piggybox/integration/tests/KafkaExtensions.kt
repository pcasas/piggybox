package parts.code.piggybox.integration.tests

import java.time.Duration
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer

fun <K, V> KafkaConsumer<K, V>.lastRecord(key: String): ConsumerRecord<K, V> =
    this.lastRecord(key, Duration.ofSeconds(30).toMillis())

fun <K, V> KafkaConsumer<K, V>.lastRecord(key: String, timeoutMillis: Long): ConsumerRecord<K, V> {
    val consumer = this
    var event: ConsumerRecord<K, V>? = null

    try {
        runBlocking {
            withTimeout(timeoutMillis) {
                while (true) {
                    delay(50)
                    val events = consumer.poll(Duration.ZERO).filter { it.key() == key }.toList()

                    if (events.isNotEmpty()) {
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
