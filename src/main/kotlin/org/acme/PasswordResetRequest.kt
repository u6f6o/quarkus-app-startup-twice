package org.acme

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PasswordResetRequest(
    override val userHandle: String,
    val link: String,
    @Transient
    override var receiptHandle: String? = null,
): TrackableEvent