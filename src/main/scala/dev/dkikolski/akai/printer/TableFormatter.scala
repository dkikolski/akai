package dev.dkikolski.akai.printer

import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.RootOfTrust

import java.awt.RenderingHints.Key
import scala.annotation.tailrec
import scala.collection.immutable.TreeMap

object TableFormatter extends KeyDescriptionFormatter {
  private val ColumnWidth = 36

  private type TableRecord = (String, String, String)

  def render(keyDescription: KeyDescription, humanFriendlyFormat: Boolean): String = {
    val intermediateModel: Map[String, Any] =
      IntermediateModel(humanFriendlyFormat).convert(keyDescription)

    val generalInfoHeader = createTableHeader("Attestation info")
    val authListsHeader =
      createTableHeader("Authorization list", "Software enforced", "TEE enforced")
    val generalInfoRecords = createGeneralInforRecords(intermediateModel)
    val authListsRecords   = createAuthListsRecords(intermediateModel)

    generalInfoHeader
      .concat(generalInfoRecords)
      .concat(authListsHeader)
      .concat(authListsRecords)
      .map(formatRecord)
      .mkString("\n")
  }

  def createGeneralInforRecords(
      intermediateModel: Map[String, Any]
  ): Seq[TableRecord] = {
    (intermediateModel - "softwareEnforced" - "teeEnforced")
      .map((property, value) => (property, toStringValue(value), ""))
      .toSeq
  }

  def createAuthListsRecords(
      intermediateModel: Map[String, Any]
  ): Seq[TableRecord] = {
    val softwareEnforced: Map[String, Any] = flattenAuthList(
      intermediateModel("softwareEnforced").asInstanceOf[Map[String, Any]]
    )
    val teeEnforced: Map[String, Any] = flattenAuthList(
      intermediateModel("teeEnforced").asInstanceOf[Map[String, Any]]
    )
    (softwareEnforced zip teeEnforced)
      .map((left, right) => (left._1, toStringValue(left._2), toStringValue(right._2)))
      .toSeq
  }

  def flattenAuthList(authList: Map[String, Any]): Map[String, Any] = {
    val flattenRot = authList("rootOfTrust") match {
      case None         => Map[String, Any]()
      case m: Map[_, _] => m.map((k, v) => s"rootOfTrust.$k" -> v)
    }
    (authList - "rootOfTrust") concat flattenRot
  }

  private[this] def toStringValue(value: Any): String = value match {
    case None                    => ""
    case Some(x)                 => toStringValue(x)
    case bytes: Array[Byte]      => bytesToHex(bytes).mkString(" ")
    case collection: Iterable[_] => collection.mkString(", ")
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
