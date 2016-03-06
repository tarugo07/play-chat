package util

import com.typesafe.config.ConfigFactory

case class AccessTokenConfig(privateKey: String, initVector: String, expireMinutes: Int)

object Configuration {

  lazy val applicationConfig = ConfigFactory.load()

  lazy val accessTokenConfig = loadAccessTokenConfig

  private def loadAccessTokenConfig: AccessTokenConfig = {
    val config = applicationConfig.getConfig("accessToken")
    AccessTokenConfig(
      privateKey = config.getString("privateKey"),
      initVector = config.getString("initVector"),
      expireMinutes = config.getInt("expireMinutes")
    )
  }

}
