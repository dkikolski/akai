package dev.dkikolski.akai.io

import java.io.InputStream
import java.io.BufferedReader
import scala.util.Using

object CertificateReader {

    // TODO: Return Either with some specific error type?
    def read(stream: InputStream): Array[Byte] = {
        Using(stream)(_.readAllBytes()).getOrElse(Array())
    }
}