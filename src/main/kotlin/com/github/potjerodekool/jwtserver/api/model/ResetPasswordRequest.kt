package com.github.potjerodekool.jwtserver.api.model

/**
 * Request to change or reset passoword.
 * ResetToken is needed when user is not logged in.
 */
data class ResetPasswordRequest(val email: String,
                                val newPassword: String,
                                val resetToken: String? = null)