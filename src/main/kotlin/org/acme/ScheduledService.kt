package org.acme

import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import io.quarkus.vertx.ConsumeEvent
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class ScheduledService(
    val vertxEventBus: EventBus
) {

    fun onStart(@Observes event: StartupEvent) {
        Log.info("Registering codec")
        vertxEventBus.registerDefaultCodec(PasswordResetRequest::class.java, PasswordResetCodec())
    }

    @Scheduled(every = "10s")
    fun receiveEvents() {
        Log.info("Scheduled service executed")
        vertxEventBus.request<String>("foobar", PasswordResetRequest("uu", "uu"))
    }
}

/**
 * If you comment the  service below, application starts successfully
 */
@ApplicationScoped
class PasswordResetEventConsumer {

    @ConsumeEvent("foobar")
    fun foobar(event: PasswordResetRequest) {
        Log.info("Recevied event: $event")
    }
}

interface TrackableEvent {
    val userHandle: String
    var receiptHandle: String?
}

open class LocalOnlyCodec<T>(
    private val id: String) : MessageCodec<T, T>
{
    override fun decodeFromWire(pos: Int, buffer: Buffer) = throw UnsupportedOperationException("Not implemented.")

    override fun systemCodecID(): Byte = -1

    override fun encodeToWire(buffer: Buffer?, s: T)  = throw UnsupportedOperationException("Not implemented.")

    override fun transform(s: T) = s

    override fun name() = "loc.$id"
}

class PasswordResetCodec: LocalOnlyCodec<PasswordResetRequest>("codecs.password-reset")
