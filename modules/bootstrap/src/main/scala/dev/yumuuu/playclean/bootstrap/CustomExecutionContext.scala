package dev.yumuuu.playclean.bootstrap

import org.apache.pekko.actor.typed.{ActorSystem, DispatcherSelector}

import scala.concurrent.ExecutionContextExecutor

/** ActorSystem が持つ dispatcher へ処理を委譲する ExecutionContext。
  *
  * default の ExecutionContext を利用すべきでない処理、たとえばデータベースアクセスや ブロッキング I/O を実行する場合に利用する。利用する dispatcher
  * の名前を指定して継承する。
  *
  * {{{
  * @Singleton
  * class DatabaseExecutionContext @Inject() (system: ActorSystem[Nothing])
  *     extends CustomExecutionContext(system, "database-dispatcher")
  * }}}
  *
  * dispatcher は ActorSystem が読み込む設定（application.conf）に定義する。dispatcher は
  * 利用箇所ではなくここで解決するため、存在しない名前を指定した場合は起動時に検出できる。
  *
  * @param system
  *   dispatcher を提供する ActorSystem
  * @param name
  *   dispatcher の設定上のパス
  * @see
  *   [[https://pekko.apache.org/docs/pekko/current/dispatchers.html Pekko Dispatchers]]
  */
abstract class CustomExecutionContext(system: ActorSystem[Nothing], name: String)
    extends ExecutionContextExecutor {

  private val dispatcher: ExecutionContextExecutor =
    system.dispatchers.lookup(DispatcherSelector.fromConfig(name))

  override def execute(command: Runnable): Unit =
    dispatcher.execute(command)

  override def reportFailure(cause: Throwable): Unit =
    dispatcher.reportFailure(cause)
}
