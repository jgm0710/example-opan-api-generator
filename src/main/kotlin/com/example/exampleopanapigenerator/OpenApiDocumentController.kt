package com.example.exampleopanapigenerator

import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenApiDocumentController {

    @GetMapping("/open-api/docs")
    fun getDocs(): ClassPathResource {
        return ClassPathResource("/docs/openapi.yml")
    }
}
