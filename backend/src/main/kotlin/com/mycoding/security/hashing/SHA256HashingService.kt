package com.mycoding.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingService : HashingService {

  override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    val saltAsHex = Hex.encodeHexString(salt)
    val hash = DigestUtils.sha256Hex("$saltAsHex$value")
    return SaltedHash(
      hash = hash,
      salt = saltAsHex
    )
  }

  override fun verifyPassword(value: String, saltedHash: SaltedHash): Boolean {
    val hash = DigestUtils.sha256Hex("${saltedHash.salt}${value}")
    return DigestUtils.sha256Hex(saltedHash.salt + value) == saltedHash.hash
  }
}