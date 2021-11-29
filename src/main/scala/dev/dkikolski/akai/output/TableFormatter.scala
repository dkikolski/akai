package dev.dkikolski.akai.output

import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.RootOfTrust

import java.awt.RenderingHints.Key
import scala.annotation.tailrec
import scala.collection.immutable.TreeMap

private[output] object TableFormatter extends KeyDescriptionFormatter {
  private val ColumnWidth = 36

  private type TableRecord = (String, String, String)

  def render(keyDescription: KeyDescription, humanFriendlyFormat: Boolean): String = {
    val intermediateModel: Seq[(String, Any)] =
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
      intermediateModel: Seq[(String, Any)]
  ): Seq[TableRecord] = {
    intermediateModel
      .filterNot((key, value) => key.equals("softwareEnforced") || key.equals("teeEnforced"))
      .map((property, value) => (property, toStringValue(value), ""))
      .toSeq
  }

  def createAuthListsRecords(
      intermediateModel: Seq[(String, Any)]
  ): Seq[TableRecord] = {
    val softwareEnforced: Seq[(String, Any)] = flattenAuthList(
      intermediateModel
        .find((key, value) => key.equals("softwareEnforced"))
        .map((key, value) => value.asInstanceOf[Seq[(String, Any)]])
        .getOrElse(Seq())
    )
    val teeEnforced: Seq[(String, Any)] = flattenAuthList(
      intermediateModel
        .find((key, value) => key.equals("teeEnforced"))
        .map((key, value) => value.asInstanceOf[Seq[(String, Any)]])
        .getOrElse(Seq())
    )
    (softwareEnforced zip teeEnforced)
      .map((left, right) => (left._1, toStringValue(left._2), toStringValue(right._2)))
      .toSeq
  }

  def flattenAuthList(authList: Seq[(String, Any)]): Seq[(String, Any)] = {
    val flattenRot = authList
      .filter((key, value) => key.equals("rootOfTrust"))
      .map((key, value) => value.asInstanceOf[Seq[(String, Any)]])
      .headOption
      .map(seq => seq.map((key, value) => s"rootOfTrust.$key" -> value))
      .getOrElse(Seq())

    // val flattenRot = authList("rootOfTrust") match {
    //   case None         => Map[String, Any]()
    //   case m: Map[_, _] => m.map((k, v) => s"rootOfTrust.$k" -> v)
    // }

    authList.filterNot((key, value) => key.equals("rootOfTrust")) concat flattenRot
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
