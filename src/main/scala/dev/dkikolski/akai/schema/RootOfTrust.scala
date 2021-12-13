package dev.dkikolski.akai.schema

final case class RootOfTrust(
    verifiedBootKey: Array[Byte],
    deviceLocked: Boolean,
    verifiedBootState: VerifiedBootState,
    verifiedBootHash: Array[Byte]
)

enum VerifiedBootState(val intValue: Int) {
  case Verified extends VerifiedBootState(0)
  case SelfSigned extends VerifiedBootState(1)
  case Unverified extends VerifiedBootState(2)
  case Failed extends VerifiedBootState(3)
}