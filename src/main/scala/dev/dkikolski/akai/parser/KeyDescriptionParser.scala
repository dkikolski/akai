package dev.dkikolski.akai.parser

import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema._
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Private
import org.bouncycastle.asn1.ASN1Sequence

object KeyDescriptionParser {
  val VeryfiedBootKeyIndex   = 0
  val DeviceLockedIndex      = 1
  val VerifiedBootStateIndex = 2
  val VerifiedBootHashIndex  = 3

  val AttestationVersionIndex   = 0
  val AttestationSecLevelIndex  = 1
  val KeyMasterVersionIndex     = 2
  val KeyMasterSecLevelIndex    = 3
  val AttestationChallengeIndex = 4
  val UniqueIdIndex             = 5
  val SoftwareEnforcedIndex     = 6
  val TeeEnforcedIndex          = 7

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
      attestationVersion <- seq.getIntAt(AttestationVersionIndex)
      attestationSecLevel <- seq
        .getIntAt(AttestationSecLevelIndex)
        .flatMap(parseSecurityLevel)
      keyMasterVersion     <- seq.getIntAt(KeyMasterVersionIndex)
      keyMasterSecLevel    <- seq.getIntAt(KeyMasterSecLevelIndex).flatMap(parseSecurityLevel)
      attestationChallenge <- seq.getBytesAt(AttestationChallengeIndex)
      uniqueId             <- seq.getBytesAt(UniqueIdIndex)
      softwareEnforced <- seq
        .getTaggedObjectsAt(SoftwareEnforcedIndex)
        .flatMap(parseAuthorizationList)
      teeEnforced <- seq.getTaggedObjectsAt(TeeEnforcedIndex).flatMap(parseAuthorizationList)

      keyDescription = KeyDescription(
        attestationVersion,
        attestationSecLevel,
        keyMasterVersion,
        keyMasterSecLevel,
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
      purposes                  <- taggedValues.getIntSetOrEmptyAt(PurposeTag)
      algorithm                 <- taggedValues.getOptionalIntAt(AlgorithmTag)
      keySize                   <- taggedValues.getOptionalIntAt(KeySizeTag)
      digests                   <- taggedValues.getIntSetOrEmptyAt(DigestTag)
      paddings                  <- taggedValues.getIntSetOrEmptyAt(PaddingTag)
      ecCurves                  <- taggedValues.getIntSetOrEmptyAt(EcCurveTag)
      rsaPubExponent            <- taggedValues.getOptionalLongAt(RsaPublicExponentTag)
      activeDateTime            <- taggedValues.getOptionalInstantAt(ActiveDateTimeTag)
      originationExpireDateTime <- taggedValues.getOptionalInstantAt(OriginationExpireDateTimeTag)
      usageExpireDateTime       <- taggedValues.getOptionalInstantAt(UsageExpireDateTimeTag)
      userAuthType              <- taggedValues.getOptionalLongAt(UserAuthTypeTag)
      userAuthTimeout           <- taggedValues.getOptionalLongAt(AuthTimeoutTag)
      applicationId             <- taggedValues.getBytesOrEmptyAt(ApplicationIdTag)
      creationDateTime          <- taggedValues.getOptionalInstantAt(CreationDateTimeTag)
      origin                    <- taggedValues.getOptionalIntAt(OriginTag)
      rootOfTrust               <- parseToRootOfTrustFrom(taggedValues)
      osVersion                 <- taggedValues.getOptionalIntAt(OsVersionTag)
      osPatchLevel              <- taggedValues.getOptionalIntAt(OsPatchLevelTag)
      attestationApplicationId  <- taggedValues.getBytesOrEmptyAt(AttestationApplicationIdTag)
      attestationIdBrand        <- taggedValues.getBytesOrEmptyAt(AttestationIdBrandTag)
      attestationIdDevice       <- taggedValues.getBytesOrEmptyAt(AttestationIdDeviceTag)
      attestationIdProduct      <- taggedValues.getBytesOrEmptyAt(AttestationIdProductTag)
      attestationIdSerial       <- taggedValues.getBytesOrEmptyAt(AttestationIdSerialTag)
      attestationIdImei         <- taggedValues.getBytesOrEmptyAt(AttestationIdImeiTag)
      attestationIdMeid         <- taggedValues.getBytesOrEmptyAt(AttestationIdMeidTag)
      attestationIdManufacturer <- taggedValues.getBytesOrEmptyAt(AttestationIdManufacturerTag)
      attestationIdModel        <- taggedValues.getBytesOrEmptyAt(AttestationIdModelTag)
      vendorPatchLevel          <- taggedValues.getOptionalIntAt(VendorPatchLevelTag)
      bootPatchLevel            <- taggedValues.getOptionalIntAt(BootPatchLevelTag)

      rollbackResistance          = taggedValues.getBooleanAt(RollbackResitanceTag)
      noAuthRiquired              = taggedValues.getBooleanAt(NoAuthRequiredTag)
      allowWhileOnBody            = taggedValues.getBooleanAt(AllowWhileOnBodyTag)
      trustedUserPresenceRequired = taggedValues.getBooleanAt(TrustedUserPresenceRequiredTag)
      trustedConfirmationRequired = taggedValues.getBooleanAt(TrustedConfirmationRequiredTag)
      unlockedDeviceRequired      = taggedValues.getBooleanAt(UnlockedDeviceRequiredTag)
      allApplications             = taggedValues.getBooleanAt(AllApplicationsTag)
      rollbackResistant           = taggedValues.getBooleanAt(RollbackResistantTag)
      deviceUniqueAttestation     = taggedValues.getBooleanAt(DeviceUniqueAttestationTag)

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
        rollbackResistance = rollbackResistance,
        noAuthRequired = noAuthRiquired,
        allowWhileOnBody = allowWhileOnBody,
        trustedUserPresenceRequired = trustedUserPresenceRequired,
        trustedConfirmationRequired = trustedConfirmationRequired,
        unlockedDeviceRequired = deviceUniqueAttestation,
        allApplications = allApplications
      )
    } yield authList
  }

  private def parseToRootOfTrustFrom(
      taggedValues: ASN1TypeNarrowedTaggedObjects
  ): Either[ParsingFailure, Option[RootOfTrust]] =
    taggedValues
      .getOptionalASN1SeqAt(RootOfTrustTag)
      .flatMap(
        _.map(parseRootOfTrust)
          .getOrElse(Right(None))
      )

  private def parseRootOfTrust(
      seq: ASN1TypeNarrowedSeq
  ): Either[ParsingFailure, Option[RootOfTrust]] = {
    for {
      veryfiedBootKey <- seq.getBytesAt(VeryfiedBootKeyIndex)
      deviceLocked    <- seq.getBooleanAt(DeviceLockedIndex)
      verifiedBootState <- seq
        .getIntAt(VerifiedBootStateIndex)
        .flatMap(parseVerifiedBootState)
      verifiedBootHash <- seq.getBytesOrEmptyAt(VerifiedBootHashIndex)
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
