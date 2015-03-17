package models

import scala.util.Random

case class Employee(id: Long = new Random().nextLong(), username: String, firstName: String, lastName: String, picturePath: String)

object EmployeeDAO {

  var employees = Set(
    Employee(1L, "ufuk", "ufuk", "uzun", ""),
    Employee(2L, "huseyin", "hüseyin", "çelik", ""),
    Employee(3L, "yusuf", "yusuf", "soysal", "")
  )

  def findAll = employees.toList

  def findByUsername(username: String) = employees.find(_.username == username)

  def add(employee: Employee) = employees += (employee)
}