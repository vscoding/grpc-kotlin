package io.intellij.kotlin.grpc.client.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.intellij.kotlin.grpc.multi.GreetRequest

/**
 * GreetReq
 *
 * @author tech@intellij.io
 */
class GreetReq(
    val id: Int = 0,
    val name: String,
    val gender: Gender,
    val emails: List<String>
) : GrpcConvertor<GreetRequest> {

    enum class Gender(@get:JsonValue val value: Int) {
        MAN(0),
        WOMEN(1);

        companion object {
            @JsonCreator
            fun fromValue(value: Int): Gender {
                for (color in entries) {
                    if (color.value == value) {
                        return color
                    }
                }
                throw IllegalArgumentException("Invalid gender value: $value")
            }
        }
    }

    override fun cast(): GreetRequest {
        return GreetRequest.newBuilder()
            .setId(id).setName(name)
            .setGenderValue(gender.value)
            .addAllEmails(emails)
            .build()
    }

}
