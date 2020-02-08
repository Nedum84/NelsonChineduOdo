package com.nelsonchineduodo

import org.json.JSONArray


data class CarFiltersClassBinder(val id: Int?,
                                 val start_year: Int?,
                                 val end_year: Int?,
                                 val gender: String?,
                                 val countries: JSONArray?,
                                 val colors: JSONArray?)


data class CarOwnersClassBinder(val id: Int?,
                                 val first_name: String?,
                                 val last_name: String?,
                                 val email: String?,
                                 val country: String?,
                                val car_model: String?,
                                val car_model_year: Int?,
                                val car_color: String?,
                                val gender: String?,
                                val job_title: String?,
                                val bio: String?)


