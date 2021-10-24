package dev.dkikolski.akai.schema

final case class RootOfTrust(
    val verifiedBootKey: Array[Byte],
    val deviceLocked: Boolean,
    val verifiedBootState: VerifiedBootState,
    val verifiedBootHash: Array[Byte]
)

enum VerifiedBootState(val intValue: Int) {
  case Verified extends VerifiedBootState(0)
  case SelfSigned extends VerifiedBootState(1)
  case Unverified extends VerifiedBootState(2)
  case Failed extends VerifiedBootState(3)
}

object VerifiedBootState {
  def fromInt(i: Int): VerifiedBootState = VerifiedBootState.values.find(_.intValue == i).get // TODO: fix unsafe get
}
