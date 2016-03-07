package util

import java.security.InvalidKeyException
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import org.apache.commons.codec.binary.Base64

import scala.util.{Failure, Try}

object Encryption {

  def encrypt(value: String, algorithm: String, secretKey: String, initVector: String, blockMode: String, padding: String): Try[String] = {
    Try {
      val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm)
      val cipher = Cipher.getInstance(algorithm, s"/$blockMode/$padding")
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(initVector.getBytes("UTF-8")))
      Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
    } recoverWith {
      case ex: InvalidKeyException => Failure(ex)
    }
  }

}
