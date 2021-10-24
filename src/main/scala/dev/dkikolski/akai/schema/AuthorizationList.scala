package dev.dkikolski.akai.schema

import java.time.Instant
import java.time.Duration

case class AuthorizationList(
    val purpose: Set[Int],
    val algorithm: Option[Int],
    val keySize: Option[Int],
    val digest: Set[Int],
    val padding: Set[Int],
    val ecCurve: Set[Int],
    val rsaPublicExponent: Option[Long],
    val rollbackResistance: Boolean,
    val activeDateTime: Option[Instant],
    val originationExpireDateTime: Option[Instant],
    val usageExpireDateTime: Option[Instant],
    val noAuthRequired: Boolean,
    val userAuthType: Option[Long],
    val authTimeout: Option[Duration],
    val allowWhileOnBody: Boolean,
    val trustedUserPresenceRequired: Boolean,
    val trustedConfirmationRequired: Boolean,
    val unlockedDeviceRequired: Boolean,
    val allApplications: Boolean,
    val applicationId: Array[Byte],
    val creationDateTime: Option[Instant],
    val origin: Option[Int],
    val rootOfTrust: Option[RootOfTrust],
    val osVersion: Option[Int],
    val osPatchLevel: Option[Int],
    val attestationApplicationId: Array[Byte],
    val attestationIdBrand: Array[Byte],
    val attestationIdDevice: Array[Byte],
    val attestationIdProduct: Array[Byte],
    val attestationIdSerial: Array[Byte],
    val attestationIdImei: Array[Byte],
    val attestationIdMeid: Array[Byte],
    val attestationIdManufacturer: Array[Byte],
    val attestationIdModel: Array[Byte],
    val vendorPatchLevel: Option[Int],
    val bootPatchLevel: Option[Int]
)
