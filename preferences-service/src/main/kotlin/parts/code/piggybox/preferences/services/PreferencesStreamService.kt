package parts.code.piggybox.preferences.services

import javax.inject.Inject
import org.apache.kafka.streams.KafkaStreams
import ratpack.service.Service
import ratpack.service.StartEvent
import ratpack.service.StopEvent

class PreferencesStreamService @Inject constructor(
    private val streams: KafkaStreams
) : Service {

    override fun onStart(event: StartEvent?) {
        streams.start()
    }

    override fun onStop(event: StopEvent?) {
        streams.close()
    }
}
