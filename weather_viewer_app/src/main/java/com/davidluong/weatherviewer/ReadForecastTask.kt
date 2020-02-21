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
import com.davidluong.weatherviewer.R
import java.net.HttpURLConnection
import java.io.BufferedReader
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ReadForecastTask // creates a new ReadForecastTask
( var zipcodeString: String,  var weatherForecastListener: ForecastListener, context: Context):
        AsyncTask<Unit, Unit, Boolean>() {

    var resources: Resources? = null

    var temperatureString: String? = null // the temperature
    var humidityString: String? = null // the humidity
    var iconBitmap: Bitmap? = null // image of the sky condition
    var cityString: String? = null
    var countryString: String? = null

    var bitmapSampleSize: Int = -1

    init {
        resources = context.resources
    }

    // interface for receiver of weather information
    interface ForecastListener {
        fun onForecastLoaded(image: Bitmap?, temperature: String?,
                             humidity: String?,
                             cityString: String?,
                             countryString: String?)
    } // end interface ForecastListener

    // set the sample size for the forecast's Bitmap
    fun setSampleSize(sampleSize: Int) {
        bitmapSampleSize = sampleSize
    } // end method setSampleSize

    override fun doInBackground(vararg args: Unit?): Boolean? {
        var urlConnection: HttpURLConnection? = null
        try {
            // the url for the WorldWeatherOnline JSON service
            //****** MAKE SURE YOU REPLACE WITH YOUR API KEY

            /*
            val webServiceURL = URL(resources?.getString(
                    R.string.pre_zipcode_url) + zipcodeString +
                    ",us&&units=imperial&appid=YOUR_APP_ID_API_KEY_HERE")*/

            val webServiceURL = URL(resources?.getString(
                    R.string.pre_zipcode_url) + zipcodeString +
                    ",us&&units=imperial&appid=e79200d7d4f574b35eb8a9da9b101f14")

            urlConnection = webServiceURL.openConnection() as HttpURLConnection

            val responseCode = urlConnection?.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false
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
                val coord = jsonObj.getJSONObject("coord")
                val longitude = coord.getString("lon")
                val latitude = coord.getString("lat")

                val weatherArray = jsonObj.getJSONArray("weather")
                val weatherONE = weatherArray.getJSONObject(0)
                val desc = weatherONE.getString("description")
                val imageString = weatherONE.getString("icon")

                val mainObj = jsonObj.getJSONObject("main")
                temperatureString = mainObj.getDouble("temp").toString()
                humidityString = mainObj.getInt("humidity").toString()

                val sys = jsonObj.getJSONObject("sys")
                val sunsetL = sys.getLong("sunset")
                val sunsetTime = Date(sunsetL * 1000L)
                countryString = sys.getString("country")

                cityString = jsonObj.getString("name")

                iconBitmap = getIconBitmap("${resources!!.getString(R.string.pre_condition_url)}$imageString${resources!!.getString(R.string.post_condition_url)}",
                        bitmapSampleSize)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return true
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
        return false
    } // end method doInBackground

    // update the UI back on the main thread
    override fun onPostExecute(success: Boolean) {
        //should make sure forecastString has a value
        // pass the information to the ForecastListener

        if (success) {
            weatherForecastListener.onForecastLoaded(iconBitmap,
                    temperatureString, humidityString,
                    cityString,
                    countryString)
        } else {
            weatherForecastListener.onForecastLoaded(null,
                    null, null,
                    null,
                    null)
        }
    } // end method onPostExecute

    companion object {
        private val TAG = "ReadForecastTask.kt"

        // get the sky condition image Bitmap
        fun getIconBitmap(imageURLString: String,
                           bitmapSampleSize: Int): Bitmap? {
            var iconBitmap: Bitmap? = null // create the Bitmap
            try {
                // create a URL pointing to the image on WorldWeatherOnline's site
                val weatherURL = URL(imageURLString)

                val options = BitmapFactory.Options()
                if (bitmapSampleSize != -1) {
                    options.inSampleSize = bitmapSampleSize
                } // end if

                // save the image as a Bitmap
                iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options)
            } // end try
            catch (e: MalformedURLException) {
                Log.e(TAG, e.toString())
            } // end catch
            catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
            catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            // end catch

            return iconBitmap // return the image
        } // end method getIconBitmap
    }
} // end ReadForecastTask

