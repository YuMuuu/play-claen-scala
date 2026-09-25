package dev.yumuuu.playclean.bootstrap

import pureconfig.ConfigReader

final case class DatabaseConfig(url: String, user: String, password: String) derives ConfigReader
