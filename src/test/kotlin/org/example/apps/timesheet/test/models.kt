package org.example.apps.timesheet.test

import java.time.LocalDate
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class Person(var firstName: String = "", var surname: String = "", var birthDate: LocalDate? = null)
