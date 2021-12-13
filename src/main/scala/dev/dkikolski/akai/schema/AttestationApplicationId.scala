package dev.dkikolski.akai.schema

final case class AttestationApplicationId(
    packageInfos: Set[AttestationPackageInfo],
    signatureDigest: Set[Array[Byte]]
)

final case class AttestationPackageInfo(
    packageName: String,
    version: Long
)
