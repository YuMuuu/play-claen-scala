package dev.yumuuu.playclean.bootstrap

import pureconfig.ConfigReader
import scala.concurrent.duration.FiniteDuration

final case class ServerConfig(
    host: String,
    port: Int,
    gracefulShutdownTimeout: FiniteDuration
) derives ConfigReader
