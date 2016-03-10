package util

import javax.crypto.{Cipher => JCipher}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import scala.util.Try

object Cipher {

  private def doFinal(value: Array[Byte], mode: Int, algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Array[Byte] = {
    val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm)
    val cipher = JCipher.getInstance(s"$algorithm/$blockMode/$padding")
    cipher.init(mode, secretKeySpec, new IvParameterSpec(initVector.getBytes("UTF-8")))
    cipher.doFinal(value)
  }

  def encrypt(value: Array[Byte], algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[Array[Byte]] = Try {
    doFinal(value, JCipher.ENCRYPT_MODE, algorithm, secretKey, initVector, blockMode, padding)
  }

  def decrypt(value: Array[Byte], algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[Array[Byte]] = Try {
    doFinal(value, JCipher.DECRYPT_MODE, algorithm, secretKey, initVector, blockMode, padding)
  }

}
