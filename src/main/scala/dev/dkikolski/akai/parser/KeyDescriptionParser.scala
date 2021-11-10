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
      attestationVersion   <- seq.parseIntAt(0)
      attestationSecLvl    <- seq.parseIntAt(1).flatMap(parseSecurityLevel)
      keyMasterVersion     <- seq.parseIntAt(2)
      keyMasterSecLvl      <- seq.parseIntAt(3).flatMap(parseSecurityLevel)
      attestationChallenge <- seq.parseBytesAt(4)
      uniqueId             <- seq.parseBytesAt(5)
      softwareEnforced     <- seq.parseTaggedObjectsAt(6).flatMap(parseAuthorizationList)
      teeEnforced          <- seq.parseTaggedObjectsAt(7).flatMap(parseAuthorizationList)

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
      purposes                  <- taggedValues.getIntSet(PurposeTag)
      algorithm                 <- taggedValues.getInt(AlgorithmTag)
      keySize                   <- taggedValues.getInt(KeySizeTag)
      digests                   <- taggedValues.getIntSet(DigestTag)
      paddings                  <- taggedValues.getIntSet(PaddingTag)
      ecCurves                  <- taggedValues.getIntSet(EcCurveTag)
      rsaPubExponent            <- taggedValues.getLong(RsaPublicExponentTag)
      activeDateTime            <- taggedValues.getInstant(ActiveDateTimeTag)
      originationExpireDateTime <- taggedValues.getInstant(OriginationExpireDateTimeTag)
      usageExpireDateTime       <- taggedValues.getInstant(UsageExpireDateTimeTag)
      userAuthType              <- taggedValues.getLong(UserAuthTypeTag)
      userAuthTimeout           <- taggedValues.getDuration(AuthTimeoutTag)
      applicationId             <- taggedValues.getBytes(ApplicationIdTag)
      creationDateTime          <- taggedValues.getInstant(CreationDateTimeTag)
      origin                    <- taggedValues.getInt(OriginTag)
      rootOfTrust               <- getRootOfTrust(taggedValues)
      osVersion                 <- taggedValues.getInt(OsVersionTag)
      osPatchLevel              <- taggedValues.getInt(OsPatchLevelTag)
      attestationApplicationId  <- taggedValues.getBytes(AttestationApplicationIdTag)
      attestationIdBrand        <- taggedValues.getBytes(AttestationIdBrandTag)
      attestationIdDevice       <- taggedValues.getBytes(AttestationIdDeviceTag)
      attestationIdProduct      <- taggedValues.getBytes(AttestationIdProductTag)
      attestationIdSerial       <- taggedValues.getBytes(AttestationIdSerialTag)
      attestationIdImei         <- taggedValues.getBytes(AttestationIdImeiTag)
      attestationIdMeid         <- taggedValues.getBytes(AttestationIdMeidTag)
      attestationIdManufacturer <- taggedValues.getBytes(AttestationIdManufacturerTag)
      attestationIdModel        <- taggedValues.getBytes(AttestationIdModelTag)
      vendorPatchLevel          <- taggedValues.getInt(VendorPatchLevelTag)
      bootPatchLevel            <- taggedValues.getInt(BootPatchLevelTag)

      rollbackResistance          = taggedValues.getBoolean(RollbackResitanceTag)
      noAuthRiquired              = taggedValues.getBoolean(NoAuthRequiredTag)
      allowWhileOnBody            = taggedValues.getBoolean(AllowWhileOnBodyTag)
      trustedUserPresenceRequired = taggedValues.getBoolean(TrustedUserPresenceRequiredTag)
      trustedConfirmationRequired = taggedValues.getBoolean(TrustedConfirmationRequiredTag)
      unlockedDeviceRequired      = taggedValues.getBoolean(UnlockedDeviceRequiredTag)
      allApplications             = taggedValues.getBoolean(AllApplicationsTag)
      rollbackResistant           = taggedValues.getBoolean(RollbackResistantTag)
      deviceUniqueAttestation     = taggedValues.getBoolean(DeviceUniqueAttestationTag)

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
        rollbackResistance = taggedValues.getBoolean(RollbackResitanceTag),
        noAuthRequired = taggedValues.getBoolean(NoAuthRequiredTag),
        allowWhileOnBody = taggedValues.getBoolean(AllowWhileOnBodyTag),
        trustedUserPresenceRequired = taggedValues.getBoolean(TrustedUserPresenceRequiredTag),
        trustedConfirmationRequired = taggedValues.getBoolean(TrustedConfirmationRequiredTag),
        unlockedDeviceRequired = taggedValues.getBoolean(UnlockedDeviceRequiredTag),
        allApplications = taggedValues.getBoolean(AllApplicationsTag)
      )
    } yield authList
  }

  private def getRootOfTrust(
      taggedValues: ASN1TypeNarrowedTaggedObjects
  ): Either[ParsingFailure, Option[RootOfTrust]] =
    taggedValues
      .getASN1TypeNarrowedSeq(RootOfTrustTag)
      .flatMap(
        _.map(parseRootOfTrust)
          .getOrElse(Right(None))
      )

  private def parseRootOfTrust(
      tseq: ASN1TypeNarrowedSeq
  ): Either[ParsingFailure, Option[RootOfTrust]] = {
    for {
      veryfiedBootKey   <- tseq.parseBytesAt(0)
      deviceLocked      <- tseq.parseBooleanAt(1)
      verifiedBootState <- tseq.parseIntAt(2).flatMap(parseVerifiedBootState)
      verifiedBootHash  <- tseq.parseBytesOrEmptyAt(3)
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
