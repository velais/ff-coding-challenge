package flatfair

object OrganisationUnit {
  sealed trait Kind
  case object Client extends Kind
  case object Division extends Kind
  case object Area extends Kind
  case object Branch extends Kind
}

case class OrganisationUnit(
  name: String,
  kind: OrganisationUnit.Kind,
  config: Option[OrganisationUnitConfig],
  parent: Option[OrganisationUnit])

case class OrganisationUnitConfig(
  fixedMembershipFee: Option[Int])
