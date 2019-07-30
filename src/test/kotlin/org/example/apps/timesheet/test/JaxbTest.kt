package org.example.apps.timesheet.test

import org.junit.Test
import java.io.StringReader
import java.io.StringWriter
import java.time.LocalDate
import javax.xml.bind.JAXBContext

class JaxbTest {
    @Test
    fun test() {
        var jaxbCtx = JAXBContext.newInstance(Person::class.java)
        var marshaller = jaxbCtx.createMarshaller()
        var unmarshaller = jaxbCtx.createUnmarshaller()

        val data = Person("John", "Doe", LocalDate.now())

        var output = StringWriter()
        marshaller.marshal(data, output)
        println("output: $output")

        var input = StringReader(output.toString())
        var returnedData = unmarshaller.unmarshal(input)
        println("returnedData: $returnedData")
    }
}