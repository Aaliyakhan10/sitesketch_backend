package com.doftec.sitesketch.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(@Autowired val mailSender: JavaMailSender) {

    fun sendVerificationEmail(to: String, token: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject("Verify your email")
        message.setText("Click here to verify your account: http://localhost:8080/api/verify?token=$token")
        mailSender.send(message)
    }
}
