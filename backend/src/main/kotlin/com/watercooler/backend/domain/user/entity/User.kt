package com.watercooler.backend.domain.user.entity

import com.watercooler.backend.global.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER
) : BaseTimeEntity() {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}