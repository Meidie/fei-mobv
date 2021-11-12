package sk.stuba.fei.mobv.cryptowallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        testCrashlytics()
    }

//    private fun testCrashlytics(){
//        val crashButton = Button(this)
//        crashButton.text = "Test Crash"
//        crashButton.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
//
//        addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))
//
//    }
}