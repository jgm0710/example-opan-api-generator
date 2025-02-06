package com.example.exampleopanapigenerator.member

import openapi.api.MemberApi
import openapi.model.LoginMember200Response
import openapi.model.LoginMemberRequest
import openapi.model.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.NativeWebRequest
import java.util.Optional

@RestController
class MemberWebAdapter : MemberApi{

    override fun registerMember(member: Member?): ResponseEntity<Member> {
        return super.registerMember(member)
    }

    override fun getMember(authorization: String?, memberId: String?, xUserId: String?): ResponseEntity<Member> {
        return super.getMember(authorization, memberId, xUserId)
    }

    override fun loginMember(loginMemberRequest: LoginMemberRequest?): ResponseEntity<LoginMember200Response> {
        return super.loginMember(loginMemberRequest)
    }

    override fun updateMember(memberId: String?, member: Member?): ResponseEntity<Member> {
        return super.updateMember(memberId, member)
    }
}
