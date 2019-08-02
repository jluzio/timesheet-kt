package org.example.apps.timesheet

import org.apache.logging.log4j.LogManager
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestAppConfig::class])
abstract class AbstractTest {
    val log = LogManager.getLogger(javaClass)

}