package util

import java.security.InvalidKeyException
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import scala.util.{Failure, Try}

object Encryption {

  def encrypt(value: Array[Byte], algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[Array[Byte]] = {
    Try {
      val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm)
      val cipher = Cipher.getInstance(s"$algorithm/$blockMode/$padding")
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(initVector.getBytes("UTF-8")))
      cipher.doFinal(value)
    } recoverWith {
      case ex: InvalidKeyException => Failure(ex)
    }
  }

}
