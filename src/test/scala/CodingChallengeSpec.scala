import org.scalatest._
import flatfair._
import flatfair.CodingChallenge._
import flatfair.OrganisationUnit._

class CodingChallengeSpec extends FlatSpec with Matchers {
  behavior of "Flatfair coding challenge"

  it should "calculate membership fee from a fixed fee if it exists" in {
    val fixedFee = 35000
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(Some(35000))), None)
    calculateMembershipFee(3000, "week", branch) shouldBe fixedFee
  }

  it should "look for a parent units config if no config exists" in {
    val fixedFee = 35000
    val client = OrganisationUnit("client", Client, Some(OrganisationUnitConfig(None)), None)
    val division = OrganisationUnit("division", Division, Some(OrganisationUnitConfig(Some(fixedFee))), Some(client))
    val area = OrganisationUnit("area", Area, None, Some(division))
    val branch = OrganisationUnit("branch", Branch, None, Some(area))
    calculateMembershipFee(3000, "week", branch) shouldBe fixedFee
  }

  it should "enforce the minimum membership fee" in {
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(None)), None)
    calculateMembershipFee(5000, "week", branch) shouldBe minMembershipFee + VATFor(minMembershipFee, VAT)
  }

  it should "calculate membership fee from rent/period" in {
    val rent = 30000
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(None)), None)
    calculateMembershipFee(rent, "week", branch) shouldBe rent + VATFor(rent, VAT)
  }

  it should "validate min rent" in {
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(None)), None)
    an [IllegalArgumentException] should be thrownBy calculateMembershipFee(24, "week", branch)
    an [IllegalArgumentException] should be thrownBy calculateMembershipFee(3000, "month", branch)
  }

  it should "validate max rent" in {
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(None)), None)
    an [IllegalArgumentException] should be thrownBy calculateMembershipFee(2001, "week", branch)
    an [IllegalArgumentException] should be thrownBy calculateMembershipFee(8661, "month", branch)
  }

  it should "validate rent period string" in {
    val branch = OrganisationUnit("branch", Branch, Some(OrganisationUnitConfig(None)), None)
    an [NoSuchElementException] should be thrownBy calculateMembershipFee(200, "horse", branch)
  }

  it should "get fixed fees for an OrganisationUnit" in {
    val client = OrganisationUnit("client", Client, Some(OrganisationUnitConfig(None)), None)
    val division_A = OrganisationUnit("division_a", Division, Some(OrganisationUnitConfig(None)), Some(client))
    val area_A = OrganisationUnit("area_a", Area, Some(OrganisationUnitConfig(Some(30000))), Some(division_A))
    val branch_A = OrganisationUnit("branch_a", Branch, Some(OrganisationUnitConfig(None)), Some(area_A))
    val branch_B = branch_A.copy(name="branch_b", config=None)
    getFixedFee(client) shouldBe None
    getFixedFee(area_A) shouldBe  Some(30000)
    getFixedFee(branch_A) shouldBe None
    getFixedFee(branch_B) shouldBe Some(30000)
  }

  it should "calculate weekly rent" in {
    calculateWeeklyRent(2500,Periods.Month) shouldBe 577
    calculateWeeklyRent(250,Periods.Week) shouldBe 250
  }


  it should "calculate VAT" in {
    VATFor(1000, 0.2) shouldBe 1200
    VATFor(1000, 0.3) shouldBe 1300
  }
}
