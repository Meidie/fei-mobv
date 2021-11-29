package sk.stuba.fei.mobv.cryptowallet.util

enum class FormError(var message: String) {
    NO_ERROR(""),
    MISSING_VALUE("Value is required!"),
    NEGATIVE_VALUE("Negative amount not allowed!"),
    INVALID_PK_FORMAT("Key must start with \'G\' character"),
    INVALID_PK_LENGTH("Key must contain 56 alphanumeric characters"),
    INVALID_PIN("Pin in not valid"),
    SK_ALREADY_EXISTS("Account already exist in our database"),
    ACCOUNT_NOT_FOUND("Account does not exist in our database"),
    INCORRECT_CHAR(", is not alowed, use ."),
    ACCOUNT_BALANCE_EXCEEDED("Account balance exceeded, maximum is: ")
}