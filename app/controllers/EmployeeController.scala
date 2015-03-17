package controllers

import models.{Employee, EmployeeDAO}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.util.Random

object EmployeeController extends Controller {

  def list = Action {
    Ok(views.html.list(EmployeeDAO.findAll))
  }

  def success = Action {
    Ok(views.html.index("successfully uploaded"))
  }

  def show(username: String) = Action { implicit request =>
    EmployeeDAO.findByUsername(username).map { employee =>
      Ok(views.html.details(employee))
    }.getOrElse(NotFound)
  }

  private val employeeForm: Form[Employee] = Form(
    mapping(
      "id" -> ignored(new Random().nextLong()),
      "username" -> (
        nonEmptyText
        verifying("Bu kullanıcı adı daha önce kullanıldı.", EmployeeDAO.findByUsername(_).isEmpty)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "image" -> nonEmptyText
    )(Employee.apply)(Employee.unapply)
  )

  def save = Action { implicit request =>
    val newEmployeeForm = employeeForm.bindFromRequest()
    newEmployeeForm.fold(
      hasErrors = { form =>
        Redirect(routes.EmployeeController.newEmployee()).flashing(Flash(form.data) + ("error" -> "validation errors"))
      },
      success = { newEmployee =>
        EmployeeDAO.add(newEmployee)
        Redirect(routes.EmployeeController.show(newEmployee.username))
      }
    )
  }

  def newEmployee = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      val errorForm = employeeForm.bind(request.flash.data)

      errorForm
    } else
      employeeForm

    Ok(views.html.editEmployee(form))
  }


  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File("/tmp/picture"))
      Redirect(routes.EmployeeController.list)
    }.getOrElse {
      Redirect(routes.EmployeeController.list).flashing(
        "error" -> "Missing file"
      )
    }
  }
}