package com.apulbere.cygni.util

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{BColor, FColor}
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.{BarcodeFormat, EncodeHintType, MultiFormatWriter}

import scala.collection.JavaConverters._

class QrCodePrinter {
  private val multiFormatWriter = new MultiFormatWriter()
  private val qrParams = Map(
    EncodeHintType.ERROR_CORRECTION -> ErrorCorrectionLevel.L,
    EncodeHintType.CHARACTER_SET -> "utf-8"
  )

  def print(text: String): Unit = {
    val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 1, 1, qrParams.asJava)
    printToAscii(bitMatrix)
  }

  private def printToAscii(bitMatrix: BitMatrix): Unit = {
    val codePrinter = createPrinter(FColor.WHITE, BColor.BLACK)
    for(rows <- 0 until bitMatrix.getHeight) {
      val bgPrinter = createPrinter(FColor.BLACK, BColor.WHITE)
      for(cols <- 0 until bitMatrix.getWidth) {
        if(!bitMatrix.get(rows, cols)) {
          bgPrinter.print("  ")
        } else {
          codePrinter.print("  ")
        }
      }
      bgPrinter.clear()
      bgPrinter.println("")
    }
    codePrinter.clear()
  }

  private def createPrinter(fg: FColor, bg: BColor) = {
    new ColoredPrinter.Builder(1, false).foreground(fg).background(bg).build
  }

}

object QrCodePrinter {
  private val generator = new QrCodePrinter
  def print(text: String): Unit = generator.print(text)
}