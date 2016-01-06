package domain.model

case class EntityNotFoundException(message: String) extends Exception(message)
