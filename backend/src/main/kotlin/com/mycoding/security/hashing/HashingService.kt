package com.mycoding.security.hashing

interface HashingService {
  fun generateSaltedHash(value: String, saltLength: Int = 32) : SaltedHash
  fun verifyPassword(value : String, saltedHash: SaltedHash) : Boolean
}