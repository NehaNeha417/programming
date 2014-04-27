package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.{Await, ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.libs.json.{Json, JsString, JsObject, JsValue}
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.duration._
import models.{Scores, GameApp}

object Application extends Controller {

  /**
   * homepage, listing of available games
   */
  def index(user: Option[String]) = Action { implicit r =>
    Ok(views.html.index(user, GameApp.gameSet))
  }

  /**
   * Called by the login form in the navbar
   * TODO should really be replaced with use of the WebSocket
   * TODO should also check whether the username is already taken
   */
  def login(user: Option[String], password: Option[String]) = Action { implicit r =>
    // Map to None if "user" is an empty string, now render index only if user.isDefined.
    // map{...}getOrElse{...} is a "standard" Scala way to do this
    //    (src for Play's Redirect works the same way)
    user.filterNot(_.isEmpty).map { user =>
      Ok(views.html.index(Some(user), GameApp.gameSet))
    }.getOrElse {
      // `user` didn't work out, so save an error message to the "Flash scope",
      // which is data that shall only be sent back to the Client in _this_ Response using Cookies.
      // The Cookie's key & value _must_ be Strings.
      // Flash scope should not be used in Ajax calls because it is subject to race conditions.
      Redirect(routes.Application.index(None)).flashing(
        "error" -> "Please choose a valid username."
      )
    }
  }

  /**
   * the page for a particular game
   */
  def game(user: Option[String], name: Option[String]) = {
    name map { n =>
      // I don't think this is how you're supposed to do it
      val scoresFuture = GameApp.scoresForGame(n)
      Await.result(scoresFuture, 5 seconds)
      scoresFuture onSuccess { case scores: Map[String, Int] =>
        Action { implicit r =>
          Ok(views.html.game(user, name, scores))
        }
      }
      scoresFuture onFailure {
        case e : Exception => println("Exception in Application.game")
      }
    } getOrElse
        Redirect(routes.Application.index(user))
  }

  /**
   * User asked to create a new game instance
   * TODO use AsyncResult { ... } instead of Future.onSuccess { Action { ... } } ?
   */
  def create(user: Option[String], name: Option[String]) = {
    name map { strname : String =>
      // TODO this should be calling GameApp.createGame(name.get) not scoresForGame...
      val scoresFuture = GameApp.scoresForGame(strname)
      Await.result(scoresFuture, 5 seconds)
      scoresFuture onSuccess {
        case scores: Map[String, Int] =>
          Action { implicit r =>
            (user, name) match {
              case (Some(u), Some(n)) =>
                GameApp.createGame(u, n) map { _ =>
                  Ok(views.html.game(user, name, scores))
                } getOrElse { // TODO hook this Flashing thing into the form (not sure how to do that)
                  Redirect(routes.Application.index(user)).flashing("error" -> "Game already exists")
                }
              case _ => Redirect(routes.Application.index(user))
            }
          }
      }
    } getOrElse {
      Redirect(routes.Application.index(user))
    }
  }

  /**
   * User requested to join an existing game
   */
  def join(user: Option[String], name: Option[String]) = Action { implicit r =>
    // TODO get the real (scores: Map) to pass to the game view
    Ok(views.html.game(user, name, scores = Map.empty))
  }

  /**
   * TODO management of the WebSocket
   */                 // TODO use async and *ask* an actor for the info
  def gameSocket(user: String) = WebSocket.using[JsValue] { implicit r =>

    println(s"\n$user sent the following message through the chatSocket:")

    // temporarily: send one item through websocket and leave it open
    val out = Enumerator[JsValue](JsObject(Seq("username" -> JsString(user))))

    // temporarily: just pretty-print any received JSON to the console
    val in = Iteratee.foreach[JsValue] { msg => println(Json.prettyPrint(msg)) }

    (in, out)
  }

  /** TODO serve game.js Asset (or something like that; whatever you're supposed to do) */
  def squareGameJs = Action { implicit r =>
    Redirect(routes.Application.index(None))
  }
}
