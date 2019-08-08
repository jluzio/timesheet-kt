package org.example.apps.timesheet.test

import java.time.LocalDate
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
data class Person(
        var firstName: String = "",
        var surname: String = "",
        @get:XmlJavaTypeAdapter(io.github.threetenjaxb.core.LocalDateXmlAdapter::class)
        var birthDate: LocalDate? = null
)
