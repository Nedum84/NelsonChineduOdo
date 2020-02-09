package com.nelsonchineduodo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.adapter_car_filters.view.*
import kotlinx.android.synthetic.main.adapter_car_owners.view.*


class CarFilterAdapter(items:MutableList<CarFiltersClassBinder>, ctx: Context): RecyclerView.Adapter<CarFilterAdapter.ViewHolder>(){
    private var adapterCallbackInterface: CarFilterAdapterCallbackInterface? = null
    private var list = items
    private var context = ctx




    init {
        try {
            adapterCallbackInterface = context as CarFilterAdapterCallbackInterface
        } catch (e: ClassCastException) {
            throw RuntimeException(context.toString() + "Activity must implement CarFilterAdapterCallbackInterface.", e)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.adapter_car_filters,
                parent,
                false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItems(items: MutableList<CarFiltersClassBinder>) {
        val lastPos = list.size - 1
        list.addAll(items)
        notifyItemRangeInserted(lastPos, items.size)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listDetails = list[position]

        holder.filterYear.text     = "Between ${listDetails.start_year} - ${listDetails.end_year}"
        holder.filterGender.text = if(listDetails.gender!!.isNotEmpty())"${listDetails.gender}" else "both male & female"


        //Colors
        if(listDetails.colors!!.length()>0){
            val colorsArray = mutableListOf<String>()
            var colorsString = ""
            for(c in 0 until listDetails.colors.length()){
                val cl = listDetails.colors[c]
                if(cl in colorsArray)continue

                colorsArray.add("$cl")
                colorsString += "$cl, "
            }
            holder.filterColors.text     = colorsString.substring(0, colorsString.length - 2)

        }else{
            holder.filterColors.text     = "ALL"
        }

        //Countries
        if(listDetails.countries!!.length()>0){
            val countriesArray = mutableListOf<String>()
            var countriesString = ""
            for(c in 0 until listDetails.countries.length()){
                val co = listDetails.countries[c]
                if(co in countriesArray)continue

                countriesArray.add("$co")
                countriesString += "$co, "
            }
            holder.filterCountries.text     = countriesString.substring(0, countriesString.length - 2)
        }else{
            holder.filterCountries.text     = "ALL"
        }

        holder.filterWrapper.setOnClickListener {
            ClassSharedPreferences(context).setCarFilterJSONDetails(Gson().toJson(mutableListOf(listDetails)))

            adapterCallbackInterface?.onFilterClicked()
        }

    }


    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val filterWrapper = v.filterWrapper!!
        val filterYear = v.filterYear!!
        val filterGender = v.filterGender!!
        val filterColors = v.filterColors!!
        val filterCountries = v.filterCountries!!
    }



    //interface declaration
    interface CarFilterAdapterCallbackInterface {
        fun onFilterClicked()
    }
}

class CarOwnerAdapter(var list:MutableList<CarOwnersClassBinder>, val context: Context): RecyclerView.Adapter<CarOwnerAdapter.ViewHolder>(){
    private var adapterCallbackInterface: CarOwnersAdapterCallbackInterface? = null




    init {
        try {
            adapterCallbackInterface = context as CarOwnersAdapterCallbackInterface
        } catch (e: ClassCastException) {
            throw RuntimeException(context.toString() + "Activity must implement CarFilterAdapterCallbackInterface.", e)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.adapter_car_owners,
                parent,
                false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItems(items: MutableList<CarOwnersClassBinder>) {
        val lastPos = list.size - 1
        list.addAll(items)
        notifyItemRangeInserted(lastPos, items.size)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listDetails = list[position]

        holder.ownerName.text     = "${listDetails.first_name?.toUpperCase()} ${listDetails.last_name}"
        holder.ownerEmail.text     = "${listDetails.email}"
        holder.ownerGender.text = "${listDetails.gender?.toLowerCase()} "
        holder.ownerCountry.text     = "${listDetails.country?.toUpperCase()}"
        holder.ownerJob.text     = "${listDetails.job_title}"
        holder.ownerCarDetail.text     = "${listDetails.car_color} ${listDetails.car_model} Car(${listDetails.car_model_year})"
        holder.ownerBio.text     = "${listDetails.bio}"

        holder.ownerBioReadMore.setOnClickListener {
            ClassSharedPreferences(context).setCarOwnerJSONDetails(Gson().toJson(mutableListOf(listDetails)))

            adapterCallbackInterface?.onBioReadMoreClicked()
        }
    }


    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val ownerWrapper = v.ownerWrapper!!
        val ownerName = v.ownerName!!
        val ownerEmail = v.ownerEmail!!
        val ownerGender = v.ownerGender!!
        val ownerCountry = v.ownerCountry!!
        val ownerJob = v.ownerJob!!
        val ownerCarDetail = v.ownerCarDetail!!
        val ownerBio = v.ownerBio!!
        val ownerBioReadMore = v.ownerBioReadMore!!
    }

    //interface declaration
    interface CarOwnersAdapterCallbackInterface {
        fun onBioReadMoreClicked()
    }
}
