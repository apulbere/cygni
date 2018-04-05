package com.apulbere.cygni.util

import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.{BarcodeFormat, EncodeHintType, MultiFormatWriter}

import scala.collection.JavaConverters._

class QrCodeGenerator {
  private val multiFormatWriter = new MultiFormatWriter()
  private val qrParams = Map(
    EncodeHintType.ERROR_CORRECTION -> ErrorCorrectionLevel.L,
    EncodeHintType.CHARACTER_SET -> "utf-8"
  )

  def from(text: String): String = {
    val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 1, 1, qrParams.asJava)
    toAscii(bitMatrix)
  }

  private def toAscii(bitMatrix: BitMatrix): String = {
    val sb = new StringBuilder
    for(rows <- 0 until bitMatrix.getHeight) {
      for(cols <- 0 until bitMatrix.getWidth) {
        if(!bitMatrix.get(rows, cols)) {
          sb.append("\u001b[47m  \u001b[0m") //[40m
        } else {
          sb.append("\u001b[40m  \u001b[0m") //[1m
        }
      }
      sb.append("\n")
    }
    sb.toString
  }

}

object QrCodeGenerator {
  private val generator = new QrCodeGenerator
  def from(text: String): String = generator.from(text)
}