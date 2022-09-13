package dev.dkikolski.akai.input

import java.io.InputStream
import java.io.BufferedReader
import scala.util.Using

object CertificateReader {
    def read(stream: InputStream): Array[Byte] = {
        Using(stream)(_.readAllBytes()).getOrElse(Array())
    }
}