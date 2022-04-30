package dev.dkikolski.akai.output
import dev.dkikolski.akai.cli.OutputValuesFormat
import dev.dkikolski.akai.schema.KeyDescription

private[output] trait KeyDescriptionRenderer {
  def render(keyDescription: FormattableKeyDescription): String
}

object KeyDescriptionRenderer {

  def render(
      keyDescription: KeyDescription,
      valuesFormat: OutputValuesFormat
  ): String =
    val formatableKeyDescription = valuesFormat match {
      case OutputValuesFormat.HumanFriendly => HumanFriendlyKeyDescription(keyDescription)
      case OutputValuesFormat.Raw           => RawValuesKeyDescription(keyDescription)
    }
    TableRenderer.render(formatableKeyDescription)
}
