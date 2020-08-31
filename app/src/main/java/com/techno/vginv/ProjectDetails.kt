package com.techno.vginv

import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.techno.vginv.Adapters.AttachmentsAdapter
import com.techno.vginv.Adapters.CommentsAdapter
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.Model.News
import com.techno.vginv.Model.ProjectCatalog
import com.techno.vginv.Model.ProjectComments
import com.techno.vginv.Views.EmptyRecyclerView
import com.techno.vginv.utils.CloudDataService
import com.techno.vginv.utils.CloudDataService.postComment
import com.techno.vginv.utils.CloudDataService.postInvestment
import com.techno.vginv.utils.CloudDataService.postLike
import com.techno.vginv.utils.SharedPrefManager
import com.techno.vginv.utils.Version
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ProjectDetails : AppCompatActivity() {
    private var heading: TextView? = null
    private var description: TextView? = null
    private var totalInvestment: TextView? = null
    private var totalLike: TextView? = null
    private var comment: EditText? = null
    private var imageView: ImageView? = null
    private var like: ImageView? = null
    private var progressBar: ProgressBar? = null
    var alert: AlertDialog.Builder? = null
    var liked = false
    var commentsAdapter: CommentsAdapter? = null
    var attachmentsAdapter: AttachmentsAdapter? = null
    val projects = ArrayList<ProjectComments>()
    private var frameLayout: FrameLayout? = null
    private var investButton : Button? = null
    private var downloadFiles : Button? = null
    private var budget = ""
    private var investment = ""


    override fun getTheme(): Resources.Theme? {
        val theme = super.getTheme()
        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true)
            } else {
                theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true)
            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true)
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true)
            } else {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true)
            }
        }
        // you could also use a switch if you have many themes that could apply
        return theme
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)
        alert = AlertDialog.Builder(this)
        heading = findViewById(R.id.heading)
        frameLayout = findViewById(R.id.container)
        description = findViewById(R.id.description)
        imageView = findViewById(R.id.image)
        totalInvestment = findViewById(R.id.totalInvestment)
        like = findViewById(R.id.like)
        totalLike = findViewById(R.id.totalLike)
        progressBar = findViewById(R.id.progress_choice_1)
        comment = findViewById(R.id.comment)
        investButton = findViewById(R.id.invest)
        downloadFiles = findViewById(R.id.downloadFiles)


        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                investButton?.text = resources.getString(R.string.InvestInProject)
                fetchProjects()
            } else {
                investButton?.text = resources.getString(R.string.InvestInDeal)
                setBackgroundTint(investButton)
                setBackgroundTint(downloadFiles)
                fetchDeals()
            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                investButton?.text = resources.getString(R.string.InvestInProject)
                fetchProjects()
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                investButton?.text = resources.getString(R.string.InvestInDeal)
                setBackgroundTint(investButton)
                setBackgroundTint(downloadFiles)
                fetchDeals()
            } else {
                investButton?.text = resources.getString(R.string.InvestInProject)
                fetchProjects()
            }
        }

        val layoutManager = LinearLayoutManager(this)
        val mRecyclerView = findViewById<EmptyRecyclerView>(R.id.commentsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = layoutManager

        val attachmentsLayoutManager = LinearLayoutManager(this)
        val attachmentsRecyclerView = findViewById<EmptyRecyclerView>(R.id.attachmentsView)
        attachmentsRecyclerView.setHasFixedSize(true)
        attachmentsRecyclerView.layoutManager = attachmentsLayoutManager


//        try {
//            for (projectDetails in SharedInstance.projectCatalog.projectCatalog) {
//                for (projectComments in projectDetails.projectComments) {
//                    if (projectComments.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
//                        projects.add(projectComments)
//                    }
//                }
//            }
//        } catch (e: Exception) {
//
//        }

        commentsAdapter = CommentsAdapter(this, projects, frameLayout)
        mRecyclerView.adapter = commentsAdapter

        attachmentsAdapter = AttachmentsAdapter(this, mProjects!!.projectID)
        attachmentsRecyclerView.adapter = attachmentsAdapter

        Glide.with(applicationContext.applicationContext)
                .load(url)
                .into(imageView!!)
        heading?.setText(mProjects!!.title)
        description?.setText(mProjects!!.description)

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                totalLike?.setText(mProjects!!.date + " " + resources.getString(R.string.project_like_vg))
            } else {
                totalLike?.setText(mProjects!!.date + " " + resources.getString(R.string.project_like_hmg))
            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                totalLike?.setText(mProjects!!.date + " " + resources.getString(R.string.project_like_vg))
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                totalLike?.setText(mProjects!!.date + " " + resources.getString(R.string.project_like_hmg))
            } else {
                totalLike?.setText(mProjects!!.date + " " + resources.getString(R.string.project_like_vg))
            }
        }

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                fetchProjectsLikes()
            } else {
                fetchDealsLikes()
            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                fetchProjectsLikes()
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                fetchDealsLikes()
            } else {
                fetchProjectsLikes()
            }
        }

//        try {
//            for (projectDetails in SharedInstance.projectCatalog.projectCatalog) {
//                for (projectLike in projectDetails.projectLikes) {
//                    if (projectLike.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
//                        if (projectLike.id.equals(SharedPrefManager.read(SharedPrefManager.USER_ID, ""))) {
//                            like?.setImageResource(R.drawable.liked)
//                            liked = true
//                            break
//                        }
//                    }
//                }
//            }
//        } catch (e: java.lang.Exception) {
//
//        }


        try {
            if (mProjects!!.trailTextHtml.contains(resources.getString(R.string.budget))) {
                val totalInvestmen = mProjects!!.trailTextHtml.split(":").toTypedArray()
                budget = totalInvestmen[1].trim { it <= ' ' }
                budget = if (budget.isEmpty()) "0" else budget
                progressBar?.setMax(budget.toInt())
            } else {
                progressBar?.setMax(mProjects!!.trailTextHtml.toInt())
            }
        } catch (e: Exception) {
        }
        try {
            if (mProjects!!.author.contains(resources.getString(R.string.Total_Investment))) {
                val totalInvestmen1 = mProjects!!.author.split(":").toTypedArray()
                investment = totalInvestmen1[1].trim { it <= ' ' }
                investment = if (investment.isEmpty()) "0" else investment
                progressBar?.setProgress(investment.toInt())
                totalInvestment?.setText(investment)
            } else {
                progressBar?.setProgress(mProjects!!.author.toInt())
                totalInvestment?.setText(mProjects!!.author)
            }
        } catch (e: Exception) {

        }

        try {
            if (Version.isEqual(investment, budget) || Version.isGreater(investment, budget)) {
                investButton?.isEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        comment?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == IME_ACTION_DONE) {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("createdAt", Date().time)
                        jsonObject.put("projectId",mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))
                        jsonObject.put("comment", comment?.text.toString())
                        postComment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                            runOnUiThread {
                                Toast.makeText(this@ProjectDetails, s, Toast.LENGTH_SHORT).show()
                                recallProjects()
                            }
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
                return false
            }
        })

        comment?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("createdAt", Date().time)
                        jsonObject.put("projectId", mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))
                        jsonObject.put("comment", comment?.text.toString())
                        postComment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                            runOnUiThread { Toast.makeText(this@ProjectDetails, s, Toast.LENGTH_SHORT).show()
                                comment?.text!!.clear()
                                recallProjects()
                            }
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
                return false
            }
        })

        like?.setOnClickListener(View.OnClickListener { v: View? ->
            if (liked) {
                return@OnClickListener
            }
            val jsonObject = JSONObject()
            try {
                jsonObject.put("createdAt", Date().time)
                jsonObject.put("projectId", mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))
                postLike(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                    runOnUiThread { Toast.makeText(this@ProjectDetails, s, Toast.LENGTH_SHORT).show() }
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        frameLayout?.visibility = View.INVISIBLE
    }

    fun recallProjects() {
        try {
            projects.clear()
            CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject ->
                try {
                    SharedInstance.projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)
                    try {
                        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                                fetchProjects()
                            } else {
                                fetchDeals()
                            }
                        } else {
                            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                                fetchProjects()
                            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                                fetchDeals()
                            } else {
                                fetchProjects()
                            }
                        }

//                        for (projectDetails in SharedInstance.projectCatalog.projectCatalog) {
//                            for (projectComments in projectDetails.projectComments) {
//                                if (projectComments.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
//                                    projects.add(projectComments)
//                                }
//                            }
//                        }
                    } catch (e: Exception) {

                    }
                    runOnUiThread {
                        commentsAdapter!!.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBackgroundTint(button: Button?) {
        var buttonDrawable: Drawable = button?.background!!
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red))
        button?.background = buttonDrawable
    }

    fun downloadFiles(view: View?) {
        for (projectAssets in mProjects!!.projectID) {
            try {
                val newsUri = Uri.parse(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectAssets.path)
                val websiteIntent = Intent(Intent.ACTION_VIEW, newsUri)
                startActivity(websiteIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun investInProject(view: View?) {
//        AlertDialog.Builder(this)
//                .setTitle(resources.getString(R.string.call_message))
//                .setMessage(resources.getString(R.string.hmg_members_message))
//                .setPositiveButton(resources.getString(R.string.yes), null)
//                .setNegativeButton(resources.getString(R.string.no), null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show()

        AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.project_interest))
                .setMessage(resources.getString(R.string.call_message))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface?, which: Int ->
                    try {
                        MaterialDialog(this).show {
                            title(text = "Select Date and Time")
                            dateTimePicker(requireFutureDateTime = true, show24HoursView = true) { _, dateTime ->
                                toast(resources.getString(R.string.call_scheduled) + "\n Selected date/time: ${dateTime.formatDateTime()}")
                                sendCallDetails(dateTime.formatDate(), dateTime.formatTime().replace("PM", "").replace("AM", ""))
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface?, which: Int ->
                    try {
                        sendInterested();
                        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                                toast(resources.getString(R.string.thanks_keep_exploring_projects))
                                Toast.makeText(this, resources.getString(R.string.thanks_keep_exploring_projects), Toast.LENGTH_SHORT).show();
                                onBackPressed()
                            } else {
                                Toast.makeText(this, resources.getString(R.string.thanks_keep_exploring_deals), Toast.LENGTH_SHORT).show();
//                                toast(resources.getString(R.string.thanks_keep_exploring_deals))
                                onBackPressed()
                            }
                        } else {
                            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                                toast(resources.getString(R.string.thanks_keep_exploring_projects))
                                Toast.makeText(this, resources.getString(R.string.thanks_keep_exploring_projects), Toast.LENGTH_SHORT).show();
                                onBackPressed()
                            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
//                                toast(resources.getString(R.string.thanks_keep_exploring_deals))
                                Toast.makeText(this, resources.getString(R.string.thanks_keep_exploring_deals), Toast.LENGTH_SHORT).show();
                                onBackPressed()
                            } else {
//                                toast(resources.getString(R.string.thanks_keep_exploring_projects))
                                Toast.makeText(this, resources.getString(R.string.thanks_keep_exploring_projects), Toast.LENGTH_SHORT).show();
                                onBackPressed()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

//        val edittext = EditText(this)
//        edittext.inputType = InputType.TYPE_CLASS_NUMBER
//        alert!!.setMessage(resources.getString(R.string.enter_amount) + " SAR")
//        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
//            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                alert!!.setTitle(resources.getString(R.string.InvestInProject))
//            } else {
//                alert!!.setTitle(resources.getString(R.string.InvestInDeal))
//            }
//        } else {
//            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                alert!!.setTitle(resources.getString(R.string.InvestInProject))
//            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
//                alert!!.setTitle(resources.getString(R.string.InvestInDeal))
//            } else {
//                alert!!.setTitle(resources.getString(R.string.InvestInProject))
//            }
//        }
//        alert!!.setView(edittext)
//        alert!!.setPositiveButton(resources.getString(R.string.submit)) { dialog: DialogInterface?, whichButton: Int ->
//            val YouEditTextValue = edittext.text.toString()
//            val jsonObject = JSONObject()
//            try {
//                jsonObject.put("createdAt", Date().time)
//                jsonObject.put("projectId", mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))
//                jsonObject.put("amount", YouEditTextValue)
////                if (YouEditTextValue.isEmpty()) {
////                    return@setPositiveButton
////                }
//                postInvestment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
//                    runOnUiThread {
//                        Toast.makeText(this@ProjectDetails, s, Toast.LENGTH_SHORT).show()
//                        try {
//                            if (mProjects!!.author.contains(resources.getString(R.string.Total_Investment))) {
//                                val totalInvestmen1 = mProjects!!.author.split(":").toTypedArray()
//                                var invest = totalInvestmen1[1].trim { it <= ' ' }
//                                invest = if (invest.isEmpty()) "0" else invest
//                                progressBar?.setProgress(invest.toInt())
//                                totalInvestment?.setText(invest)
//                            } else {
//                                progressBar?.setProgress(mProjects!!.author.toInt())
//                                totalInvestment?.setText(mProjects!!.author)
//                            }
//                        } catch (e: Exception) {
//
//                        }
//                    }
//                    null
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        alert!!.setNegativeButton(resources.getString(R.string.close)) { dialog: DialogInterface?, whichButton: Int -> }
//        alert!!.show()
    }

    fun sendCallDetails(date: String, time: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("createdAt", Date().time)
            jsonObject.put("call_schedule_date", date)
            jsonObject.put("call_schedule_time", time)
            jsonObject.put("status", 2)
            jsonObject.put("projectId", mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))

            postInvestment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendInterested() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("createdAt", Date().time)
            jsonObject.put("status", 0)
            jsonObject.put("projectId", mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))

            postInvestment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchProjects() {
        try {
            for (projectDetails in SharedInstance.projectCatalog.projectCatalog) {
                for (projectComments in projectDetails.projectComments) {
                    if (projectComments.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
                        projects.add(projectComments)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    fun fetchDeals() {
        try {
            for (projectDetails in SharedInstance.projectCatalog.dealsCatalog) {
                for (projectComments in projectDetails.projectComments) {
                    if (projectComments.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
                        projects.add(projectComments)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    fun fetchProjectsLikes() {
        try {
            for (projectDetails in SharedInstance.projectCatalog.projectCatalog) {
                for (projectLike in projectDetails.projectLikes) {
                    if (projectLike.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
                        if (projectLike.id.equals(SharedPrefManager.read(SharedPrefManager.USER_ID, ""))) {
                            like?.setImageResource(R.drawable.liked)
                            liked = true
                            break
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }

    fun fetchDealsLikes() {
        try {
            for (projectDetails in SharedInstance.projectCatalog.dealsCatalog) {
                for (projectLike in projectDetails.projectLikes) {
                    if (projectLike.projectID.equals(mProjects!!.url.substring(mProjects!!.url.lastIndexOf("/") + 1).replace("/", ""))) {
                        if (projectLike.id.equals(SharedPrefManager.read(SharedPrefManager.USER_ID, ""))) {
                            like?.setImageResource(R.drawable.liked)
                            liked = true
                            break
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }

    companion object {
        private var url = ""
        private var mProjects: News? = null
        @JvmStatic
        fun open(context: Context, projects: News) {
            url = projects.thumbnail
            mProjects = projects
            context.startActivity(Intent(context, ProjectDetails::class.java))
        }
    }
}