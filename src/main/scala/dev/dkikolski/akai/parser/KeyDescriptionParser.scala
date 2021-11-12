package dev.dkikolski.akai.parser

import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema._
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Private
import org.bouncycastle.asn1.ASN1Sequence

object KeyDescriptionParser {
  val PurposeTag                     = 1
  val AlgorithmTag                   = 2
  val KeySizeTag                     = 3
  val DigestTag                      = 5
  val PaddingTag                     = 6
  val EcCurveTag                     = 10
  val RsaPublicExponentTag           = 200
  val RollbackResitanceTag           = 303
  val ActiveDateTimeTag              = 400
  val OriginationExpireDateTimeTag   = 401
  val UsageExpireDateTimeTag         = 402
  val NoAuthRequiredTag              = 503
  val UserAuthTypeTag                = 504
  val AuthTimeoutTag                 = 505
  val AllowWhileOnBodyTag            = 506
  val TrustedUserPresenceRequiredTag = 507
  val TrustedConfirmationRequiredTag = 508
  val UnlockedDeviceRequiredTag      = 509
  val AllApplicationsTag             = 600
  val ApplicationIdTag               = 601
  val CreationDateTimeTag            = 701
  val OriginTag                      = 702
  val RollbackResistantTag           = 703
  val RootOfTrustTag                 = 704
  val OsVersionTag                   = 705
  val OsPatchLevelTag                = 706
  val AttestationApplicationIdTag    = 709
  val AttestationIdBrandTag          = 710
  val AttestationIdDeviceTag         = 711
  val AttestationIdProductTag        = 712
  val AttestationIdSerialTag         = 713
  val AttestationIdImeiTag           = 714
  val AttestationIdMeidTag           = 715
  val AttestationIdManufacturerTag   = 716
  val AttestationIdModelTag          = 717
  val VendorPatchLevelTag            = 718
  val BootPatchLevelTag              = 719
  val DeviceUniqueAttestationTag     = 720

  def parse(asn1Seq: ASN1Sequence): Either[ParsingFailure, KeyDescription] = {
    val seq = ASN1TypeNarrowedSeq(asn1Seq)
    for {
      attestationVersion   <- seq.parseToIntFrom(0)
      attestationSecLvl    <- seq.parseToIntFrom(1).flatMap(parseSecurityLevel)
      keyMasterVersion     <- seq.parseToIntFrom(2)
      keyMasterSecLvl      <- seq.parseToIntFrom(3).flatMap(parseSecurityLevel)
      attestationChallenge <- seq.parseToBytesFrom(4)
      uniqueId             <- seq.parseToBytesFrom(5)
      softwareEnforced     <- seq.parseToTaggedObjectsFrom(6).flatMap(parseAuthorizationList)
      teeEnforced          <- seq.parseToTaggedObjectsFrom(7).flatMap(parseAuthorizationList)

      keyDescription = KeyDescription(
        attestationVersion,
        attestationSecLvl,
        keyMasterVersion,
        keyMasterSecLvl,
        attestationChallenge,
        uniqueId,
        softwareEnforced,
        teeEnforced
      )
    } yield keyDescription
  }

  private def parseAuthorizationList(
      taggedValues: ASN1TypeNarrowedTaggedObjects
  ): Either[ParsingFailure, AuthorizationList] = {
    for {
      purposes                  <- taggedValues.parseToIntSetFrom(PurposeTag)
      algorithm                 <- taggedValues.parseToIntFrom(AlgorithmTag)
      keySize                   <- taggedValues.parseToIntFrom(KeySizeTag)
      digests                   <- taggedValues.parseToIntSetFrom(DigestTag)
      paddings                  <- taggedValues.parseToIntSetFrom(PaddingTag)
      ecCurves                  <- taggedValues.parseToIntSetFrom(EcCurveTag)
      rsaPubExponent            <- taggedValues.parseToLongFrom(RsaPublicExponentTag)
      activeDateTime            <- taggedValues.parseToInstantFrom(ActiveDateTimeTag)
      originationExpireDateTime <- taggedValues.parseToInstantFrom(OriginationExpireDateTimeTag)
      usageExpireDateTime       <- taggedValues.parseToInstantFrom(UsageExpireDateTimeTag)
      userAuthType              <- taggedValues.parseToLongFrom(UserAuthTypeTag)
      userAuthTimeout           <- taggedValues.parseToDurationFrom(AuthTimeoutTag)
      applicationId             <- taggedValues.parseToBytesFrom(ApplicationIdTag)
      creationDateTime          <- taggedValues.parseToInstantFrom(CreationDateTimeTag)
      origin                    <- taggedValues.parseToIntFrom(OriginTag)
      rootOfTrust               <- getRootOfTrust(taggedValues)
      osVersion                 <- taggedValues.parseToIntFrom(OsVersionTag)
      osPatchLevel              <- taggedValues.parseToIntFrom(OsPatchLevelTag)
      attestationApplicationId  <- taggedValues.parseToBytesFrom(AttestationApplicationIdTag)
      attestationIdBrand        <- taggedValues.parseToBytesFrom(AttestationIdBrandTag)
      attestationIdDevice       <- taggedValues.parseToBytesFrom(AttestationIdDeviceTag)
      attestationIdProduct      <- taggedValues.parseToBytesFrom(AttestationIdProductTag)
      attestationIdSerial       <- taggedValues.parseToBytesFrom(AttestationIdSerialTag)
      attestationIdImei         <- taggedValues.parseToBytesFrom(AttestationIdImeiTag)
      attestationIdMeid         <- taggedValues.parseToBytesFrom(AttestationIdMeidTag)
      attestationIdManufacturer <- taggedValues.parseToBytesFrom(AttestationIdManufacturerTag)
      attestationIdModel        <- taggedValues.parseToBytesFrom(AttestationIdModelTag)
      vendorPatchLevel          <- taggedValues.parseToIntFrom(VendorPatchLevelTag)
      bootPatchLevel            <- taggedValues.parseToIntFrom(BootPatchLevelTag)

      rollbackResistance          = taggedValues.parseToBooleanFrom(RollbackResitanceTag)
      noAuthRiquired              = taggedValues.parseToBooleanFrom(NoAuthRequiredTag)
      allowWhileOnBody            = taggedValues.parseToBooleanFrom(AllowWhileOnBodyTag)
      trustedUserPresenceRequired = taggedValues.parseToBooleanFrom(TrustedUserPresenceRequiredTag)
      trustedConfirmationRequired = taggedValues.parseToBooleanFrom(TrustedConfirmationRequiredTag)
      unlockedDeviceRequired      = taggedValues.parseToBooleanFrom(UnlockedDeviceRequiredTag)
      allApplications             = taggedValues.parseToBooleanFrom(AllApplicationsTag)
      rollbackResistant           = taggedValues.parseToBooleanFrom(RollbackResistantTag)
      deviceUniqueAttestation     = taggedValues.parseToBooleanFrom(DeviceUniqueAttestationTag)

      authList = AuthorizationList(
        purpose = purposes,
        algorithm = algorithm,
        keySize = keySize,
        digest = digests,
        padding = paddings,
        ecCurve = ecCurves,
        rsaPublicExponent = rsaPubExponent,
        activeDateTime = activeDateTime,
        originationExpireDateTime = originationExpireDateTime,
        usageExpireDateTime = usageExpireDateTime,
        userAuthType = userAuthType,
        authTimeout = userAuthTimeout,
        applicationId = applicationId,
        creationDateTime = creationDateTime,
        origin = origin,
        rootOfTrust = rootOfTrust,
        osVersion = osVersion,
        osPatchLevel = osPatchLevel,
        attestationApplicationId = attestationApplicationId,
        attestationIdBrand = attestationIdBrand,
        attestationIdDevice = attestationIdDevice,
        attestationIdProduct = attestationIdProduct,
        attestationIdSerial = attestationIdSerial,
        attestationIdImei = attestationIdImei,
        attestationIdMeid = attestationIdMeid,
        attestationIdManufacturer = attestationIdManufacturer,
        attestationIdModel = attestationIdModel,
        vendorPatchLevel = vendorPatchLevel,
        bootPatchLevel = bootPatchLevel,
        rollbackResistance = taggedValues.parseToBooleanFrom(RollbackResitanceTag),
        noAuthRequired = taggedValues.parseToBooleanFrom(NoAuthRequiredTag),
        allowWhileOnBody = taggedValues.parseToBooleanFrom(AllowWhileOnBodyTag),
        trustedUserPresenceRequired =
          taggedValues.parseToBooleanFrom(TrustedUserPresenceRequiredTag),
        trustedConfirmationRequired =
          taggedValues.parseToBooleanFrom(TrustedConfirmationRequiredTag),
        unlockedDeviceRequired = taggedValues.parseToBooleanFrom(UnlockedDeviceRequiredTag),
        allApplications = taggedValues.parseToBooleanFrom(AllApplicationsTag)
      )
    } yield authList
  }

  private def getRootOfTrust(
      taggedValues: ASN1TypeNarrowedTaggedObjects
  ): Either[ParsingFailure, Option[RootOfTrust]] =
    taggedValues
      .parseToASN1TypeNarrowedSeqFrom(RootOfTrustTag)
      .flatMap(
        _.map(parseRootOfTrust)
          .getOrElse(Right(None))
      )

  private def parseRootOfTrust(
      seq: ASN1TypeNarrowedSeq
  ): Either[ParsingFailure, Option[RootOfTrust]] = {
    for {
      veryfiedBootKey   <- seq.parseToBytesFrom(0)
      deviceLocked      <- seq.parseToBooleanFrom(1)
      verifiedBootState <- seq.parseToIntFrom(2).flatMap(parseVerifiedBootState)
      verifiedBootHash  <- seq.parseToBytesOrEmptyFrom(3)
    } yield Some(RootOfTrust(veryfiedBootKey, deviceLocked, verifiedBootState, verifiedBootHash))
  }

  private def parseVerifiedBootState(candidate: Int): Either[ParsingFailure, VerifiedBootState] =
    candidate match {
      case 0 => Right(VerifiedBootState.Verified)
      case 1 => Right(VerifiedBootState.SelfSigned)
      case 2 => Right(VerifiedBootState.Unverified)
      case 3 => Right(VerifiedBootState.Failed)
      case x => Left(UnmatchedEnumeration(x, "VerifiedBootState"))
    }

  private def parseSecurityLevel(candidate: Int): Either[ParsingFailure, SecurityLevel] =
    candidate match {
      case 0 => Right(SecurityLevel.Software)
      case 1 => Right(SecurityLevel.TrustedEnvironment)
      case 2 => Right(SecurityLevel.StrongBox)
      case x => Left(UnmatchedEnumeration(x, "SecurityLevel"))
    }
}
