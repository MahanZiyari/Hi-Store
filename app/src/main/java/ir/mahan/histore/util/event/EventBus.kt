package ir.mahan.histore.util.event

import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

object EventBus {

    private val events = MutableSharedFlow<Any>()
    val eventsFlow = events.asSharedFlow()

    suspend fun publish(event: Any) = events.emit(event)

    suspend inline fun <reified T>observe(crossinline onEventReceived: (T) -> Unit){
        eventsFlow.filterIsInstance<T>().collect {
            coroutineContext.ensureActive()
            onEventReceived(it)
        }
    }
}