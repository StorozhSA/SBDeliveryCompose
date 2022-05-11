package config

import java.io.File
import java.io.FileInputStream
import java.util.*

object SignHelper {
    private const val KEYSTORE = "KEYSTORE"
    private const val KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD"
    private const val KEY_PASSWORD = "KEY_PASSWORD"
    private const val KEY_ALIAS = "KEY_ALIAS"

    // Get sign data from external properties file.
    // Path to external properties file store in root/gradle.properties
    // in variable systemProp.signPropsPath
    private val signProps by lazy {
        Properties().apply {
            val signPropsPath: String by System.getProperties()
            if (File(signPropsPath).exists()) {
                load(FileInputStream(File(signPropsPath)))
            }
        }
    }
    val keyStore: String? = signProps.getProperty(KEYSTORE)
    val keyStorePassword: String? = signProps.getProperty(KEYSTORE_PASSWORD)
    val keyPassword: String? = signProps.getProperty(KEY_PASSWORD)
    val keyAlias: String? = signProps.getProperty(KEY_ALIAS)
}
