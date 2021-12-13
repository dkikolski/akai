package dev.dkikolski.akai.output
import dev.dkikolski.akai.cli.OutputValuesFormat
import dev.dkikolski.akai.schema.KeyDescription

private[output] trait KeyDescriptionFormatter {
  def render(keyDescription: KeyDescription): String
}

object KeyDescriptionFormatter {

  def render(
      keyDescription: KeyDescription,
      valuesFormat: OutputValuesFormat
  ): String =
    TableFormatter(valuesFormat == OutputValuesFormat.HumanFriendly).render(keyDescription)

}
