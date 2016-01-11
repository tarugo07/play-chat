package domain.model.chat.member

trait MemberRepository {

  def messageOfIdentity(id: MemberId): Member

}
