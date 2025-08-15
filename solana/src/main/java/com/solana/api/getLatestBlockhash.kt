package com.solana.api

import com.solana.networking.RpcRequest
import com.solana.networking.SolanaResponseSerializer
import com.solana.networking.makeRequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class LatestBlockhashRequest : RpcRequest() {
    override val method: String = "getLatestBlockhash"
    override val params: JsonElement = buildJsonArray {
        add(buildJsonObject {
            put("commitment", "confirmed")
        })
    }
}

@Serializable
data class LatestBlockhashResponse(
    val blockhash: String,
    val lastValidBlockHeight: Long
)

internal fun LatestBlockhashSerializer() = SolanaResponseSerializer(LatestBlockhashResponse.serializer())

suspend fun Api.getLatestBlockhash(): Result<LatestBlockhashResponse> =
    router.makeRequestResult(LatestBlockhashRequest(), LatestBlockhashSerializer()).let { result ->
        @Suppress("UNCHECKED_CAST")
        if (result.isSuccess && result.getOrNull() == null)
            Result.failure(Error("Can not be null"))
        else result as Result<LatestBlockhashResponse>
    }

fun Api.getLatestBlockhash(onComplete: ((Result<LatestBlockhashResponse>) -> Unit)) {
    CoroutineScope(dispatcher).launch {
        onComplete(getLatestBlockhash())
    }
}