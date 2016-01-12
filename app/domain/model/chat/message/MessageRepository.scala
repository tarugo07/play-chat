package domain.model.chat.message

trait MessageRepository {

  def MessageOfIdentity(id: MessageId): Message

}
