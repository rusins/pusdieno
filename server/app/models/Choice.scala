package models

import java.util.UUID

case class Choice(id: UUID = UUID.randomUUID(), user: UUID, eatery: UUID)

// TODO: Cafe extends Eatery, cuz honestly it should – needs its own class for cafe specific stuff, but so much is the same, like come on!