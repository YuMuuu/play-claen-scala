package dev.yumuuu.playclean.bootstrap

import dev.yumuuu.playclean.application.auth.MockAuthService
import dev.yumuuu.playclean.application.user.UserAddInteractor
import dev.yumuuu.playclean.infrastructure.persistence.DoobieUserRepository
import dev.yumuuu.playclean.presentation.http.ApiRoutes
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode
import org.jboss.weld.environment.se.Weld
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

object Main {
  def main(arguments: Array[String]): Unit = {
    val weld = new Weld()
      .disableDiscovery()
      .setBeanDiscoveryMode(BeanDiscoveryMode.ALL)
      .addPackages(
        false,
        classOf[AppModule],
        classOf[MockAuthService],
        classOf[UserAddInteractor],
        classOf[DoobieUserRepository],
        classOf[ApiRoutes],
        classOf[PekkoHttpServer]
      )
      .property("org.jboss.weld.se.shutdownHook", false)
    val container = weld.initialize()
    val _ = container.select(classOf[ServerBinding]).get()

    /*
    ctrl+cでシャットダウンできるための処理
     */
    val shutdownStarted = new AtomicBoolean(false)
    def shutdown(): Unit =
      if shutdownStarted.compareAndSet(false, true) then container.shutdown()

    val _ = sys.addShutdownHook(shutdown())
    try new CountDownLatch(1).await()
    catch case _: InterruptedException => shutdown()
  }
}
