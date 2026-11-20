package com.example.core.ui

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.core.testing.MainDispatcherExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveEventsTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val lifecycleOwner = TestLifecycleOwner()
    private val frameClock = BroadcastFrameClock()
    private val channel = Channel<String>(Channel.BUFFERED)
    private val events = channel.receiveAsFlow()

    @Test
    fun `ObserveEvents when an event is sent before the screen starts then handles it once the screen starts`() = runTest {
        // Given
        val handled = mutableListOf<String>()
        lifecycleOwner.moveTo(Lifecycle.State.CREATED)
        setContent { ObserveEvents(events) { handled += it } }
        channel.send(EVENT)
        runCurrent()

        // When
        lifecycleOwner.moveTo(Lifecycle.State.STARTED)
        runCurrent()

        // Then
        assertThat(handled).containsExactly(EVENT)
    }

    @Test
    fun `ObserveEvents when an event is sent while the screen is not started then leaves it unhandled`() = runTest {
        // Given
        val handled = mutableListOf<String>()
        lifecycleOwner.moveTo(Lifecycle.State.CREATED)
        setContent { ObserveEvents(events) { handled += it } }

        // When
        channel.send(EVENT)
        runCurrent()

        // Then
        assertThat(handled).isEmpty()
    }

    @Test
    fun `ObserveEvents when onEvent changes then keeps its collection running`() = runTest {
        // Given
        var collections = 0
        val countedEvents = flow<String> {
            collections++
            awaitCancellation()
        }
        var onEvent by mutableStateOf<(String) -> Unit>({})
        lifecycleOwner.moveTo(Lifecycle.State.STARTED)
        setContent { ObserveEvents(countedEvents, onEvent) }

        // When
        onEvent = {}
        recompose()

        // Then
        assertThat(collections).isEqualTo(1)
    }

    @Test
    fun `ObserveEvents when onEvent changes then handles the next event with the latest one`() = runTest {
        // Given
        val handledByLatest = mutableListOf<String>()
        var onEvent by mutableStateOf<(String) -> Unit>({})
        lifecycleOwner.moveTo(Lifecycle.State.STARTED)
        setContent { ObserveEvents(events, onEvent) }
        onEvent = { handledByLatest += it }
        recompose()

        // When
        channel.send(EVENT)
        runCurrent()

        // Then
        assertThat(handledByLatest).containsExactly(EVENT)
    }

    private fun TestScope.setContent(content: @Composable () -> Unit) {
        val recomposer = Recomposer(backgroundScope.coroutineContext + frameClock)
        backgroundScope.launch(frameClock) { recomposer.runRecomposeAndApplyChanges() }
        Composition(NoNodesApplier, recomposer).setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content)
        }
        runCurrent()
    }

    private fun TestScope.recompose() {
        Snapshot.sendApplyNotifications()
        runCurrent()
        frameClock.sendFrame(FRAME_TIME_NANOS)
        runCurrent()
    }

    private class TestLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry.createUnsafe(this)

        override val lifecycle: Lifecycle = registry

        fun moveTo(state: Lifecycle.State) {
            registry.currentState = state
        }
    }

    private object NoNodesApplier : AbstractApplier<Unit>(Unit) {
        override fun insertTopDown(
            index: Int,
            instance: Unit,
        ) = Unit

        override fun insertBottomUp(
            index: Int,
            instance: Unit,
        ) = Unit

        override fun remove(
            index: Int,
            count: Int,
        ) = Unit

        override fun move(
            from: Int,
            to: Int,
            count: Int,
        ) = Unit

        override fun onClear() = Unit
    }

    private companion object {
        const val EVENT = "event"
        const val FRAME_TIME_NANOS = 0L
    }
}
