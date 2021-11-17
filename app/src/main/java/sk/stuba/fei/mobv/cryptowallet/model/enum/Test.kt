package sk.stuba.fei.mobv.cryptowallet.model.enum

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.stellar.sdk.KeyPair
import org.stellar.sdk.responses.AccountResponse

import org.stellar.sdk.Server
import shadow.com.google.gson.Gson


class Test {

    fun testStellar(activity: FragmentActivity) {

        val pair: KeyPair = KeyPair.random()
        println(String(pair.secretSeed))
        println(pair.accountId)

        val queue = Volley.newRequestQueue(activity)
       // getAccount("GBOWAUCRCZPPI5WE6RAMRNHILHVZAI6SHIFTXRKDECIKK4273H755I37")
//        getAccount2(queue, "GBOWAUCRCZPPI5WE6RAMRNHILHVZAI6SHIFTXRKDECIKK4273H755I37")

        Log.i("KEYPAIR", String(pair.secretSeed))
        Log.i("KEYPAIR", pair.accountId)


    }

    fun getAccount2(queue: RequestQueue, publicKey: String){

        val url = String.format("https://horizon-testnet.stellar.org/accounts/%s", publicKey)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Display the first 500 characters of the response string.

                val student = Gson().fromJson(response, AccountResponse::class.java)
                Log.i("KEYPAIR", "Response is: $student")
            },
            {
                Log.e("KEYPAIR",  " That didn't work!")
            })

        queue.add(stringRequest)


        Log.i("KEYPAIR", "DONE")
    }

    suspend fun getAccount(publicKey: String){


        val server = Server("https://horizon-testnet.stellar.org")
        val account:  AccountResponse = server.accounts().account(publicKey)
        Log.i("KEYPAIR", "Balances for account $publicKey")
        for (balance in account.balances) {
            System.out.printf(
                "Type: %s, Code: %s, Balance: %s%n",
                Log.i("KEYPAIR", balance.assetType),
                Log.i("KEYPAIR", balance.assetCode.or("NOPE")),
                Log.i("KEYPAIR", balance.balance)
            )
        }
    }


    fun createAcc(activity: FragmentActivity, pair: KeyPair){
        val queue = Volley.newRequestQueue(activity)
        val url = String.format("https://friendbot.stellar.org/?addr=%s", pair.accountId)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Display the first 500 characters of the response string.
                Log.i("KEYPAIR", "Response is: $response")
            },
            {
                Log.e("KEYPAIR",  " That didn't work!")
            })

        queue.add(stringRequest)


        Log.i("KEYPAIR", "DONE")
    }
}