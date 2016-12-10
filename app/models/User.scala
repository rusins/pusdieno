package models

object User {
  val ziedonis = User("Imants Ziedonis", Some("ziedonis.jpg"), Some(22578303), Some("11.30 - 12.30"), fav = true, hungry = false, cafe = true, Nil)
  val dalai = User("Dalai Lama", Some("dalai.jpg"), None, Some("11.45 - 12.15"), fav = true, hungry = false, cafe = true, Nil)
  val steve = User("Steve Buscemi", Some("steve.jpg"), Some(77654873), Some("14.00 - 16.00"), fav = true, hungry = true, cafe = false, Nil)
  val margaret = User("Margaret Thatcher", Some("margaret.jpg"), Some(65892397), Some("11.00 - 12.30"), fav = true, hungry = true, cafe = true, Nil)
  val tyrion = User("Tyrion Lannister", Some("tyrion.jpg"), None, None, fav = false, hungry = true, cafe = false, Nil)
  val vaira = User("Vaira Vīķe Freiberga", Some("vaira.jpg"), Some(22884993), Some("11.30 - 12.30"), fav = false, hungry = false, cafe = true, Nil)
  val martins = User("Mārtiņš Rītiņš", Some("martins.jpg"), None, None, fav = false, hungry = true, cafe = true, Nil)
  val twisty = User("Twisty the clown", Some("twisty.jpg"), Some(911), Some("24/7"), fav = false, hungry = false, cafe = false, Nil)
  val pawly = User("Pawly No-Nose", None, None, None, fav = true, hungry = false, cafe = true, Nil)
  val users = List(ziedonis, dalai, steve, margaret, tyrion, vaira, martins, twisty, pawly)
}

case class User(name: String, image: Option[String], phone: Option[Int], schedule: Option[String], fav: Boolean, hungry: Boolean, cafe: Boolean, location: List[String])