package dev.dkikolski.akai.schema

final case class AuthorizationList(
    algorithm: Option[Int],
    purpose: Set[Int],
    keySize: Option[Int],
    digest: Set[Int],
    padding: Set[Int],
    ecCurve: Option[Int],
    rsaPublicExponent: Option[Long],
    rollbackResistance: Boolean,
    activeDateTime: Option[Long],
    originationExpireDateTime: Option[Long],
    usageExpireDateTime: Option[Long],
    noAuthRequired: Boolean,
    userAuthType: Option[Long],
    authTimeout: Option[Long],
    allowWhileOnBody: Boolean,
    trustedUserPresenceRequired: Boolean,
    trustedConfirmationRequired: Boolean,
    unlockedDeviceRequired: Boolean,
    allApplications: Boolean,
    applicationId: Array[Byte],
    creationDateTime: Option[Long],
    origin: Option[Int],
    rootOfTrust: Option[RootOfTrust],
    osVersion: Option[Int],
    osPatchLevel: Option[Int],
    attestationApplicationId: Option[AttestationApplicationId],
    attestationIdBrand: Array[Byte],
    attestationIdDevice: Array[Byte],
    attestationIdProduct: Array[Byte],
    attestationIdSerial: Array[Byte],
    attestationIdImei: Array[Byte],
    attestationIdMeid: Array[Byte],
    attestationIdManufacturer: Array[Byte],
    attestationIdModel: Array[Byte],
    vendorPatchLevel: Option[Int],
    bootPatchLevel: Option[Int]
)
