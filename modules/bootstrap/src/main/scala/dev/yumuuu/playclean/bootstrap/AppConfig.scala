package dev.yumuuu.playclean.bootstrap

import pureconfig.ConfigReader

final case class AppConfig(
    server: ServerConfig,
    database: DatabaseConfig,
    mockAuth: MockAuthConfig
) derives ConfigReader
