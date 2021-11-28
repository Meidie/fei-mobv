package sk.stuba.fei.mobv.cryptowallet.adapter

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

//"app:errorText"
@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}