package org.example.apps.timesheet.test

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class Person(var firstName: String = "", var surname: String = "")
