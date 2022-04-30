package dev.dkikolski.akai.output

import dev.dkikolski.akai.schema.AttestationApplicationId
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.RootOfTrust

import scala.annotation.tailrec
import dev.dkikolski.akai.schema.AttestationPackageInfo

private[output] object TableRenderer extends KeyDescriptionRenderer {
  private val ColumnWidth = 36

  private type TableRecord = (String, String, String)

  def render(keyDescription: FormattableKeyDescription): String = {
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

  def buildGeneralInfoRecords(kd: FormattableKeyDescription): Seq[TableRecord] = {
    Seq(
      ("Attestation version", kd.attestationVersion, ""),
      ("Attestation Security Level", kd.attestationSecurityLevel, ""),
      ("Keymaster Version", kd.keymasterVersion, ""),
      ("Keymaster Security Level", kd.keymasterSecurityLevel, ""),
      ("Attestation Challenge", kd.attestationChallenge, ""),
      ("Unique Id", kd.uniqueId, "")
    )
  }

  def buildAuthListRecords(
      soft: FormattableAuthorizationList,
      tee: FormattableAuthorizationList
  ): Seq[TableRecord] = {
    Seq(
      ("Purpose", soft.purpose, tee.purpose),
      ("Algorithm", soft.algorithm, tee.algorithm),
      ("Key Size", soft.keySize, tee.keySize),
      ("Digest", soft.digest, tee.digest),
      ("Padding", soft.padding, tee.padding),
      ("EC Curve", soft.ecCurve, tee.ecCurve),
      ("RSA Public Exponent", soft.rsaPublicExponent, tee.rsaPublicExponent),
      ("Rollback Resistance", soft.rollbackResistance, tee.rollbackResistance),
      ("Active DateTime", soft.activeDateTime, tee.activeDateTime),
      (
        "Origination Expire DateTime",
        soft.originationExpireDateTime,
        tee.originationExpireDateTime
      ),
      ("Usage Expire DateTime", soft.usageExpireDateTime, tee.usageExpireDateTime),
      ("NoAuth Required", soft.noAuthRequired, tee.noAuthRequired),
      ("User Auth Type", soft.userAuthType, tee.userAuthType),
      ("Auth Timeout", soft.authTimeout, tee.authTimeout),
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
      ("Creation DateTime", soft.creationDateTime, tee.creationDateTime),
      ("Origin", soft.origin, tee.origin),
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
    )
  }

  def buildRootOfTrustRecords(
      soft: FormattableRootOfTrust,
      tee: FormattableRootOfTrust
  ): Seq[TableRecord] = {
    Seq(
      ("Verified Boot Key", soft.verifiedBootKey, tee.verifiedBootKey),
      ("Device Locked", soft.deviceLocked, tee.deviceLocked),
      ("Verified Boot State", soft.verifiedBootState, tee.verifiedBootState),
      ("Verified Boot Hash", soft.verifiedBootHash, tee.verifiedBootHash)
    )
  }

  def buildAttestationApplicationIdRecords(
      soft: FormattableAttestationApplicationId,
      tee: FormattableAttestationApplicationId
  ): Seq[TableRecord] = {
    Seq(
      ("Package Infos (name: version)", soft.packageInfos, tee.packageInfos),
      ("Digests", soft.signatureDigest, tee.signatureDigest)
    )
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
