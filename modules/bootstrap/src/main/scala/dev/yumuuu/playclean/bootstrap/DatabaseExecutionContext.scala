package dev.yumuuu.playclean.bootstrap

import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.typed.ActorSystem

/**
 * データベースアクセス専用の ExecutionContext。
 *
 * application.conf に定義した `database-dispatcher` を利用する。
 */
@Singleton
class DatabaseExecutionContext @Inject() (system: ActorSystem[Nothing])
    extends CustomExecutionContext(system, "database-dispatcher")
