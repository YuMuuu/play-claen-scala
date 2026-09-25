package dev.yumuuu.playclean.bootstrap

import pureconfig.ConfigReader

final case class MockAuthConfig(
    accessToken: String,
    userId: String,
    userName: String
) derives ConfigReader
