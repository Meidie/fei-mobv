package sk.stuba.fei.mobv.cryptowallet.util

enum class FormError(var message: String) {
    NO_ERROR(""),
    MISSING_VALUE("Value is required!"),
    NEGATIVE_VALUE("Negative amount not allowed!"),
    INVALID_PK_FORMAT("Key must start with \'G\' character"),
    INVALID_SK_FORMAT("Key must start with \'S\' character"),
    INVALID_PK_LENGTH("Key must contain 56 alphanumeric characters"),
    REQUIRED_PIN("PIN is required"),
    INVALID_PIN_LENGTH("PIN is too short"),
    INVALID_PIN("PIN in not valid"),
    INVALID_SK("Private key in not valid"),
    SK_ALREADY_EXISTS("Account already exist in our database"),
    ACCOUNT_NOT_FOUND("Account does not exist in our database"),
    ACCOUNT_BALANCE_EXCEEDED("Balance exceeded, maximum is: %s"),
    STELLAR_ACCOUNT_NOT_FOUND("Account does not exist.");

    fun getFormattedMessage(value: String?): String {
        return String.format(this.message, value)
    }
}