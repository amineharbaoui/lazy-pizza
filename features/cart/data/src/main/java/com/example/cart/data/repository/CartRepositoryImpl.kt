package com.example.cart.data.repository

import com.example.cart.data.datasource.CartLocalDataSource
import com.example.cart.data.datasource.CartTransferLocalDataSource
import com.example.cart.data.mapper.CartOwnerToKeyMapper
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.model.CartWriteResult
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartLocalDataSource,
    private val transferLocalDataSource: CartTransferLocalDataSource,
    private val ownerToKeyMapper: CartOwnerToKeyMapper,
) : CartRepository {

    override fun observeCart(owner: CartOwner): Flow<Cart> = localDataSource.observeCart(ownerKey = ownerToKeyMapper.map(owner))

    override suspend fun addItem(
        owner: CartOwner,
        item: CartItem,
    ) {
        localDataSource.addItem(ownerKey = ownerToKeyMapper.map(owner), item = item)
    }

    override suspend fun addItems(
        owner: CartOwner,
        items: List<CartItem>,
    ): CartWriteResult = try {
        localDataSource.addItems(ownerKey = ownerToKeyMapper.map(owner), items = items)
        CartWriteResult.Success
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        CartWriteResult.Failed
    }

    override suspend fun updateItemQuantity(
        owner: CartOwner,
        lineId: String,
        quantity: Int,
    ) {
        localDataSource.updateItemQuantity(ownerKey = ownerToKeyMapper.map(owner), lineId = lineId, quantity = quantity)
    }

    override suspend fun removeItem(
        owner: CartOwner,
        lineId: String,
    ) {
        localDataSource.removeItem(ownerKey = ownerToKeyMapper.map(owner), lineId = lineId)
    }

    override suspend fun clear(owner: CartOwner) {
        localDataSource.clear(ownerKey = ownerToKeyMapper.map(owner))
    }

    override suspend fun transferGuestCart(to: CartOwner.User) {
        transferLocalDataSource.transferCart(
            fromOwnerKey = ownerToKeyMapper.map(CartOwner.Guest),
            toOwnerKey = ownerToKeyMapper.map(to),
        )
    }
}
