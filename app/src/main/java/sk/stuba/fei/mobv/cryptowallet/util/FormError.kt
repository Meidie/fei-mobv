package sk.stuba.fei.mobv.cryptowallet.util

enum class FormError(val message: String) {
    NO_ERROR(""),
    MISSING_VALUE("Value is required!"),
    NEGATIVE_VALUE("Negative amount not allowed!"),
    INVALID_PK_FORMAT("Key must start with \'G\' character"),
    INVALID_PK_LENGTH("Key must contain 56 alphanumeric characters"),
    INVALID_PIN("Pin in not valid")
}