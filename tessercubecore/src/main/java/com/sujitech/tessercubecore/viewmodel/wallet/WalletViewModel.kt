package com.sujitech.tessercubecore.viewmodel.wallet

import androidx.lifecycle.ViewModel
import com.sujitech.tessercubecore.common.collection.ObservableCollection
import com.sujitech.tessercubecore.data.DbContext
import com.sujitech.tessercubecore.data.RedPacketData
import com.sujitech.tessercubecore.data.WalletData
import io.reactivex.disposables.Disposable

class WalletViewModel : ViewModel() {
    private var totalRedPacket = emptyList<RedPacketData>()
    val wallets = ObservableCollection<WalletData>()
    val redPacket = ObservableCollection<RedPacketData>()

    private val walletSubscription: Disposable = DbContext.data.select(WalletData::class).get().observableResult().subscribe {
        wallets.clear()
        wallets.addAll(it)
        if (wallets.any() && currentWallet == null) {
            currentWallet = wallets.first()
        }
    }

    private val redPacketSubscription: Disposable = DbContext.data.select(RedPacketData::class).get().observableResult().subscribe {
        totalRedPacket = it.toList()
        updateRedPacket()
    }

    var currentWallet: WalletData? = null
        set(value) {
            field = value
//            updateRedPacket()
        }


    private fun updateRedPacket() {
        if (redPacket.any()) {
            redPacket.clear()
        }
        currentWallet?.let { value ->
            redPacket.addAll(totalRedPacket.filter {
                it.senderAddress == value.address || it.claimAddress == value.address
            })
        }
    }


    override fun onCleared() {
        super.onCleared()
        walletSubscription.dispose()
        redPacketSubscription.dispose()
    }
}