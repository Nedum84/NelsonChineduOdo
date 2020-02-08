package com.nelsonchineduodo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException


class ActivityHome : AppCompatActivity() , CarFilterAdapter.CarFilterAdapterCallbackInterface {
    lateinit var thisContext: Activity


    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var ADAPTER : CarFilterAdapter
    private var carFilterList = mutableListOf<CarFiltersClassBinder>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        thisContext = this
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setIcon(R.drawable.ic_directions_car_white_24dp)


        linearLayoutManager = LinearLayoutManager(thisContext)
        ADAPTER = CarFilterAdapter(carFilterList,thisContext)
        car_filter_recyclerview.layoutManager = linearLayoutManager
        car_filter_recyclerview.itemAnimator = DefaultItemAnimator()
        car_filter_recyclerview.adapter = ADAPTER
        loadCarFilters()





        tapToRetry.setOnClickListener {
            refreshList()
        }
    }

    private fun refreshList(){
        carFilterList.clear()
        ADAPTER.addItems(carFilterList)
        ADAPTER.notifyDataSetChanged()
        loadCarFilters()
    }
    private fun loadCarFilters(){
        //creating volley string request
        loadingProgressbar?.visibility = View.VISIBLE
        no_network_tag?.visibility = View.GONE
        val stringRequest = object : StringRequest(Method.GET, UrlHolder.URL_CAR_FILTERS,
                Response.Listener<String> { response ->
                    loadingProgressbar?.visibility = View.GONE

                    try {
                        val jsonArray = JSONArray(response)

                        val dataArray = mutableListOf<CarFiltersClassBinder>()

                        if(jsonArray.length()!=0){
                            for (i in 0 until jsonArray.length()) {
                                val jsonObj = jsonArray.getJSONObject(i)
                                val data = CarFiltersClassBinder(
                                        jsonObj.getInt("id"),
                                        jsonObj.getInt("start_year"),
                                        jsonObj.getInt("end_year"),
                                        jsonObj.getString("gender"),
                                        jsonObj.getJSONArray("countries"),
                                        jsonObj.getJSONArray("colors")
                                )
                                if (data !in dataArray)dataArray.add(data)
                            }

                            ADAPTER.addItems(dataArray)
                        }else{
                            ClassAlertDialog(thisContext).toast("No available filter, Try again later...")
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        ClassAlertDialog(thisContext).toast("Error occurred, try again...")
                    }
                },
                Response.ErrorListener { _ ->
                    loadingProgressbar?.visibility = View.GONE
                    no_network_tag?.visibility = View.VISIBLE
                }) {}

        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_about_us->{
                aboutUsDialog()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }


    private fun aboutUsDialog() {
        ClassAlertDialog(thisContext).alertMessage("This is a mini App project that get the US car Owners over several years" +
                "\n\nDeveloped by Nelson Chinedu Odo. I develop robust, dynamic & responsible Web/Android applications using Kotlin","About Us")
    }



    override fun onFilterClicked() {
        startActivity(Intent(this,   ActivityCarOwners::class.java))
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }

}
