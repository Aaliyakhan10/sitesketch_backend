package com.doftec.sitesketch

import com.doftec.sitesketch.service.Aiservice
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SitesketchApplicationTests @Autowired constructor(
	private val aiservice: Aiservice
){


}
