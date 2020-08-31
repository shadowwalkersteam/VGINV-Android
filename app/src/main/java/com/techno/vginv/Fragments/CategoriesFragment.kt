package com.techno.vginv.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.techno.vginv.Adapters.NewsAdapter
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.Model.News
import com.techno.vginv.Model.ProjectCatalog
import com.techno.vginv.R
import com.techno.vginv.SharedInstance.allDepartments
import com.techno.vginv.Views.EmptyRecyclerView
import com.techno.vginv.utils.CloudDataService
import com.techno.vginv.utils.SharedPrefManager


class CategoriesFragment : Fragment() {

    private var categories: Spinner? = null
    private var status: Spinner? = null
    private var projectsAdapter: NewsAdapter? = null
    private var departmentID = -1
    private var projectStatus = 1
    private var searchView: SearchView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        categories = view.findViewById(R.id.category)
        status = view.findViewById(R.id.status)
        searchView = view.findViewById(R.id.searchView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.categories))

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categories?.setAdapter(adapter)

        val adapter2: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.ProjectStatus))

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        status?.setAdapter(adapter2)

        val layoutManager = LinearLayoutManager(activity)

        val mRecyclerView = view.findViewById<EmptyRecyclerView>(R.id.projectsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = layoutManager

        categories?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                try {
                    if (position == 0) {
                        departmentID = -1
                    } else {
                        for (departmentsData in allDepartments.allDepartments) {
                            if (departmentsData.depName.equals(categories?.selectedItem.toString(), ignoreCase = true)) {
                                departmentID = departmentsData.id.toInt()
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        status?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                try {
                    if (position == 0) {
                        projectStatus = 1
                    } else if (position == 1) {
                        projectStatus = 1
                    } else if (position == 2) {
                        projectStatus = 0
                    }
                    loadProjects(mRecyclerView, departmentID, projectStatus)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query:String):Boolean {
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                projectsAdapter?.filter?.filter(newText)
                return false
            }
        })

        loadProjects(mRecyclerView, departmentID, 1)
    }

    private fun loadProjects(mRecyclerView: EmptyRecyclerView, id: Int, status: Int) {
        try {
            CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, "")) { jsonObject ->
                try {
                    val projects = ArrayList<News>()
                    val projectCatalog = Gson().fromJson(jsonObject.toString(), ProjectCatalog::class.java)
//                    for (projectDetails in projectCatalog?.projectCatalog!!) {
//                        if (id == -1 && projectDetails.status == status.toString()) {
//                            val title = projectDetails.title
//                            val description = projectDetails.description
//                            var thumbnail = ""
//                            try {
//                                thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                            val budget = resources.getString(R.string.budget) + ": SAR " + projectDetails.budget
//                            val investment = resources.getString(R.string.Total_Investment) + ": SAR " + projectDetails.investment
//                            val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
//                            projects.add(news)
//                        } else if (id.toString() == projectDetails.dep_id && projectDetails.status == status.toString()) {
//                            val title = projectDetails.title
//                            val description = projectDetails.description
//                            var thumbnail = ""
//                            try {
//                                thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                            val budget = resources.getString(R.string.budget) + ": SAR " + projectDetails.budget
//                            val investment = resources.getString(R.string.Total_Investment) + ": SAR " + projectDetails.investment
//                            val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
//                            projects.add(news)
//                        }
//                    }

                    if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                        if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equals("vg", ignoreCase = true)) {
                            projects.addAll(fetchProjects(projectCatalog, id, status))
                        } else {
                            projects.addAll(fetchDeals(projectCatalog, id, status))
                        }
                    } else {
                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("vg", ignoreCase = true)) {
                            projects.addAll(fetchProjects(projectCatalog, id, status))
                        } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equals("HMG", ignoreCase = true)){
                            projects.addAll(fetchDeals(projectCatalog, id, status))
                        } else {
                            projects.addAll(fetchProjects(projectCatalog, id, status))
                        }
                    }
                    projects.reverse()
                    activity!!.runOnUiThread {
                        projectsAdapter = NewsAdapter(activity, projects, true)
                        mRecyclerView.adapter = projectsAdapter
                        mRecyclerView.adapter?.notifyDataSetChanged()
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

    fun fetchProjects(projectCatalog: ProjectCatalog, id: Int, status: Int) : ArrayList<News> {
        val projects = ArrayList<News>()
        for (projectDetails in projectCatalog?.projectCatalog!!) {
            if (id == -1 && projectDetails.status == status.toString()) {
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
                val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
                projects.add(news)
            } else if (id.toString() == projectDetails.dep_id && projectDetails.status == status.toString()) {
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
                val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
                projects.add(news)
            }
        }
        return projects
    }

    fun fetchDeals(projectCatalog: ProjectCatalog, id: Int, status: Int) : ArrayList<News> {
        val projects = ArrayList<News>()
        for (projectDetails in projectCatalog?.dealsCatalog!!) {
            if (id == -1 && projectDetails.status == status.toString()) {
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
                val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
                projects.add(news)
            } else if (id.toString() == projectDetails.dep_id && projectDetails.status == status.toString()) {
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
                val news = News(title, resources.getString(R.string.business), investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, description, projectDetails.projectAssets)
                projects.add(news)
            }
        }
        return projects
    }

}