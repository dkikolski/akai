package dev.dkikolski.akai.output

import dev.dkikolski.akai.schema.AttestationApplicationId
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.RootOfTrust

import java.awt.RenderingHints.Key
import scala.annotation.tailrec
import scala.collection.immutable.TreeMap
import dev.dkikolski.akai.schema.AttestationPackageInfo

private[output] class TableFormatter(
    val printInHumanFriendlyFormat: Boolean
) extends KeyDescriptionFormatter {
  private val ColumnWidth = 36

  private type TableRecord = (String, String, String)

  def render(keyDescription: KeyDescription): String = {
    val generalInfoHeader = createTableHeader("Attestation info")

    val authListsHeader =
      createTableHeader("Authorization list", "Software enforced", "TEE enforced")

    val rotHeader = createTableHeader("Root of Trust", "Software enforced", "TEE enforced")

    val attestationAppIdHeader =
      createTableHeader("Attestation App Id", "Software Enforced", "TEE enforced")

    val generalInfoRecords = buildGeneralInfoRecords(keyDescription)

    val rotRecords = buildRootOfTrustRecords(
      keyDescription.softwareEnforced.rootOfTrust,
      keyDescription.teeEnforced.rootOfTrust
    )

    val authListsRecords =
      buildAuthListRecords(keyDescription.softwareEnforced, keyDescription.teeEnforced)

    val attestationAppIdRecords = buildAttestationApplicationIdRecords(
      keyDescription.softwareEnforced.attestationApplicationId,
      keyDescription.teeEnforced.attestationApplicationId
    )

    generalInfoHeader
      .concat(generalInfoRecords)
      .concat(authListsHeader)
      .concat(authListsRecords)
      .concat(rotHeader)
      .concat(rotRecords)
      .concat(attestationAppIdHeader)
      .concat(attestationAppIdRecords)
      .map(formatRecord)
      .mkString("\n")
  }

  def buildGeneralInfoRecords(kd: KeyDescription): Seq[TableRecord] = {
    Seq(
      ("Attestation version", kd.attestationVersion, ""),
      ("Attestation Security Level", kd.attestationSecurityLevel, ""),
      ("Keymaster Version", kd.keymasterVersion, ""),
      ("Keymaster Security Level", kd.keymasterSecurityLevel, ""),
      ("Attestation Challenge", kd.attestationChallenge, ""),
      ("Unique Id", kd.uniqueId, "")
    ).map((header, v1, v2) => (header, toStringValue(v1), toStringValue(v2)))
  }

  def buildAuthListRecords(soft: AuthorizationList, tee: AuthorizationList): Seq[TableRecord] = {
    Seq(
      (
        "Purpose",
        humanFriendyConversion(soft.purpose, purposeFromInt),
        humanFriendyConversion(tee.purpose, purposeFromInt)
      ),
      (
        "Algorithm",
        humanFriendyConversion(soft.algorithm, algorithmFromInt),
        humanFriendyConversion(tee.algorithm, algorithmFromInt)
      ),
      ("Key Size", soft.keySize, tee.keySize),
      (
        "Digest",
        humanFriendyConversion(soft.digest, digestFromInt),
        humanFriendyConversion(tee.digest, digestFromInt)
      ),
      (
        "Padding",
        humanFriendyConversion(soft.padding, paddingFromInt),
        humanFriendyConversion(tee.padding, paddingFromInt)
      ),
      (
        "EC Curve",
        humanFriendyConversion(soft.ecCurve, ecCurveFromInt),
        humanFriendyConversion(tee.ecCurve, ecCurveFromInt)
      ),
      ("RSA Public Exponent", soft.rsaPublicExponent, tee.rsaPublicExponent),
      ("Rollback Resistance", soft.rollbackResistance, tee.rollbackResistance),
      (
        "Active DateTime",
        humanFriendyConversion(soft.activeDateTime, instantStringFromMillis),
        humanFriendyConversion(tee.activeDateTime, instantStringFromMillis)
      ),
      (
        "Origination Expire DateTime",
        humanFriendyConversion(soft.originationExpireDateTime, instantStringFromMillis),
        humanFriendyConversion(tee.originationExpireDateTime, instantStringFromMillis)
      ),
      (
        "Usage Expire DateTime",
        humanFriendyConversion(soft.usageExpireDateTime, instantStringFromMillis),
        humanFriendyConversion(tee.usageExpireDateTime, instantStringFromMillis)
      ),
      ("NoAuth Required", soft.noAuthRequired, tee.noAuthRequired),
      (
        "User Auth Type",
        humanFriendyUserAuthType(soft.userAuthType),
        humanFriendyUserAuthType(tee.userAuthType)
      ),
      (
        "Auth Timeout",
        humanFriendyConversion(soft.authTimeout, durationStringFromSeconds),
        humanFriendyConversion(tee.authTimeout, durationStringFromSeconds)
      ),
      ("Allow While On Body", soft.allowWhileOnBody, tee.allowWhileOnBody),
      (
        "Trusted User Presence Required",
        soft.trustedUserPresenceRequired,
        tee.trustedUserPresenceRequired
      ),
      (
        "Trusted Confirmation Required",
        soft.trustedConfirmationRequired,
        tee.trustedConfirmationRequired
      ),
      ("Unlocked Device Required", soft.unlockedDeviceRequired, tee.unlockedDeviceRequired),
      ("All Applications", soft.allApplications, tee.allApplications),
      ("Application Id", soft.applicationId, tee.applicationId),
      (
        "Creation DateTime",
        humanFriendyConversion(soft.creationDateTime, instantStringFromMillis),
        humanFriendyConversion(tee.creationDateTime, instantStringFromMillis)
      ),
      (
        "Origin",
        humanFriendyConversion(soft.origin, originFromInt),
        humanFriendyConversion(tee.origin, originFromInt)
      ),
      ("OS Version", soft.osVersion, tee.osVersion),
      ("OS Patch Level", soft.osPatchLevel, tee.osPatchLevel),
      ("Attestation Id Brand", soft.attestationIdBrand, tee.attestationIdBrand),
      ("Attestation Id Device", soft.attestationIdDevice, tee.attestationIdDevice),
      ("Attestation Id Product", soft.attestationIdProduct, tee.attestationIdProduct),
      ("Attestation Id Serial", soft.attestationIdSerial, tee.attestationIdSerial),
      ("Attestation Id Imei", soft.attestationIdImei, tee.attestationIdImei),
      ("Attestation Id Meid", soft.attestationIdMeid, tee.attestationIdMeid),
      (
        "Attestation ID Manufacturer",
        soft.attestationIdManufacturer,
        tee.attestationIdManufacturer
      ),
      ("Attestation ID Model", soft.attestationIdModel, tee.attestationIdModel),
      ("Vendor Patch Level", soft.vendorPatchLevel, tee.vendorPatchLevel),
      ("Boot Patch Level", soft.bootPatchLevel, tee.bootPatchLevel)
    ).map((header, v1, v2) => (header, toStringValue(v1), toStringValue(v2)))
  }

  def buildRootOfTrustRecords(
      soft: Option[RootOfTrust],
      tee: Option[RootOfTrust]
  ): Seq[TableRecord] = {
    Seq(
      ("Verified Boot Key", soft.map(_.verifiedBootKey), tee.map(_.verifiedBootKey)),
      ("Device Locked", soft.map(_.deviceLocked), tee.map(_.deviceLocked)),
      ("Verified Boot State", soft.map(_.verifiedBootState), tee.map(_.verifiedBootState)),
      ("Verified Boot Hash", soft.map(_.verifiedBootHash), tee.map(_.verifiedBootHash))
    ).map((header, v1, v2) => (header, toStringValue(v1), toStringValue(v2)))
  }

  def buildAttestationApplicationIdRecords(
      soft: Option[AttestationApplicationId],
      tee: Option[AttestationApplicationId]
  ): Seq[TableRecord] = {
    val packageInfoToString: AttestationPackageInfo => String = info => s"('${info.packageName}': ${info.version})"  
    val softPackageInfos = soft.map(_.packageInfos).map(_.map(packageInfoToString))
    val teePackageInfos = tee.map(_.packageInfos).map(_.map(packageInfoToString))
    val softDigests = soft.map(_.signatureDigest)
    val teeDigests  = tee.map(_.signatureDigest)

    Seq(
      ("Package Infos (name: version)", softPackageInfos, teePackageInfos),
      ("Digests", softDigests, teeDigests)
    ).map((header, v1, v2) => (header, toStringValue(v1), toStringValue(v2)))
  }

  private[this] def humanFriendyConversion[A <: Any](
      values: Iterable[A],
      conversion: A => String
  ): Iterable[String] = {
    if (printInHumanFriendlyFormat) values.map(conversion(_))
    else values.map(toStringValue)
  }

  private[this] def humanFriendyUserAuthType[A <: Any](value: Option[Long]): Option[Any] = {
    if (printInHumanFriendlyFormat) value.map(userAuthTypeFromLong)
    else value
  }

  private[this] def toStringValue(value: Any): String = value match {
    case None                    => ""
    case Some(x)                 => toStringValue(x)
    case bytes: Array[Byte]      => bytesToHex(bytes).mkString(" ")
    case collection: Iterable[_] => collection.map(toStringValue).mkString(", ")
    case other                   => other.toString
  }

  private[this] def createTableHeader(
      a: String,
      b: String = "",
      c: String = ""
  ): List[TableRecord] = {
    val hline = "-" * ColumnWidth
    List(
      (hline, hline, hline),
      (a, b, c),
      (hline, hline, hline)
    )
  }

  private[this] def formatRecord(record: TableRecord): String = {
    def hfillCell(s: String): String = s + " " * (ColumnWidth - s.length)

    def trimToColumnWidht(s: String): (String, String) = {
      if (s.length >= ColumnWidth) (s.substring(0, ColumnWidth), s.substring(ColumnWidth).trim)
      else if (s.length > 0 && s.length < ColumnWidth) (s, "")
      else ("", "")
    }

    @tailrec
    def go(a: String, b: String, c: String, sb: StringBuilder): String = {
      if (a.isEmpty && b.isEmpty && c.isEmpty) sb.toString
      else {
        val (ah, at) = trimToColumnWidht(a)
        val (bh, bt) = trimToColumnWidht(b)
        val (ch, ct) = trimToColumnWidht(c)
        val endline  = if (!at.isEmpty || !bt.isEmpty || !ct.isEmpty) "\n" else ""
        val nextLine = sb
          .append(hfillCell(ah))
          .append(" | ")
          .append(hfillCell(bh))
          .append(" | ")
          .append(hfillCell(ch))
          .append(endline)
        go(at, bt, ct, nextLine)
      }
    }
    go(record._1, record._2, record._3, new StringBuilder())
  }
}
