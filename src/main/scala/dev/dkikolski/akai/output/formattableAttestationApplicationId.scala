package dev.dkikolski.akai.output

import java.util.Formattable
import dev.dkikolski.akai.schema.AttestationApplicationId
import dev.dkikolski.akai.schema.AttestationPackageInfo

import UniversalConversions.convertToString

private[output] sealed trait FormattableAttestationApplicationId {
  def packageInfos: String
  def signatureDigest: String
}

private[output] class BlankAttestationApplicationId extends FormattableAttestationApplicationId {
  def packageInfos: String    = ""
  def signatureDigest: String = ""
}

private[output] class CommonAttestationApplicationId(
    appId: AttestationApplicationId
) extends FormattableAttestationApplicationId {

  def packageInfos: String = convertToString(
    appId.packageInfos.map(convertApplicationPackageInfoToString)
  )
  def signatureDigest: String = convertToString(appId.signatureDigest)

  private def convertApplicationPackageInfoToString(info: AttestationPackageInfo): String =
    s"('${info.packageName}': ${info.version})"
}
