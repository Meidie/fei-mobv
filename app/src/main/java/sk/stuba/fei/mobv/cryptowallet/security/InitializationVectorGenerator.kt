package sk.stuba.fei.mobv.cryptowallet.security

import java.security.SecureRandom
import javax.crypto.spec.IvParameterSpec

class InitializationVectorGenerator(private val size: Int) {

    fun createInitializationVector(): IvParameterSpec {

        val initializationVector = ByteArray(size)
        SecureRandom().nextBytes(initializationVector)
        return IvParameterSpec(initializationVector)
    }
}