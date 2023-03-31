package com.example.friendapp

import android.app.VoiceInteractor.Prompt
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import okhttp3.*
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler.JSON
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.Headers
import com.codepath.asynchttpclient.RequestHeaders
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Response
import okhttp3.internal.addHeaderLenient
import okhttp3.internal.http2.Header
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity() {
    var apodurl = ""
    var apodtitle = ""
    var picdate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        @RequiresApi(Build.VERSION_CODES.O)
        fun nasapicurl(date:String){
            val client = AsyncHttpClient()
            val params = RequestParams()
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(calendar.time)

            params["date"] = date
            params["thumbs"] = "false"
            params["api_key"] = "Your Api Key goes here. Go to NASA API website"

            client ["https://api.nasa.gov/planetary/apod",params,object: JsonHttpResponseHandler(){
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String?,
                    throwable: Throwable?
                ) {
                    if (errorResponse != null) {
                        Log.d("Picture Error",errorResponse)
                    }
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                  Log.d("Nasa Picture","response successful$json")
                    if (json != null) {
                        apodurl = json.jsonObject.getString("url")
                        apodtitle = json.jsonObject.getString("title")
                        picdate = json.jsonObject.getString("date")
                        Log.d("apodImageUrl"," APOD image URL is set")
                    }


                }

            }]

        }

        fun randomDateInRange(from: Date, to: Date): String {
            val diffInMillis = to.time - from.time
            val randomFactor = Random().nextDouble()
            val randomMillis = (diffInMillis * randomFactor).toLong()
            val randomDate = Date(from.time + randomMillis)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(randomDate)
        }

        val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("1995-06-16")!!
        val toDate = Calendar.getInstance().time
        val randomDate = randomDateInRange(fromDate, toDate)

        fun getAPOD(button: Button, imageView: ImageView,datetextview: TextView,titletextview: TextView){
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(calendar.time)

            button.setOnClickListener {
                val randomDate = randomDateInRange(fromDate, toDate)
                nasapicurl(randomDate)

                Glide.with(this)
                    . load(apodurl)
                    .fitCenter()
                    .into(imageView)


                    datetextview.text= "Date $picdate"
                    titletextview.text = "$apodtitle"
            }
        }

        //Main componenets
        val genimg = findViewById<ImageView>(R.id.genimg)
        val imgbtn = findViewById<Button>(R.id.genbtn)
        val Datetext = findViewById<TextView>(R.id.Date)
        val picturetitle = findViewById<TextView>(R.id.pictureTitle)


        genimg.setOnClickListener{view ->
            nasapicurl(randomDate)
            Log.d("apod", "APOD is Completed")

            }
            getAPOD(imgbtn,genimg,Datetext,picturetitle)
        }
    }






