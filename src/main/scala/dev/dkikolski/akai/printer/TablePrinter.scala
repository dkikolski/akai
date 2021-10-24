package dev.dkikolski.akai.printer

import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.AuthorizationList
import scala.annotation.tailrec
import scala.collection.immutable.TreeMap
import java.awt.RenderingHints.Key
import dev.dkikolski.akai.schema.RootOfTrust

object TablePrinter {
  private val ColumnWidth = 36

  def render(kd: KeyDescription): String = {
    def extractSingle(extractFun: KeyDescription => Any): (String, String) =
      (extractFun(kd).toString, "")

    def extractBoth[A](
        extractFun: AuthorizationList => A, 
        toStr: A => String = (a: A) => a.toString
    ): (String, String) =
      (toStr(extractFun(kd.softwareEnforced)), toStr(extractFun(kd.teeEnforced)))

    def extractBothRoT[A](
        extractFun: RootOfTrust => A,
        toStr: A => String = (a: A) => a.toString
    ): (String, String) = {
      (
        optionToString(kd.softwareEnforced.rootOfTrust.map(extractFun).map(toStr)),
        optionToString(kd.teeEnforced.rootOfTrust.map(extractFun).map(toStr))
      )
    }

    val attestationInfo: Seq[(String, (String, String))] = Seq(
      "Attestation Version"        -> extractSingle(_.attestationVersion),
      "Attestation Security Level" -> extractSingle(_.attestationSecurityLevel),
      "Keymaster Version"          -> extractSingle(_.keymasterVersion),
      "Keymaster Security Level"   -> extractSingle(_.keymasterSecurityLevel),
      "Attestation Challenge"      -> extractSingle(_.attestationChallenge),
      "Unique ID"                  -> extractSingle(_.uniqueId)
    )

    val authorizationList: Seq[(String, (String, String))] = Seq(
      "Purpose"   -> extractBoth(_.purpose, it => setToString(it.map(purposeFromInt))),
      "Algorithm" -> extractBoth(_.algorithm, it => optionToString(it.map(algorithmFromInt))),
      "Key Size"  -> extractBoth(_.keySize, optionToString),
      "Digest"    -> extractBoth(_.digest, it => setToString(it.map(digestFromInt))),
      "Padding"   -> extractBoth(_.padding, it => setToString(it.map(paddingFromInt))),
      "EC Curve"  -> extractBoth(_.ecCurve, it => setToString(it.map(ecCurveFromInt))),
      "RSA public Exponent"            -> extractBoth(_.rsaPublicExponent, optionToString),
      "Rollback Resistance"            -> extractBoth(_.rollbackResistance),
      "Active Date Time"               -> extractBoth(_.activeDateTime, optionToString),
      "Origination Expire Date Time"   -> extractBoth(_.originationExpireDateTime, optionToString),
      "Usage Expire Date Time"         -> extractBoth(_.usageExpireDateTime, optionToString),
      "No Auth Required"               -> extractBoth(_.noAuthRequired),
      "User Auth Type"                 -> extractBoth(_.userAuthType, userAuthTypeToString),
      "Auth Timeout"                   -> extractBoth(_.authTimeout, optionToString),
      "Allow While On Body"            -> extractBoth(_.allowWhileOnBody),
      "Trusted User Presence Required" -> extractBoth(_.trustedUserPresenceRequired),
      "Trusted Confirmation Required"  -> extractBoth(_.trustedConfirmationRequired),
      "Unlocked Device Required"       -> extractBoth(_.unlockedDeviceRequired),
      "All Applications"               -> extractBoth(_.allApplications),
      "Application ID"                 -> extractBoth(_.applicationId, bytesToString),
      "Creation Date Time"             -> extractBoth(_.creationDateTime, optionToString),
      "Origin" -> extractBoth(_.origin, it => optionToString(it.map(originFromInt))),
      "Root of Trust: Verified Boot Key"   -> extractBothRoT(_.verifiedBootKey, bytesToHex),
      "Root of Trust: Device Locked"       -> extractBothRoT(_.deviceLocked),
      "Root of Trust: Verified Boot State" -> extractBothRoT(_.verifiedBootState),
      "Root of Trust: Verified Boot Hash"  -> extractBothRoT(_.verifiedBootHash, bytesToString),
      "OS Version"                         -> extractBoth(_.osVersion, optionToString),
      "OS Patch Level"                     -> extractBoth(_.osPatchLevel, optionToString),
      "Attestation Application ID"  -> extractBoth(_.attestationApplicationId, bytesToString),
      "Attestation ID Brand"        -> extractBoth(_.attestationIdBrand, bytesToString),
      "Attestation ID Device"       -> extractBoth(_.attestationIdDevice, bytesToString),
      "Attestation ID Product"      -> extractBoth(_.attestationIdProduct, bytesToString),
      "Attestation ID Serial"       -> extractBoth(_.attestationIdSerial, bytesToString),
      "Attestation ID IMEI"         -> extractBoth(_.attestationIdImei, bytesToString),
      "Attestation ID MEID"         -> extractBoth(_.attestationIdMeid, bytesToString),
      "Attestation ID Manufacturer" -> extractBoth(_.attestationIdManufacturer, bytesToString),
      "Attestation ID Model"        -> extractBoth(_.attestationIdModel, bytesToString),
      "Vendor Patch level"          -> extractBoth(_.vendorPatchLevel, optionToString),
      "Boot patch level"            -> extractBoth(_.bootPatchLevel, optionToString)
    )

    val attestationInfoHeaderRecords =
      headerRecord("Attestation info", " ", " ")

    val authrizationHeaderRecords =
      headerRecord("Authorization list", "Software enforced", "TEE enforced")

    attestationInfoHeaderRecords
      .map(formatRecord)
      .concat(attestationInfo.map(formatRecord))
      .concat(authrizationHeaderRecords.map(formatRecord))
      .concat(authorizationList.map(formatRecord))
      .mkString("\n")
  }

  private[this] def optionToString(o: Option[_]): String =
    if (o.isEmpty) "<empty>"
    else o.get.toString

  private[this] def setToString(s: Set[_]): String =
    if (s.isEmpty) "<empty>"
    else s.mkString(", ")

  private[this] def bytesToString(bytes: Array[Byte]) =
    if (bytes.isEmpty) "<empty>"
    else String.valueOf(bytes.map(b => if (b >= 32) b.toChar else '.'))

  private[this] def bytesToHex(bytes: Array[Byte]) =
    if (bytes.isEmpty) "<empty>"
    else bytes.map(b => String.format("%02x", b)).mkString(" ")

  private[this] def userAuthTypeToString(o: Option[Long]) =
    optionToString(o.map(userAuthTypeFromLong).map(setToString))

  private[this] def headerRecord(
      a: String,
      b: String,
      c: String
  ): List[(String, String, String)] = {
    val hline = "-" * ColumnWidth
    List(
      (hline, hline, hline),
      (a, b, c),
      (hline, hline, hline)
    )
  }

  private[this] def formatRecord(r: (String, (String, String))): String = {
    formatRecord((r._1, r._2._1, r._2._2))
  }

  private[this] def formatRecord(record: (String, String, String)): String = {
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