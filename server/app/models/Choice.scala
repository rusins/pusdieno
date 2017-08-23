package models

import java.util.UUID

case class Choice(id: UUID = UUID.randomUUID(), user: UUID, establishment: UUID)