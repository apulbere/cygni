package com.apulbere.cygni.util

import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.{BarcodeFormat, EncodeHintType, MultiFormatWriter}

import scala.collection.JavaConverters._

private class QrCodeGenerator {

  def from(text: String): String = {
    val qrParams = Map(
      EncodeHintType.ERROR_CORRECTION -> ErrorCorrectionLevel.L,
      EncodeHintType.CHARACTER_SET -> "utf-8"
    )
    val bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 1, 1, qrParams.asJava)
    toAscii(bitMatrix)
  }

  def toAscii(bitMatrix: BitMatrix): String = {
    val sb = new StringBuilder
    var rows = 0
    while(rows < bitMatrix.getHeight) {
      var cols = 0
      while(cols < bitMatrix.getWidth) {
        if(!bitMatrix.get(rows, cols)) {
          sb.append("\u001b[47m  \u001b[0m") //[40m
        } else {
          sb.append("\u001b[40m  \u001b[0m") //[1m
        }
        cols += 1; cols - 1
      }
      sb.append("\n")
      rows += 1; rows - 1
    }
    sb.toString
  }

}

object QrCodeGenerator {
  private val generator = new QrCodeGenerator
  def from(text: String): String = generator.from(text)
}