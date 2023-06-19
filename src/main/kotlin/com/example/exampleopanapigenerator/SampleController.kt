package com.example.exampleopanapigenerator

import com.example.api.SamplesApi
import com.example.model.CreateSampleRequest
import com.example.model.SamplesResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class SampleController : SamplesApi {

    override fun getSamples(sort: Int, filter: Int): ResponseEntity<List<SamplesResponse>> {
        return super.getSamples(sort, filter)
    }

    override fun createSample(createSampleRequest: CreateSampleRequest?): ResponseEntity<SamplesResponse> {
        return super.createSample(createSampleRequest)
    }
}
