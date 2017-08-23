package models

import java.util.UUID

case class Contact(id: UUID = UUID.randomUUID(), name: String, ownerID: UUID, contactID: Option[UUID],
                   phone: Option[Int] = None, email: Option[String] = None, favorite: Boolean = false)