package com.techno.vginv.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.DashboardActivity
import com.techno.vginv.IOnBackPressed
import com.techno.vginv.R
import com.techno.vginv.SharedInstance.allCities
import com.techno.vginv.SharedInstance.allCountries
import com.techno.vginv.SharedInstance.allDepartments
import com.techno.vginv.SharedInstance.projectCatalog
import com.techno.vginv.utils.CloudDataService.ChangeProfile
import com.techno.vginv.utils.CloudDataService.getUserProfile
import com.techno.vginv.utils.FilePath
import com.techno.vginv.utils.SharedPrefManager
import com.techno.vginv.utils.SimpleResponse
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment, IOnBackPressed {
    private var totalInvestment: TextView? = null
    private var totalProjects: TextView? = null
    private var favorite: TextView? = null
    private var email: EditText? = null
    private var phone: EditText? = null
    private var address: EditText? = null
    private var designation: EditText? = null
    private var description: EditText? = null
    private var name: EditText? = null
    private var dialog: ProgressDialog? = null
    private var profile_image: CircleImageView? = null
    private var chooseImage: ImageView? = null
    private var edit: Button? = null
    private var save: Button? = null
    private var countriesLayout: LinearLayout? = null
    private var addressSec: LinearLayout? = null
    private var cityLayout: LinearLayout? = null
    var country: Spinner? = null
    var city: Spinner? = null
    var department: Spinner? = null
    private var countryID = 0
    private var cityID = 0
    private var departmentID = 0
    private val countriesList: MutableList<String> = ArrayList()
    private val categories: MutableList<String> = ArrayList()
    private val cityList: MutableList<String> = ArrayList()
    var simpleResponse: SimpleResponse<Uri?>? = null
    private var fab: FloatingActionButton? = null
    private var fabText: TextView? = null
    private var projectLayout: LinearLayout? = null
    private var dealsLayout: LinearLayout? = null
    var picturePath = ""
    var filePath = ""
    var imageName = ""
    var fileName: String? = ""
    private var i = 0
    private var isEditable = false
    private var isSomethingChanged = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        email = view.findViewById(R.id.email)
        phone = view.findViewById(R.id.phone)
        address = view.findViewById(R.id.address)
        totalInvestment = view.findViewById(R.id.totalInvestment)
        totalProjects = view.findViewById(R.id.totalProjects)
        favorite = view.findViewById(R.id.favorite)
        department = view.findViewById(R.id.department)
        designation = view.findViewById(R.id.role)
        name = view.findViewById(R.id.name)
        description = view.findViewById(R.id.description)
        profile_image = view.findViewById(R.id.profile_image)
        edit = view.findViewById(R.id.editProfile)
        save = view.findViewById(R.id.saveProfile)
        countriesLayout = view.findViewById(R.id.countriesSelection)
        city = view.findViewById(R.id.cities)
        country = view.findViewById(R.id.countries)
        addressSec = view.findViewById(R.id.addressLayout)
        cityLayout = view.findViewById(R.id.citiesSelection)
        chooseImage = view.findViewById(R.id.chooseImage)
        fab = view.findViewById(R.id.fab)
        fabText = view.findViewById(R.id.fabText)
        dealsLayout = view.findViewById(R.id.dealsLayout)
        projectLayout = view.findViewById(R.id.projectLayout)
        dialog = ProgressDialog(activity)
        return view
    }

    constructor() {}
    constructor(isEditable: Boolean) {
        this.isEditable = isEditable
    }

    override fun onResume() {
        super.onResume()
        try {
            (context as DashboardActivity?)!!.mViewHolder.toolbarTitle.text = resources.getString(R.string.profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email!!.isEnabled = false
        phone!!.isEnabled = false
        address!!.isEnabled = false
        designation!!.isEnabled = false
        description!!.isEnabled = false
        name!!.isEnabled = false
        email!!.isFocusable = false
        phone!!.isFocusable = false
        address!!.isFocusable = false
        designation!!.isFocusable = false
        description!!.isFocusable = false
        name!!.isFocusable = false
        projectLayout!!.setOnClickListener { v: View? -> goToFragment(ProjectDealsFragment(true, false, ""), true) }
        dealsLayout!!.setOnClickListener { v: View? ->
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    AlertDialog.Builder(activity!!)
                            .setMessage(this.resources.getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.resources.getString(R.string.close)) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                } else {
                    goToFragment(ProjectDealsFragment(false, false, ""), true)
                }
            } else {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    AlertDialog.Builder(activity!!)
                            .setMessage(this.resources.getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.resources.getString(R.string.close)) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                    goToFragment(ProjectDealsFragment(false, false, ""), true)
                } else {
                    AlertDialog.Builder(activity!!)
                            .setMessage(this.resources.getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.resources.getString(R.string.close)) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                }
            }
        }
        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                setBackGroundTint(edit)
                setBackGroundTint(save)
                setBackGroundTintFAB(fab)
            }
        } else {
            if (!SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                setBackGroundTint(edit)
                setBackGroundTint(save)
                setBackGroundTintFAB(fab)
            }
        }
        try {
            for (countriesData in allCountries.allCountries) {
                countriesList.add(countriesData.name)
            }
            Collections.sort(countriesList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val adapter = ArrayAdapter(activity!!,
                android.R.layout.simple_spinner_item, countriesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        country!!.adapter = adapter
        val adapter2 = ArrayAdapter(activity!!,
                android.R.layout.simple_spinner_item, cityList)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        city!!.adapter = adapter2


//        val arrayList: List<String> = ArrayList(Arrays.asList(*activity!!.resources.getStringArray(R.array.category)))
//        val adapter3 = ArrayAdapter(activity!!,
//                android.R.layout.simple_spinner_item, arrayList)
//        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        department!!.adapter = adapter3




        val adapter4: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.category))
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        department?.setAdapter(adapter4)

        city!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                try {
                    val cityName = cityList[position]
                    for (citiesData in allCities.allCities) {
                        if (citiesData.cityName == cityName) {
                            cityID = citiesData.id.toInt()
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        country!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                val countryName = countriesList[position]
                for (countriesData in allCountries.allCountries) {
                    if (countriesData.name == countryName) {
                        countryID = countriesData.id.toInt()
                        break
                    }
                }
                try {
                    cityList.clear()
                    for (citiesData in allCities.allCities) {
                        if (citiesData.countryID.toInt() == countryID) {
                            cityList.add(citiesData.cityName)
                        }
                    }
                    Collections.sort(cityList)
                    adapter2.notifyDataSetChanged()
                    city!!.setSelection(cityID)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        department!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                try {
                    for (departmentsData in allDepartments.allDepartments) {
                        if (departmentsData.depName == department!!.selectedItem.toString()) {
                            departmentID = departmentsData.id.toInt()
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        chooseImage!!.setOnClickListener { v: View? ->
            simpleResponse = SimpleResponse { response: Uri? -> println(response) }
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "*/*"
            photoPickerIntent = Intent.createChooser(photoPickerIntent, resources.getString(R.string.choseFile))
            startActivityForResult(photoPickerIntent, 1)
        }
        edit!!.setOnClickListener { view1: View? ->
            email!!.isEnabled = true
            phone!!.isEnabled = true
            address!!.isEnabled = true
            designation!!.isEnabled = true
            description!!.isEnabled = true
            name!!.isEnabled = true
            email!!.isFocusableInTouchMode = true
            phone!!.isFocusableInTouchMode = true
            address!!.isFocusableInTouchMode = true
            designation!!.isFocusableInTouchMode = true
            description!!.isFocusableInTouchMode = true
            name!!.isFocusableInTouchMode = true
            countriesLayout!!.visibility = View.VISIBLE
            cityLayout!!.visibility = View.VISIBLE
            addressSec!!.visibility = View.GONE

//            save.setVisibility(View.VISIBLE);
//            edit.setVisibility(View.GONE);
            chooseImage!!.visibility = View.VISIBLE
        }
        save!!.setOnClickListener { view12: View? ->
            val jsonObject = JSONObject()
            try {
                val fullname = name!!.text.toString().split(" ").toTypedArray()
                try {
                    if (!fullname[0].isEmpty()) {
                        jsonObject.put("first_name", fullname[0])
                    }
                    if (!fullname[1].isEmpty()) {
                        jsonObject.put("last_name", fullname[1])
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                jsonObject.put("email", email!!.text.toString())
                jsonObject.put("phone", phone!!.text.toString())
                jsonObject.put("description", description!!.text.toString())
                jsonObject.put("position", designation!!.text.toString())
                jsonObject.put("city_id", cityID)
                jsonObject.put("departments", favorite!!.text.toString())
                Thread(Runnable {
                    ChangeProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, picturePath) { s: String ->
                        activity!!.runOnUiThread {
                            if (s.isEmpty()) {
                                Toast.makeText(activity, resources.getString(R.string.profileChangeError), Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, resources.getString(R.string.profileChangeSuccesss), Toast.LENGTH_LONG).show()
                                email!!.isEnabled = false
                                phone!!.isEnabled = false
                                address!!.isEnabled = false
                                designation!!.isEnabled = false
                                description!!.isEnabled = false
                                name!!.isEnabled = false
                                email!!.isFocusable = false
                                phone!!.isFocusable = false
                                address!!.isFocusable = false
                                designation!!.isFocusable = false
                                description!!.isFocusable = false
                                name!!.isFocusable = false

//                                save.setVisibility(View.GONE);
//                                edit.setVisibility(View.VISIBLE);
                                countriesLayout!!.visibility = View.GONE
                                cityLayout!!.visibility = View.GONE
                                addressSec!!.visibility = View.VISIBLE
                                loadProfile()
                            }
                        }
                        null
                    }
                }).start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (isEditable) {
            editProfile()
            i++
        }
        fab!!.setOnClickListener { view13: View? ->
            if (i % 2 == 0) {
//                fab.setImageResource(R.drawable.save);
                editProfile()
            } else {
                fabText!!.text = resources.getString(R.string.edit)
                chooseImage!!.visibility = View.INVISIBLE
                //                fab.setImageResource(R.drawable.edit);
                isSomethingChanged = false
                saveProfile()
            }
            i++
        }
        loadProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) if (requestCode == 1) {
            val selectedFileUri = data!!.data
            try {
                var scheme: String? = null
                if (selectedFileUri != null) {
                    scheme = selectedFileUri.scheme
                }
                if (scheme != null) {
                    if (scheme == "file") {
                        fileName = selectedFileUri!!.lastPathSegment
                    } else if (scheme == "content") {
                        val proj = arrayOf(MediaStore.Images.Media.TITLE)
                        val cursor = activity!!.contentResolver.query(selectedFileUri!!, proj, null, null, null)
                        if (cursor != null && cursor.count != 0) {
                            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
                            cursor.moveToFirst()
                            imageName = cursor.getString(columnIndex)
                            //                                addImage.setText(imageName);
                        }
                        cursor?.close()
                    }
                }
                picturePath = FilePath.getPath(activity, selectedFileUri)
                try {
                    Glide.with(activity!!)
                            .load(selectedFileUri)
                            .into(profile_image!!)
                    simpleResponse!!.onResponse(selectedFileUri)
                    isSomethingChanged = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadProfile() {
        dialog!!.setMessage(resources.getString(R.string.pleasewait))
        dialog!!.show()
        val data = getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject: JSONObject? ->
            try {
                if (jsonObject == null) {
                    activity!!.runOnUiThread {
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    }
                    return@getUserProfile
                }
                activity!!.runOnUiThread {
                    try {
                        if (jsonObject.has("first_name") || jsonObject.has("last_name")) {
                            val username = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
                            name!!.setText(username)
                        }
                        if (jsonObject.has("email")) {
                            email!!.setText(jsonObject.getString("email"))
                        }
                        if (jsonObject.has("phone")) {
                            phone!!.setText(jsonObject.getString("phone"))
                        }
                        if (jsonObject.has("position")) {
                            designation!!.setText(jsonObject.getString("position"))
                        }
                        if (jsonObject.has("description")) {
                            description!!.setText(jsonObject.getString("description"))
                        }
                        Glide.with(activity!!)
                                .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + jsonObject.getString("image"))
                                .into(profile_image!!)
                        if (jsonObject.has("City")) {
                            val city1 = jsonObject.getJSONObject("City")
//                            cityID = jsonObject.getInt("id")
                            val cityname = city1.getString("city_name")
                            val jsonObject1 = city1.getJSONObject("Country")
                            if (jsonObject1.has("name")) {
                                val countryName = jsonObject1.getString("name")

                                var tempCountryID = 0
                                for (countriesData in allCountries.allCountries) {
                                    if (countriesData.name == countryName) {
                                        tempCountryID = countriesData.id.toInt()
                                        break
                                    }
                                }

                                for (countriesData in countriesList) {
                                    if (countriesData == countryName) {
                                        countryID = countriesList.indexOf(countriesData)
                                        country!!.setSelection(countryID)
                                        break
                                    }
                                }

                                try {
                                    cityList.clear()
                                    for (citiesData in allCities.allCities) {
                                        if (citiesData.countryID.toInt() == tempCountryID) {
                                            cityList.add(citiesData.cityName)
                                        }
                                    }
                                    Collections.sort(cityList)

                                    for (citiesData in cityList) {
                                        if (citiesData == cityname) {
                                            cityID = cityList.indexOf(citiesData)
//                                            city!!.setSelection(cityID)
                                            break
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val res = "$cityname / $countryName"
                                address!!.setText(res)
                            }
                        }
                        //                            if (jsonObject.has("Invest_Projects")) {
//                                try {
//                                    JSONArray jsonArray = jsonObject.getJSONArray("Invest_Projects");
//                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//                                    if (jsonObject1.has("total_invest")) {
//                                        if (jsonObject1.getString("total_invest").contains("null")) {
//                                            totalInvestment.setText("SAR 0");
//                                        } else {
//                                            totalInvestment.setText("SAR " + jsonObject1.getString("total_invest"));
//                                        }
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                            }
                        if (jsonObject.has("Department_Users")) {
                            try {
                                val jsonArray = jsonObject.getJSONArray("Department_Users")
                                val jsonObject1 = jsonArray.getJSONObject(0)
                                if (jsonObject1.has("Department")) {
                                    val jsonObject2 = jsonObject1.getJSONObject("Department")
                                    var res = ""
                                    if (SharedPrefManager.read(SharedPrefManager.LANGUAGE, 0) == 0) {
                                        res = jsonObject2.getString("dep_en")
                                    } else {
                                        res = jsonObject2.getString("dep_ar")
                                    }
                                    departmentID = resources.getStringArray(R.array.category).indexOf(res)
                                    department!!.setSelection(departmentID)
                                    if (res.length > 10) {
                                        favorite!!.textSize = 14f
                                        favorite!!.text = res
                                    } else {
                                        favorite!!.text = res
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

//                            if (jsonObject.has("completed")) {
//                                totalProjects.setText(jsonObject.getString("completed"));
//                            }
                        var totalProject = 0
                        var totalDeals = 0
                        for (projectCatalog in projectCatalog.projectCatalog) {
                            if (projectCatalog.auth == SharedPrefManager.read(SharedPrefManager.USER_ID, "")) {
                                totalProject = totalProject + 1
                            }
                        }
                        for (projectCatalog in projectCatalog.dealsCatalog) {
                            if (projectCatalog.auth == SharedPrefManager.read(SharedPrefManager.USER_ID, "")) {
                                totalDeals = totalDeals + 1
                            }
                        }
                        totalProjects!!.text = totalProject.toString()
                        totalInvestment!!.text = totalDeals.toString()

//                            totalProjects.setText(jsonObject.getString("completed"));
                        email!!.addTextChangedListener(textWatcher)
                        phone!!.addTextChangedListener(textWatcher)
                        address!!.addTextChangedListener(textWatcher)
                        designation!!.addTextChangedListener(textWatcher)
                        description!!.addTextChangedListener(textWatcher)
                        name!!.addTextChangedListener(textWatcher)
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun setBackGroundTint(button: Button?) {
        var buttonDrawable = button!!.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
        DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red))
        button.background = buttonDrawable
    }

    private fun setBackGroundTintFAB(button: FloatingActionButton?) {
        var buttonDrawable = button!!.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
        DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red))
        button.background = buttonDrawable
    }

    fun saveProfile() {
        val jsonObject = JSONObject()
        try {
            val fullname = name!!.text.toString().split(" ").toTypedArray()
            try {
                if (!fullname[0].isEmpty()) {
                    jsonObject.put("first_name", fullname[0])
                }
                if (!fullname[1].isEmpty()) {
                    jsonObject.put("last_name", fullname[1])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val cityName = cityList[cityID]
                for (citiesData in allCities.allCities) {
                    if (citiesData.cityName == cityName) {
                        cityID = citiesData.id.toInt()
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            jsonObject.put("email", email!!.text.toString())
            jsonObject.put("phone", phone!!.text.toString())
            jsonObject.put("description", description!!.text.toString())
            jsonObject.put("position", designation!!.text.toString())
            jsonObject.put("city_id", cityID)
            if (departmentID != 0) {
                val jsonArray = JSONArray()
                jsonArray.put(departmentID)
                jsonObject.put("departments", jsonArray);
            }
            Thread(Runnable {
                ChangeProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, picturePath) { s: String ->
                    activity!!.runOnUiThread {
                        if (s.isEmpty()) {
                            Toast.makeText(activity, resources.getString(R.string.profileChangeError), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.profileChangeSuccesss), Toast.LENGTH_LONG).show()
                            email!!.isEnabled = false
                            phone!!.isEnabled = false
                            address!!.isEnabled = false
                            designation!!.isEnabled = false
                            description!!.isEnabled = false
                            name!!.isEnabled = false
                            email!!.isFocusable = false
                            phone!!.isFocusable = false
                            address!!.isFocusable = false
                            designation!!.isFocusable = false
                            description!!.isFocusable = false
                            name!!.isFocusable = false

                            favorite!!.visibility = View.VISIBLE;
                            department!!.visibility = View.GONE;
//                            save.setVisibility(View.GONE);
//                            edit.setVisibility(View.VISIBLE);
                            countriesLayout!!.visibility = View.GONE
                            cityLayout!!.visibility = View.GONE
                            addressSec!!.visibility = View.VISIBLE
                            loadProfile()
                        }
                    }
                    null
                }
            }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveProfileFormPopup() {
        val jsonObject = JSONObject()
        try {
            val fullname = name!!.text.toString().split(" ").toTypedArray()
            try {
                if (!fullname[0].isEmpty()) {
                    jsonObject.put("first_name", fullname[0])
                }
                if (!fullname[1].isEmpty()) {
                    jsonObject.put("last_name", fullname[1])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val cityName = cityList[cityID]
                for (citiesData in allCities.allCities) {
                    if (citiesData.cityName == cityName) {
                        cityID = citiesData.id.toInt()
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            jsonObject.put("email", email!!.text.toString())
            jsonObject.put("phone", phone!!.text.toString())
            jsonObject.put("description", description!!.text.toString())
            jsonObject.put("position", designation!!.text.toString())
            jsonObject.put("city_id", cityID)
            if (departmentID != 0) {
                val jsonArray = JSONArray()
                jsonArray.put(departmentID)
                jsonObject.put("departments", jsonArray);
            }
            Thread(Runnable { ChangeProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, "") { s: String? -> null } }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun editProfile() {
        fabText!!.text = resources.getString(R.string.save)
        email!!.isEnabled = true
        phone!!.isEnabled = true
        address!!.isEnabled = true
        designation!!.isEnabled = true
        description!!.isEnabled = true
        name!!.isEnabled = true
        email!!.isFocusableInTouchMode = true
        phone!!.isFocusableInTouchMode = true
        address!!.isFocusableInTouchMode = true
        designation!!.isFocusableInTouchMode = true
        description!!.isFocusableInTouchMode = true
        name!!.isFocusableInTouchMode = true
        countriesLayout!!.visibility = View.VISIBLE
        cityLayout!!.visibility = View.VISIBLE
        addressSec!!.visibility = View.GONE

//                save.setVisibility(View.VISIBLE);
//                edit.setVisibility(View.GONE);
        chooseImage!!.visibility = View.VISIBLE
        favorite!!.visibility = View.GONE
        department!!.visibility = View.VISIBLE
        country!!.setSelection(countryID)
        city!!.setSelection(cityID)
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            println("Something changed")
            isSomethingChanged = true
        }
    }

    override fun onBackPressed(): Boolean {
        return if (isSomethingChanged) {
            AlertDialog.Builder(activity!!)
                    .setTitle(activity!!.resources.getString(R.string.saveProfile))
                    .setMessage(activity!!.resources.getString(R.string.save_message))
                    .setPositiveButton(activity!!.resources.getString(R.string.save)) { dialog: DialogInterface?, which: Int ->
                        isSomethingChanged = false
                        saveProfileFormPopup()
                        isSomethingChanged = false
                        try {
//                            getActivity().getFragmentManager().popBackStack();
                            activity!!.onBackPressed()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton(this.resources.getString(R.string.close)) { dialog, which ->
                        isSomethingChanged = false
                        try {
//                                getActivity().getFragmentManager().popBackStack();
                            activity!!.onBackPressed()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setOnDismissListener { }
                    .show()
            true
        } else {
            false
        }
    }

    interface DialogSingleButtonListener {
        fun onButtonClicked(dialog: DialogInterface?)
    }

    private fun goToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.replace(R.id.container, fragment).commit()
    }
}