package dev.yumuuu.playclean.bootstrap

import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors

import scala.concurrent.Await
import scala.concurrent.duration.*

@Singleton
class ManagedActorSystem @Inject() () {
  lazy val actorSystem: ActorSystem[Nothing] =
    ActorSystem[Nothing](Behaviors.empty, "pekko-http")

  @PostConstruct
  def start(): Unit =
    val _ = actorSystem

  @PreDestroy
  def stop(): Unit = {
    actorSystem.terminate()
    val _ = Await.result(actorSystem.whenTerminated, 10.seconds)
  }
}
