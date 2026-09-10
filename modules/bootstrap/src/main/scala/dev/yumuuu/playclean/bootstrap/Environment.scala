package dev.yumuuu.playclean.bootstrap

/** アプリケーションの実行環境。
  *
  * classloader のように実行環境によって決まる値は、利用する class が直接取得せず、 この型を constructor injection して受け取る。
  */
final case class Environment(classLoader: ClassLoader)
