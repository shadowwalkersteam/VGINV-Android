package com.techno.vginv.Fragments


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.techno.vginv.Adapters.NewsAdapter
import com.techno.vginv.Adapters.ShortcutsAdapter
import com.techno.vginv.AddNewProject
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.DashboardActivity
import com.techno.vginv.Loaders.NewsLoader
import com.techno.vginv.Model.News
import com.techno.vginv.Model.PollsData
import com.techno.vginv.Model.ProjectCatalog
import com.techno.vginv.R
import com.techno.vginv.SharedInstance
import com.techno.vginv.SharedInstance.allPollsData
import com.techno.vginv.Views.EmptyRecyclerView
import com.techno.vginv.databinding.ViewItemBinding
import com.techno.vginv.utils.CloudDataService
import com.techno.vginv.utils.CloudDataService.getAllPollsData
import com.techno.vginv.utils.CloudDataService.postPolls
import com.techno.vginv.utils.CloudDataService.toggleUser
import com.techno.vginv.utils.NewsPreferences
import com.techno.vginv.utils.QueryUtils
import com.techno.vginv.utils.SharedPrefManager
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class MainFragment : Fragment(), LoaderManager.LoaderCallbacks<List<News>> {
    private var recyclerView: RecyclerView? = null
    private var pollsRecycler: RecyclerView? = null
    private var mAdapter: NewsAdapter? = null
    private var projectsAdapter: NewsAdapter? = null
    private var addNewProject: Button? = null
    private var loadMore: Button? = null
    private var dialog: ProgressDialog? = null
    private var timer: Timer? = null
    private var pollQuestion: TextView? = null
    private var projectHeading: TextView? = null
    private var dataDetail: TextView? = null
    private var dataDetail2: TextView? = null
    private var searchView: SearchView? = null
    private var hmgVgWarning: LinearLayout? = null
    private var warningMessage: TextView? = null

    private// Get a reference to the ConnectivityManager to check state of network connectivity
    // Get details on the currently active default data network
    val isConnected: Boolean
        get() {
            val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            return networkInfo != null && networkInfo.isConnected
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = view.findViewById(R.id.rvShortcuts)
        pollsRecycler = view.findViewById(R.id.polls)
        addNewProject = view.findViewById(R.id.addNewProject)
        pollQuestion = view.findViewById(R.id.pollQuestion)
        projectHeading = view.findViewById(R.id.projectHeading)
        dataDetail = view.findViewById(R.id.dataDetail)
        dataDetail2 = view.findViewById(R.id.dataDetail2)
        searchView = view.findViewById(R.id.searchView)
        hmgVgWarning = view.findViewById(R.id.hmgVgWarning)
        warningMessage = view.findViewById(R.id.warningMessage)
        loadMore = view.findViewById(R.id.loadMore)
        dialog = ProgressDialog(activity)

        hmgVgWarning?.setOnClickListener({
            switchUser()
        })

//        val binding = DataBindingUtil.setContentView<>(this, R.layout.activity_main)
//        binding.recyclerView.apply {
//            adapter = PersonAdapter(listOf(Person("Larry"), Person("Moe"), Person("Curly")))
//            layoutManager = LinearLayoutManager(context)
//        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var shortcutsAdapter: ShortcutsAdapter? = null

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                shortcutsAdapter = ShortcutsAdapter(context, ArrayList(Arrays.asList(*resources.getStringArray(R.array.shortcutOptions))))
                projectHeading?.text = resources.getString(R.string.project)
                addNewProject?.text = resources.getString(R.string.AddNewProject)
                loadMore?.text = resources.getString(R.string.load_more)
            } else {
                shortcutsAdapter = ShortcutsAdapter(context, ArrayList(Arrays.asList(*resources.getStringArray(R.array.shortcutOptionsHMG))))
                projectHeading?.text = resources.getString(R.string.Deals)
                addNewProject?.text = resources.getString(R.string.AddNewDeal)
                var buttonDrawable: Drawable = addNewProject?.background!!
                buttonDrawable = DrawableCompat.wrap(buttonDrawable)
                DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red))
                addNewProject?.background = buttonDrawable
                loadMore?.text = resources.getString(R.string.load_more_deals)

            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                shortcutsAdapter = ShortcutsAdapter(context, ArrayList(Arrays.asList(*resources.getStringArray(R.array.shortcutOptions))))
                projectHeading?.text = resources.getString(R.string.project)
                addNewProject?.text = resources.getString(R.string.AddNewProject)
                loadMore?.text = resources.getString(R.string.load_more)
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                shortcutsAdapter = ShortcutsAdapter(context, ArrayList(Arrays.asList(*resources.getStringArray(R.array.shortcutOptionsHMG))))
                projectHeading?.text = resources.getString(R.string.Deals)
                addNewProject?.text = resources.getString(R.string.AddNewDeal)
                var buttonDrawable: Drawable = addNewProject?.background!!
                buttonDrawable = DrawableCompat.wrap(buttonDrawable)
                DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red))
                addNewProject?.background = buttonDrawable
                loadMore?.text = resources.getString(R.string.load_more_deals)
            } else {
                shortcutsAdapter = ShortcutsAdapter(context, ArrayList(Arrays.asList(*resources.getStringArray(R.array.shortcutOptions))))
                projectHeading?.text = resources.getString(R.string.project)
                addNewProject?.text = resources.getString(R.string.AddNewProject)
                loadMore?.text = resources.getString(R.string.load_more)
            }
        }
        recyclerView!!.layoutManager = GridLayoutManager(context, 3)
        recyclerView!!.adapter = shortcutsAdapter


        // Find a reference to the {@link RecyclerView} in the layout
        // Replaced RecyclerView with EmptyRecyclerView
        val layoutManager = LinearLayoutManager(activity)
        val layoutManager2 = LinearLayoutManager(activity)

        val mRecyclerView = view.findViewById<EmptyRecyclerView>(R.id.projectsRecyclerView)
        mRecyclerView.setHasFixedSize(true)

        val newsRecyclerView = view.findViewById<EmptyRecyclerView>(R.id.newsRecyclerView)
        newsRecyclerView.setHasFixedSize(true)

        // Set the layoutManager on the {@link RecyclerView}
        mRecyclerView.layoutManager = layoutManager
        newsRecyclerView.layoutManager = layoutManager2
//        mRecyclerView.addItemDecoration(HeaderFooterDecoration(5, 5))



        try {
            CloudDataService.getAllPosts(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject ->
                try {
                    val news = QueryUtils.extractData(jsonObject)
                    activity!!.runOnUiThread {
                        if (news.size <=0 ) {
                            dataDetail2?.visibility = View.VISIBLE
                        }
                        mAdapter = NewsAdapter(activity, news, false)
                        newsRecyclerView.adapter = mAdapter
                    }
                } catch (e: java.lang.Exception) {
                    activity!!.runOnUiThread {
                        dataDetail2?.visibility = View.VISIBLE
                    }
                }
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dataDetail2?.visibility = View.VISIBLE
        }

        try {
            CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject ->
                try {
                    if (jsonObject.has("id")) {
                        val id = jsonObject.getInt("id")
                        SharedPrefManager.write(SharedPrefManager.USER_ID, id.toString())
                    }
                    if (jsonObject.has("position")) {
                        val positiin = jsonObject.getString("position")
                        SharedPrefManager.write(SharedPrefManager.USER_DESIGNATION, positiin)
                    }

                    SharedPrefManager.write(SharedPrefManager.USER_TYPE, jsonObject.getString("type"))
                    SharedPrefManager.write(SharedPrefManager.LOGGED_IN_USER_TYPE, jsonObject.getString("type"))
                    val name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
                    SharedPrefManager.write(SharedPrefManager.USER_NAME, name)
                    SharedPrefManager.write(SharedPrefManager.USER_PORIFLE_PIC, jsonObject.getString("image"))

                    val projects = ArrayList<News>(5)
                    val finalProjects = ArrayList<News>()
                    val projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)
                    SharedInstance.projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)

                    if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                        if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                            projects.addAll(fetchProjects(projectCatalog))
                        } else {
                            projects.addAll(fetchDeals(projectCatalog))
                        }
                    } else {
                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                            projects.addAll(fetchProjects(projectCatalog))
                        } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                            projects.addAll(fetchDeals(projectCatalog))
                        } else {
                            projects.addAll(fetchProjects(projectCatalog))
                        }
                    }

//                    for (projectDetails in projectCatalog.projectCatalog) {
//                        val title = projectDetails.title
//                        val description = projectDetails.description
//                        var thumbnail = ""
//                        try {
//                            thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                        val budget = resources.getString(R.string.budget) + ": SAR " + projectDetails.budget
//                        val investment = resources.getString(R.string.Total_Investment) + ": SAR " + projectDetails.investment
//                        val news = News(title, resources.getString(R.string.business), investment, projectDetails.projectLikes.size.toString(), ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
//                    }

                    projects.reverse()

                    for (news in projects) {
                        if (finalProjects.size <= 4) {
                            finalProjects.add(news)
                        }
                    }
                    projects.clear()

                    activity!!.runOnUiThread {
                        if (finalProjects.size <= 0) {
                            dataDetail?.visibility = View.VISIBLE
                            loadMore?.visibility = View.INVISIBLE
                        }
                        projectsAdapter = NewsAdapter(activity, finalProjects, true)
                        mRecyclerView.adapter = projectsAdapter
                        recyclerView!!.adapter = shortcutsAdapter
                        recyclerView!!.adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity!!.runOnUiThread {
                        dataDetail?.visibility = View.VISIBLE
                        loadMore?.visibility = View.INVISIBLE
                    }
                }

                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dataDetail?.visibility = View.VISIBLE
            loadMore?.visibility = View.INVISIBLE
        }

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query:String):Boolean {
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                projectsAdapter?.filter?.filter(newText)
                return false
            }
        })

        addNewProject!!.setOnClickListener {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    val intent = Intent(activity, AddNewProject()::class.java)
                    intent.putExtra("type", "projects")
                    startActivity(intent)
                } else {
                    val intent = Intent(activity, AddNewProject()::class.java)
                    intent.putExtra("type", "deals")
                    startActivity(intent)
                }
            } else {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    val intent = Intent(activity, AddNewProject()::class.java)
                    intent.putExtra("type", "projects")
                    startActivity(intent)
                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                    val intent = Intent(activity, AddNewProject()::class.java)
                    intent.putExtra("type", "deals")
                    startActivity(intent)
                } else {
                    val intent = Intent(activity, AddNewProject()::class.java)
                    intent.putExtra("type", "projects")
                    startActivity(intent)
                }
            }
        }

        loadMore!!.setOnClickListener {
            goToFragment(ProjectsFragment(), true)
        }
    }

    private fun goToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.replace(R.id.container, fragment).commit()
    }

    fun fetchProjects(projectCatalog: ProjectCatalog) : ArrayList<News> {
        val projects = ArrayList<News>()
        for (projectDetails in projectCatalog.projectCatalog) {
//            if (projects.size <= 4) {
//
//            }
            val title = projectDetails.title
            val description = projectDetails.description
            var thumbnail = ""
            try {
                thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val budget = resources.getString(R.string.budget) + ": SAR " + projectDetails.budget
            val investment = resources.getString(R.string.Total_Investment) + ": SAR " + projectDetails.investment
            val news = News(title, resources.getString(R.string.business), investment, projectDetails.projectLikes.size.toString(), ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
            projects.add(news)
        }
        return projects
    }

    fun fetchDeals(projectCatalog: ProjectCatalog) : ArrayList<News> {
        val projects = ArrayList<News>()
        for (projectDetails in projectCatalog.dealsCatalog) {
//            if (projects.size <= 4) {
//
//            }
            val title = projectDetails.title
            val description = projectDetails.description
            var thumbnail = ""
            try {
                thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val budget = resources.getString(R.string.budget) + ": SAR " + projectDetails.budget
            val investment = resources.getString(R.string.Total_Investment) + ": SAR " + projectDetails.investment
            val news = News(title, resources.getString(R.string.business), investment, projectDetails.projectLikes.size.toString(), ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
            projects.add(news)
        }
        return projects
    }

    override fun onResume() {
        super.onResume()
        try {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Home_vg)
                } else {
                    (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Home_hmg)
                }
            } else {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                    (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Home_vg)
                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                    (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Home_hmg)
                } else {
                    (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Home_vg)
                }
            }
        } catch (e: Exception) {

        }

        try {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true) && SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("hmg", ignoreCase = true)) {
                    hmgVgWarning?.visibility = View.VISIBLE
                    warningMessage?.text = resources.getString(R.string.switchback_hmg)
                } else if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("hmg", ignoreCase = true) && SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)){
                    hmgVgWarning?.visibility = View.VISIBLE
                    warningMessage?.text = resources.getString(R.string.switchback_vg)
                }
            }
        } catch (e: Exception) {

        }

        try {
            dialog!!.setMessage(resources.getString(R.string.pleasewait))
            dialog!!.show()

//            timer = Timer()
//            timer?.schedule(object : TimerTask() {
//                override fun run() {
//                    try {
//
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }, 0, 3000)

            getAllPollsData(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { s: String? ->
                allPollsData = Gson().fromJson(s, PollsData::class.java)
                val list: ArrayList<Polls>? = ArrayList()
                for (getPoll in allPollsData.pollsData.getAllPolls) {
                    for (pollAnswers in getPoll.pollQuestions) {
                        val pollObject = Polls(pollAnswers.answer, pollAnswers.id, getPoll.id)
                        list?.add(pollObject)
                    }
                }
                activity?.runOnUiThread {
                    try {
                        pollQuestion?.text = allPollsData.pollsData.getAllPolls.get(0).question
                        pollsRecycler!!.layoutManager = LinearLayoutManager(context)
                        pollsRecycler!!.adapter = PollsAdapter(list!!, context!!, this)
                        pollsRecycler!!.adapter?.notifyDataSetChanged()
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    } catch (e: Exception) {
                        if (dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    }
                }
                null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        //        restartLoader(isConnected());
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<News>> {
        val uriBuilder = NewsPreferences.getPreferredUri(context)

        // Create a new loader for the given URL
        return NewsLoader(activity, uriBuilder.toString())
    }

    override fun onLoadFinished(loader: Loader<List<News>>, data: List<News>?) {
        // Clear the adapter of previous news data
        mAdapter!!.clearAll()

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the recyclerView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter!!.addAll(data)
        }
    }

    override fun onLoaderReset(loader: Loader<List<News>>) {
        mAdapter!!.clearAll()
    }

    private fun initializeLoader(isConnected: Boolean) {
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            val loaderManager = loaderManager
            // Initialize the loader with the NEWS_LOADER_ID
            loaderManager.initLoader(NEWS_LOADER_ID, null, this)
        } else {
            println("Device is not connected to the internet")
        }
    }

    private fun restartLoader(isConnected: Boolean) {
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            val loaderManager = loaderManager
            // Restart the loader with the NEWS_LOADER_ID
            loaderManager.restartLoader(NEWS_LOADER_ID, null, this)
        } else {
            println("Device is not connected to the internet")
        }
    }

    companion object {
        private val NEWS_LOADER_ID = 1
    }

    fun switchUser() {
        if (SharedPrefManager.read(SharedPrefManager.SWITCH_POPUP, false)) {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("hello", "1234")
                toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String ->
                    (context as DashboardActivity).runOnUiThread {
                        if (s.contains("switched to hmg")) {
                            SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "HMG")
                        } else {
                            SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "VG")
                        }
                        SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true)
                        try {
                            Thread.sleep(2000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        val intent = (context as DashboardActivity).intent
                        (context as DashboardActivity).finish()
                        (context as DashboardActivity).startActivity(intent)
                        (context as DashboardActivity).overridePendingTransition(0, 0)
                    }
                    null
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            AlertDialog.Builder(context!!)
                    .setTitle(activity?.resources?.getString(R.string.switchusertitle))
                    .setMessage(activity?.resources?.getString(R.string.switchmsg))
                    .setPositiveButton(activity?.resources?.getString(R.string.agree)) { dialog: DialogInterface?, which: Int ->
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put("hello", "1234")
                            toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String ->
                                (context as DashboardActivity).runOnUiThread {
                                    if (s.contains("switched to hmg")) {
                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "HMG")
                                    } else {
                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "VG")
                                    }
                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true)
                                    try {
                                        Thread.sleep(2000)
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                    val intent = (context as DashboardActivity).intent
                                    (context as DashboardActivity).finish()
                                    (context as DashboardActivity).startActivity(intent)
                                    (context as DashboardActivity).overridePendingTransition(0, 0)
                                }
                                null
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton(activity?.resources?.getString(R.string.close), null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
    }
}

data class Polls(val answer: String, val id: String, val questionID: String)

class PollsAdapter(polls: List<Polls>, context: Context, fragment: Fragment) : RadioAdapter<Polls>(polls, context, fragment) {
    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.textView.text = this[position].answer

        for (pollVotes in allPollsData.pollsData.pollVotes) {
            if (pollVotes.answerId == this[position].id) {
                val count = pollVotes.count.toInt()
                val total = pollVotes.total.toInt()
                val percentage = 100.0 * count / total
                var text = percentage.roundToInt().toString()
                text = "$text%"
                holder.binding.percentage.text = text
                holder.binding.progressChoice1.progress = percentage.roundToInt()
            }
        }
    }
}

class HeaderFooterDecoration(private val headerHeight: Int, private val footerHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return
        when (parent.getChildAdapterPosition(view)) {
            0 -> outRect.top = headerHeight
            adapter.itemCount - 1 -> outRect.bottom = footerHeight
            else -> outRect.set(0, 0, 0, 0)
        }
    }
}

abstract class RadioAdapter<T>(private val items: List<T>, private val context: Context,  private val fragment: Fragment) : RecyclerView.Adapter<RadioAdapter<T>.RadioViewHolder>() {

    private var selectedPosition = -1

    inner class RadioViewHolder(val binding: ViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val clickHandler: (View) -> Unit = {
            try {
                var jsonObject = JSONObject()
                jsonObject.put("questionId", (items[position] as Polls).questionID)
                jsonObject.put("answerId", (items[position] as Polls).id)
                postPolls(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject) { s: String? ->
                    getAllPollsData(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { response: String? ->
                        allPollsData = Gson().fromJson(response, PollsData::class.java)
                        val list: ArrayList<Polls>? = ArrayList()
                        for (getPoll in allPollsData.pollsData.getAllPolls) {
                            for (pollAnswers in getPoll.pollQuestions) {
                                val pollObject = Polls(pollAnswers.answer, pollAnswers.id, getPoll.id)
                                list?.add(pollObject)
                                PollsAdapter(list!!, context, fragment)
//                                (context as Activity).runOnUiThread {
//                                    notifyDataSetChanged()
//                                }
                            }
                        }
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, context.resources.getString(R.string.ThanksforSubmittingthePoll), Toast.LENGTH_SHORT).show()
                            (fragment as MainFragment).onResume()
                        }
                        null
                    }
                    null
                }
                selectedPosition = adapterPosition
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        init {
            binding.apply {
                root.setOnClickListener(clickHandler)
                radioButton.setOnClickListener(clickHandler)
            }
        }
    }

    override fun getItemCount() = items.size

    operator fun get(position: Int): T = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DataBindingUtil.inflate<ViewItemBinding>(LayoutInflater.from(parent.context), R.layout.view_item, parent, false).run {
                RadioViewHolder(this)
            }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.binding.radioButton.isChecked = position == selectedPosition
    }
}
