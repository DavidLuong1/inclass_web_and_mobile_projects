package com.davidluong.weatherviewer

import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.io.BufferedReader
import org.json.JSONException
import org.json.JSONObject
import android.view.Gravity
import android.widget.Toast
import com.davidluong.weatherviewer.R


//converts zipcode to city name in a background thread
class ReadLocationTask // creates a new ReadForecastTask
( var zipcodeString: String, var context: Context, var weatherLocationLoadedListener: LocationLoadedListener):
        AsyncTask<Unit, Unit, String?>() {

    var resources: Resources? = null
    var cityString: String? = null
    var countryString: String? = null

    init {
        resources = context.resources
    }

    // interface for receiver of weather information
    interface LocationLoadedListener {
        fun onLocationLoaded(cityString: String?,
                             countryString: String?)
    } // end interface LocationLoadedListener

    override fun doInBackground(vararg args: Unit?): String? {
        var urlConnection: HttpURLConnection? = null
        try {
            // the url for the WorldWeatherOnline JSON service
            //****** MAKE SURE YOU REPLACE WITH YOUR API KEY
            val webServiceURL = URL(resources?.getString(
                    R.string.pre_zipcode_url) + zipcodeString +
                    ",us&&units=imperial&appid=e79200d7d4f574b35eb8a9da9b101f14")

            urlConnection = webServiceURL.openConnection() as HttpURLConnection

            val responseCode = urlConnection?.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null
            }
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()

            try {
                val jsonObj = JSONObject(stringBuilder.toString())

                val sys = jsonObj.getJSONObject("sys")
                countryString = sys.getString("country")

                cityString = jsonObj.getString("name")

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return cityString
        } // end try
        catch (e: MalformedURLException) {
            Log.v(TAG, e.toString())
        } // end catch
        catch (e: IOException) {
            Log.v(TAG, e.toString())
        } // end catch
        catch (e: IllegalStateException) {
            Log.v(TAG, e.toString() + zipcodeString)
        }
        finally {
            urlConnection?.disconnect();
        }
        // end catch
        return null
    } // end method doInBackground

    // update the UI back on the main thread
    override fun onPostExecute(cityName: String?) {
        // if a city was found to match the given zipcode
        if (cityString != null) {
            // pass the information back to the LocationLoadedListener
            weatherLocationLoadedListener.onLocationLoaded(cityString,
                    countryString)
        } // end if
        else {
            // display Toast informing that location information
            // couldn't be found
            val errorToast = Toast.makeText(context, resources!!.getString(
                    R.string.invalid_zipcode_error), Toast.LENGTH_LONG)
            errorToast.setGravity(Gravity.CENTER, 0, 0) // center the Toast
            errorToast.show() // show the Toast
        } // end else
    } // end method onPostExecute

    companion object {
        private val TAG = "ReadLocatonTask.kt"
    }
} // end ReadForecastTask

