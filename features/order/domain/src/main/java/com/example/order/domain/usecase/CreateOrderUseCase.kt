package com.example.order.domain.usecase

import com.example.core.model.OrderStatus
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.PickupRequest
import com.example.order.domain.model.PickupType
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val clock: Clock,
) {
    operator fun invoke(
        userId: String,
        pickup: PickupRequest,
        comment: String,
        items: List<OrderItem>,
        total: Double,
    ): Order {
        val now = clock.instant()

        return Order(
            orderNumber = now.toEpochMilli().toString().takeLast(ORDER_NUMBER_LENGTH),
            userId = userId,
            createdAt = now,
            pickupType = pickup.toPickupType(),
            pickupAt = pickupAt(pickup = pickup, now = now),
            comment = comment,
            total = total,
            status = OrderStatus.IN_PROGRESS,
            items = items,
        )
    }

    private fun PickupRequest.toPickupType(): PickupType = when (this) {
        PickupRequest.Asap -> PickupType.ASAP
        is PickupRequest.Scheduled -> PickupType.SCHEDULED
    }

    private fun pickupAt(
        pickup: PickupRequest,
        now: Instant,
    ): Instant = when (pickup) {
        PickupRequest.Asap -> asapPickupAt(now)
        is PickupRequest.Scheduled -> pickup.at.atZone(clock.zone).toInstant()
    }

    private fun asapPickupAt(now: Instant): Instant {
        val withPrep = now.atZone(clock.zone).plusMinutes(ASAP_PREP_MINUTES)
        val minutesToHalfHour = (ASAP_ROUND_TO_MINUTES - withPrep.minute % ASAP_ROUND_TO_MINUTES) % ASAP_ROUND_TO_MINUTES
        return withPrep.plusMinutes(minutesToHalfHour.toLong()).truncatedTo(ChronoUnit.MINUTES).toInstant()
    }

    private companion object {
        const val ORDER_NUMBER_LENGTH = 6
        const val ASAP_PREP_MINUTES = 30L
        const val ASAP_ROUND_TO_MINUTES = 30
    }
}
