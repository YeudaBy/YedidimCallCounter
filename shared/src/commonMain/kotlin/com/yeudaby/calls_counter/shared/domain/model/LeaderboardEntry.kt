package com.yeudaby.calls_counter.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val userId: String,
    val name: String,
    val callsCount: Int,
    val rank: Int,
    val region: Region
)
