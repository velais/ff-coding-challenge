# Flatfair Coding Challenge

### Usage

Requires `sbt` to be installed  - https://www.scala-sbt.org/release/docs/Setup.html

Run the tests:
```
sbt test
```

### Future considerations

- Have the model represent parent -> child relationship not just child -> parent, would allow for easier traversal for other uses.
- Move constant values to a config or pull from a database to allow easier control.
- Expand OrganisationUnit to allow for type safety and differences in the OrgUnit types.
```scala
// Something like this
sealed trait OrganisationalUnit {
  def name: String
  def config: Option[OrganisationUnitConfig]
}

sealed trait OrganisationalUnitChild {
  def parent: OrganisationalUnit
}

case class Client(...) extends OrganisationalUnit
case class Division(...) extends OrganisationalUnit with OrganisationalUnitChild
case class Area(...) extends OrganisationalUnit with OrganisationalUnitChild
case class Branch(...) extends OrganisationalUnit with OrganisationalUnitChild
```
