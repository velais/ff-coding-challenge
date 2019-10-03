package flatfair

import scala.annotation.tailrec

object CodingChallenge {

  val minMembershipFee = 120 * 100
  val minRentPerWeek = 25 * 100
  val maxRentPerWeek = 2000 * 100
  val VAT = 0.2

  /**
    * Calculates membership fee for a given rent/period and branch.
    *
    * @throws IllegalArgumentException Validates rent, period and organisationUnit.kind
    * @return Membership fee
    */
  def calculateMembershipFee(rentAmount: Int, rentPeriod: String, organisationUnit: OrganisationUnit): Int = {
    val period = Periods.withName(rentPeriod)
    val weeklyRent = calculateWeeklyRent(rentAmount, period)

    require(minRentPerWeek <= weeklyRent && weeklyRent <= maxRentPerWeek,
      s"rent must be between $minRentPerWeek and $maxRentPerWeek per week")
    require(organisationUnit.kind == OrganisationUnit.Branch,
      s"organisationUnit has kind: ${organisationUnit.kind}, must have kind: Branch")

    getFixedFee(organisationUnit) match {
      case Some(fee)                           => fee
      case _  if weeklyRent < minMembershipFee => minMembershipFee + VATFor(minMembershipFee, VAT)
      case _                                   => weeklyRent + VATFor(weeklyRent, VAT)
    }
  }

  @tailrec
  def getFixedFee(unit: OrganisationUnit): Option[Int] = unit match {
    case OrganisationUnit(_, _, Some(config), _) => config.fixedMembershipFee
    case OrganisationUnit(_, _, _, None)         => None
    case OrganisationUnit(_, _, _, Some(parent)) => getFixedFee(parent)
  }

  def calculateWeeklyRent(rent: Int, period: Periods.Value): Int = period match {
    case Periods.Week  => rent
    case Periods.Month => ((rent * 12.0) / 52).round.toInt
  }

  def VATFor(value: Int, vat: Double): Int = (value * (1 + vat)).round.toInt
}

