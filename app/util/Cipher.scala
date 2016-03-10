package util

import javax.crypto.{Cipher => JCipher}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import scala.util.Try

object Cipher {

  def encrypt(value: Array[Byte], algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[Array[Byte]] = Try {
    val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm)
    val cipher = JCipher.getInstance(s"$algorithm/$blockMode/$padding")
    cipher.init(JCipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(initVector.getBytes("UTF-8")))
    cipher.doFinal(value)
  }

  def decrypt(value: Array[Byte], algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[Array[Byte]] = Try {
    val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm)
    val cipher = JCipher.getInstance(s"$algorithm/$blockMode/$padding")
    cipher.init(JCipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(initVector.getBytes("UTF-8")))
    cipher.doFinal(value)
  }

}
