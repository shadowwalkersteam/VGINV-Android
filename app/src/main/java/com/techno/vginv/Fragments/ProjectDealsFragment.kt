package com.techno.vginv.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.techno.vginv.Adapters.NewsAdapter
import com.techno.vginv.AddNewProject
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.DashboardActivity
import com.techno.vginv.Model.News
import com.techno.vginv.Model.ProjectCatalog
import com.techno.vginv.R
import com.techno.vginv.Views.EmptyRecyclerView
import com.techno.vginv.utils.CloudDataService
import com.techno.vginv.utils.SharedPrefManager


class ProjectDealsFragment(showProjects: Boolean, isOtherMember: Boolean, id: String) : Fragment() {

    private var projectsAdapter: NewsAdapter? = null
    private var dialog: ProgressDialog? = null
    private var projectHeading: TextView? = null
    private var emptyLayout: TextView? = null
    private var addNewProject: Button? = null
    private var searchView: SearchView? = null
    private var showProjects = false
    private var isOtherMember = false
    private var id: String = ""


    init {
        this.showProjects = showProjects
        this.isOtherMember = isOtherMember
        this.id = id
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_project_deals, container, false)
        projectHeading = view.findViewById(R.id.projectHeading)
        addNewProject = view.findViewById(R.id.addNewProject)
        searchView = view.findViewById(R.id.searchView)
        emptyLayout = view.findViewById(R.id.emptyLayout)
        dialog = ProgressDialog(activity)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
//            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                projectHeading?.text = resources.getString(R.string.project)
//                addNewProject?.text = resources.getString(R.string.AddNewProject)
//            } else {
//                projectHeading?.text = resources.getString(R.string.Deals)
//                addNewProject?.text = resources.getString(R.string.AddNewDeal)
//                setBackgroundTint(addNewProject)
//            }
//        } else {
//            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                projectHeading?.text = resources.getString(R.string.project)
//                addNewProject?.text = resources.getString(R.string.AddNewProject)
//            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
//                projectHeading?.text = resources.getString(R.string.Deals)
//                addNewProject?.text = resources.getString(R.string.AddNewDeal)
//                setBackgroundTint(addNewProject)
//            } else {
//                projectHeading?.text = resources.getString(R.string.project)
//                addNewProject?.text = resources.getString(R.string.AddNewProject)
//            }
//        }

        if (showProjects) {
            addNewProject?.text = resources.getString(R.string.AddNewProject)
            projectHeading?.text = resources.getString(R.string.project)
            (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.project)
        } else {
            addNewProject?.text = resources.getString(R.string.AddNewDeal)
            (context as DashboardActivity).mViewHolder.toolbarTitle.text = resources.getString(R.string.Deals)
            setBackgroundTint(addNewProject)
            projectHeading?.text = resources.getString(R.string.Deals)
        }

        addNewProject?.setOnClickListener(View.OnClickListener {
            addNewProject?.visibility = View.VISIBLE
            if (showProjects) {
                val intent = Intent(activity, AddNewProject()::class.java)
                intent.putExtra("type", "projects")
                startActivity(intent)
            } else {
                val intent = Intent(activity, AddNewProject()::class.java)
                intent.putExtra("type", "deals")
                startActivity(intent)
            }
        })

        val layoutManager = LinearLayoutManager(activity)
        val mRecyclerView = view.findViewById<EmptyRecyclerView>(R.id.projectsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = layoutManager
        dialog!!.setMessage(resources.getString(R.string.pleasewait))
        dialog!!.show()

        try {
            if (isOtherMember) {
                CloudDataService.getFriendProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), id) { jsonObject ->
                    try {
                        if (jsonObject.has("id")) {
                            val id = jsonObject.getInt("id")
                        }
                        if (jsonObject.has("position")) {
                            val positiin = jsonObject.getString("position")
//                            SharedPrefManager.write(SharedPrefManager.USER_DESIGNATION, positiin)
                        }
                        val name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
//                        SharedPrefManager.write(SharedPrefManager.USER_NAME, name)
//                        SharedPrefManager.write(SharedPrefManager.USER_PORIFLE_PIC, jsonObject.getString("image"))

                        val projects = ArrayList<News>()
                        val projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)

//                    if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
//                        if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        } else {
//                            projects.addAll(fetchDeals(projectCatalog))
//                        }
//                    } else {
//                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
//                            projects.addAll(fetchDeals(projectCatalog))
//                        } else {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        }
//                    }

                        if (showProjects) {
                            projects.addAll(fetchProjects(projectCatalog))
                        } else {
                            projects.addAll(fetchDeals(projectCatalog))
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
//                        val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
//                        projects.add(news)
//                    }
                        projects.reverse();

                        activity!!.runOnUiThread {
                            if (projects.size > 0) {
                                projectsAdapter = NewsAdapter(activity, projects, true)
                                mRecyclerView.adapter = projectsAdapter
                            } else {
                                mRecyclerView.visibility = View.GONE
                                emptyLayout?.visibility = View.VISIBLE
                                if (showProjects) {
                                    emptyLayout?.text = resources.getString(R.string.no_projects)
                                } else {
                                    projects.addAll(fetchDeals(projectCatalog))
                                    emptyLayout?.text = resources.getString(R.string.no_deals)
                                }
                            }
                            if (dialog!!.isShowing) {
                                dialog!!.dismiss()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    null
                }
            } else {
                CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject ->
                    try {
                        if (jsonObject.has("id")) {
                            val id = jsonObject.getInt("id")
                        }
                        if (jsonObject.has("position")) {
                            val positiin = jsonObject.getString("position")
//                            SharedPrefManager.write(SharedPrefManager.USER_DESIGNATION, positiin)
                        }
                        val name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
//                        SharedPrefManager.write(SharedPrefManager.USER_NAME, name)
//                        SharedPrefManager.write(SharedPrefManager.USER_PORIFLE_PIC, jsonObject.getString("image"))

                        val projects = ArrayList<News>()
                        val projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)

//                    if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
//                        if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        } else {
//                            projects.addAll(fetchDeals(projectCatalog))
//                        }
//                    } else {
//                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
//                            projects.addAll(fetchDeals(projectCatalog))
//                        } else {
//                            projects.addAll(fetchProjects(projectCatalog))
//                        }
//                    }

                        if (showProjects) {
                            projects.addAll(fetchProjects(projectCatalog))
                        } else {
                            projects.addAll(fetchDeals(projectCatalog))
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
//                        val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
//                        projects.add(news)
//                    }
                        projects.reverse();

                        activity!!.runOnUiThread {
                            if (projects.size > 0) {
                                projectsAdapter = NewsAdapter(activity, projects, true)
                                mRecyclerView.adapter = projectsAdapter
                            } else {
                                mRecyclerView.visibility = View.GONE
                                emptyLayout?.visibility = View.VISIBLE
                                if (showProjects) {
                                    emptyLayout?.text = resources.getString(R.string.no_projects)
                                } else {
                                    projects.addAll(fetchDeals(projectCatalog))
                                    emptyLayout?.text = resources.getString(R.string.no_deals)
                                }
                            }
                            if (dialog!!.isShowing) {
                                dialog!!.dismiss()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
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
    }

    fun fetchProjects(projectCatalog: ProjectCatalog) : ArrayList<News> {
        val projects = ArrayList<News>()
        if (isOtherMember) {
            for (projectDetails in projectCatalog.projectCatalog) {
                if (projectDetails.auth == id) {
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
            }
        } else {
            for (projectDetails in projectCatalog.projectCatalog) {
                if (projectDetails.auth == SharedPrefManager.read(SharedPrefManager.USER_ID, "")) {
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
            }
        }

        return projects
    }

    fun fetchDeals(projectCatalog: ProjectCatalog) : ArrayList<News> {
        val projects = ArrayList<News>()
        if (isOtherMember) {
            for (projectDetails in projectCatalog.dealsCatalog) {
                if (projectDetails.auth == id) {
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
            }
        } else {
            for (projectDetails in projectCatalog.dealsCatalog) {
                if (projectDetails.auth == SharedPrefManager.read(SharedPrefManager.USER_ID, "")) {
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
            }
        }
        return projects
    }

    fun setBackgroundTint(button: Button?) {
        var buttonDrawable: Drawable = button?.background!!
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red_dark))
        button?.background = buttonDrawable
    }

}
