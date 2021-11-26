package dev.dkikolski.akai.printer

import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.RootOfTrust
import dev.dkikolski.akai.schema.AuthorizationList
import java.io.StringWriter

object JsonFormatter extends KeyDescriptionFormatter {

  extension (str: String) def quoted: String = s"\"$str\""

  extension (str: String) def escapeQuotes: String = str.replace("\"", "\\\"")

  extension (bytes: Array[Byte])
    def hexString: String = bytesToHex(bytes).mkString("").quoted

  extension (seq: Iterable[String])
    def jsonString: String = seq.mkString("{", ",", "}")

  def render(keyDescription: KeyDescription, humanFriendlyFormat: Boolean): String = {
    IntermediateModel(humanFriendlyFormat)
      .convert(keyDescription)
      .map(toJsonMember)
      .jsonString
  }

  private def toJsonMember(entry: (Any, Any)): String =
    entry._1.toString.quoted + ":" + jsonValue(entry._2)

  private def jsonValue(value: Any): String = value match {
    case None                                 => "null"
    case Some(x)                              => jsonValue(x)
    case boolean: Boolean                     => boolean.toString
    case string: String                       => string.escapeQuotes.quoted
    case int: Int                             => int.toString
    case long: Long                           => long.toString
    case map: Map[_, _] if !map.isEmpty       => map.map(toJsonMember).jsonString
    case map: Map[_, _]                       => "null"
    case bytes: Array[Byte] if !bytes.isEmpty => bytes.hexString
    case bytes: Array[Byte]                   => "null"
    case set: Set[_]                          => set.map(jsonValue).mkString("[", ", ", "]")
    case other                                => other.toString.quoted
  }
}
