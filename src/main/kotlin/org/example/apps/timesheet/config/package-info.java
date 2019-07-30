@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
        @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(type = java.time.LocalDate.class, value = io.github.threetenjaxb.core.LocalDateXmlAdapter.class),
        @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(type = java.time.LocalDateTime.class, value = io.github.threetenjaxb.core.LocalDateTimeXmlAdapter.class),
})
package org.example.apps.timesheet.config;