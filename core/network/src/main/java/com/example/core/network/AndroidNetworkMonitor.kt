package com.example.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.example.core.common.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {

    private val connectivityManager: ConnectivityManager? = context.getSystemService()

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = this@AndroidNetworkMonitor.connectivityManager
        if (connectivityManager == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(connectivityManager.isDeviceOnline())
            }

            override fun onLost(network: Network) {
                trySend(connectivityManager.isDeviceOnline())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities,
            ) {
                trySend(connectivityManager.isDeviceOnline())
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        trySend(connectivityManager.isDeviceOnline())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
        .distinctUntilChanged()
        .conflate()
}

/**
 * The device is considered online if ANY active network:
 * - has INTERNET + VALIDATED
 * - and uses a real transport (Wi-Fi / Cellular / Ethernet)
 *
 * This avoids false positives from VPN networks that appear "validated"
 * while waiting for an underlying connection.
 */
private fun ConnectivityManager.isDeviceOnline(): Boolean = allNetworks.any { network ->
    val networkCapabilities = getNetworkCapabilities(network) ?: return@any false
    networkCapabilities.hasInternetValidated() && networkCapabilities.hasRealTransport()
}

private fun NetworkCapabilities.hasInternetValidated(): Boolean =
    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

private fun NetworkCapabilities.hasRealTransport(): Boolean =
    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
