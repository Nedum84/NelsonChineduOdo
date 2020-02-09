package com.nelsonchineduodo

import android.app.Activity
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.opencsv.CSVReader
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_car_owners.*
import java.io.File
import java.io.FileReader

class ActivityCarOwners : AppCompatActivity(), CarOwnerAdapter.CarOwnersAdapterCallbackInterface {
    lateinit var prefs: ClassSharedPreferences
    lateinit var thisContext: Activity
    lateinit private var progressDialog: ClassProgressDialog
    var start_page_from  = 0
    var isLoadingDataFromFile = false  //for checking when data fetching is going on

    private var carOwnerList = mutableListOf<CarOwnersClassBinder>()
    lateinit var filterDetails:CarFiltersClassBinder

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var ADAPTER : CarOwnerAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_owners)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        thisContext = this
        Slidr.attach(this)//for slidr swipe lib
        prefs = ClassSharedPreferences(thisContext)
        progressDialog = ClassProgressDialog(thisContext,"Loading Car Owners, Please wait...")
        progressDialog.createDialog()

        filterDetails = Gson().fromJson(prefs.getCarFilterJSONDetails(), Array<CarFiltersClassBinder>::class.java).asList()[0]
        supportActionBar?.title = "US Car Owners"
        supportActionBar?.subtitle = "Between ${filterDetails.start_year}-${filterDetails.end_year}"



        linearLayoutManager = LinearLayoutManager(thisContext)
        ADAPTER = CarOwnerAdapter(carOwnerList,thisContext)
        car_owners_recyclerview?.layoutManager = linearLayoutManager
        car_owners_recyclerview?.itemAnimator = DefaultItemAnimator()
        car_owners_recyclerview?.adapter = ADAPTER
        FilterCarOwners().execute()

        clickAndScrollEvents()
    }

    private fun clickAndScrollEvents() {
        goBack?.setOnClickListener {
            super.onBackPressed()
        }


        car_owners_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // only load more items if it's currently not loading
                if (!isLoadingDataFromFile) {
                    if(!recyclerView.canScrollVertically(1)){//1->bottom, -1 ->top
                        loadingProgressbar?.visibility = View.VISIBLE
                        isLoadingDataFromFile = true
                        start_page_from = (carOwnerList.map {it.id!!.toInt()}.toMutableList()).sorted().first()

                        FilterCarOwners().execute()//loading questions again
                    }
                }
            }
        })
    }


    inner class FilterCarOwners : AsyncTask<Void, Int, MutableList<CarOwnersClassBinder>?>(){
        private val fileExtStoragePath = Environment.getExternalStorageDirectory().absolutePath + "/venten/car_ownsers_data.csv"
        private val fileIntStoragePath =  Environment.getDataDirectory().absolutePath+ "/venten/car_ownsers_data.csv"
        private val fileIntStoragePath2 =  "/storage/sdcard1/venten/car_ownsers_data.csv"
        private var csvFilePath:String? = ""


        override fun doInBackground(vararg params: Void): MutableList<CarOwnersClassBinder>?{

            if(File(fileExtStoragePath).exists()){
                csvFilePath = fileExtStoragePath
            }else if(File(fileIntStoragePath).exists()){
                csvFilePath = fileIntStoragePath
            }else if(File(fileIntStoragePath2).exists()){
                csvFilePath = fileIntStoragePath2
            }else{
                return null
            }

            val fiveOwners = mutableListOf<CarOwnersClassBinder>()


            try {
                val csvReader = CSVReader(FileReader(csvFilePath))
                var readLine = csvReader.readNext()

                while (readLine != null) {

                    if(readLine[0]=="id"){readLine = csvReader.readNext();continue}//Remove the first row...

                    val id = readLine[0].toInt()
                    val first_name = readLine[1]
                    val last_name = readLine[2]
                    val email = readLine[3]
                    val country = readLine[4]
                    val car_model = readLine[5]
                    val car_model_year = readLine[6].toInt()
                    val car_color = readLine[7]
                    val gender = readLine[8]
                    val job_title = readLine[9]
                    val bio = readLine[10]

                    if(id<=start_page_from){readLine = csvReader.readNext();continue}

                    if (filterDetails.start_year !=0){
                        if (car_model_year<filterDetails.start_year!!){readLine = csvReader.readNext();continue}
                    }
                    if (filterDetails.end_year !=0){
                        if (car_model_year>filterDetails.end_year!!){readLine = csvReader.readNext();continue}
                    }
                    if (filterDetails.gender !=""){
                        if (gender.toLowerCase() !=filterDetails.gender!!.toLowerCase()){readLine = csvReader.readNext();continue}
                    }
                    if (filterDetails.countries!!.length() !=0){
                        val countriesArray = mutableListOf<String>()
                        for(c in 0 until filterDetails.countries!!.length()){
                            countriesArray.add("${filterDetails.countries!!.get(c)}".toLowerCase())
                        }

                        if(country.toLowerCase() !in countriesArray){readLine = csvReader.readNext();continue}
                    }
                    if (filterDetails.colors!!.length() !=0){
                        val colorsArray = mutableListOf<String>()
                        for(c in 0 until filterDetails.colors!!.length()){
                            colorsArray.add("${filterDetails.colors!!.get(c)}".toLowerCase())
                        }

                        if(car_color.toLowerCase() !in colorsArray){readLine = csvReader.readNext();continue}
                    }
                    val ownerD = CarOwnersClassBinder(
                            id,
                            first_name,
                            last_name,
                            email,
                            country,
                            car_model,
                            car_model_year,
                            car_color,
                            gender,
                            job_title,
                            bio
                    )


                    if(ownerD !in carOwnerList)
                        fiveOwners.add(ownerD)


                    readLine = csvReader.readNext()


                    if (fiveOwners.size>=10)
                        break
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }


            return fiveOwners
        }


        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
//            progressDialog.updateMsg("Loading..."+values[0]!!*100/5+"% done")
        }

        override fun onPostExecute(carOwners:MutableList<CarOwnersClassBinder>?){
            try {progressDialog.dismissDialog()} catch (e: Exception) {}
            loadingProgressbar?.visibility = View.GONE
            isLoadingDataFromFile = false

            when {
                carOwners==null -> {
                    no_data_wrapper?.visibility = View.VISIBLE
                    no_data_info?.text = "The car filter list is missing on your device"
                }
                carOwners.size==0 -> {
                    no_data_wrapper?.visibility = View.VISIBLE
                    no_data_info?.text = "Search result not found"
                }
                else -> {
                    no_data_wrapper?.visibility = View.GONE
                    ADAPTER.addItems(carOwners)
                }
            }

        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onBioReadMoreClicked() {
        carOwnerDetailDialogShow()
    }





    //student clearance
    private val dialogFragmentCarOwnerDetail = FragmentDialogCarOwnerDetail()
    private fun carOwnerDetailDialogShow(){
        if(dialogFragmentCarOwnerDetail.isAdded)return

        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
        val prev = supportFragmentManager.findFragmentByTag(FragmentDialogCarOwnerDetail::class.java.name)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragmentCarOwnerDetail.show(ft, FragmentDialogCarOwnerDetail::class.java.name)
    }

}
