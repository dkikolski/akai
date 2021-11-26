package dev.dkikolski.akai.printer
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.cli.OutputFormat
import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.cli.OutputValuesFormat

trait KeyDescriptionFormatter {
  def render(keyDescription: KeyDescription, humanFriendlyFormat: Boolean): String
}

object KeyDescriptionFormatter {

  private val formatters: Map[OutputFormat, KeyDescriptionFormatter] = Map(
    OutputFormat.JSON  -> JsonFormatter,
    OutputFormat.Table -> TableFormatter
  )

  def render(
      keyDescription: KeyDescription,
      format: OutputFormat,
      valuesFormat: OutputValuesFormat
  ): String =
    formatters(format).render(keyDescription, valuesFormat == OutputValuesFormat.HumanFriendly)

}
