package org.example.apps.timesheet.test

import org.junit.Test
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.XmlRootElement

class JaxbTest {
    @Test
    fun test() {
        var jaxbCtx = JAXBContext.newInstance(Person::class.java)
        var marshaller = jaxbCtx.createMarshaller()

        val person = Person("John", "Doe")
        marshaller.marshal(person, System.out)
    }
}