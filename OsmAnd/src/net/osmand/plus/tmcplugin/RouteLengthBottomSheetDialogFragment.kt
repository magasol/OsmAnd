package net.osmand.plus.tmcplugin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.osmand.ResultMatcher
import net.osmand.data.LatLon
import net.osmand.plus.OsmandApplication
import net.osmand.plus.R
import net.osmand.plus.base.MenuBottomSheetDialogFragment
import net.osmand.plus.base.bottomsheetmenu.SimpleBottomSheetItem
import net.osmand.plus.base.bottomsheetmenu.simpleitems.TitleItem
import net.osmand.search.SearchUICore
import net.osmand.search.core.ObjectType
import net.osmand.search.core.SearchCoreAPI
import net.osmand.search.core.SearchResult
import java.util.*

class RouteLengthBottomSheetDialogFragment : MenuBottomSheetDialogFragment() {
    companion object {
        val TAG = "RouteLengthBottomSheetDialogFragment"
        val LAT_KEY = "latitude"
        val LON_KEY = "longitude"
    }

    private var app: OsmandApplication? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = myApplication

    }
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Setup search core
        val searchHelper = app?.searchUICore
        searchUICore = searchHelper?.core

        return super.onCreateView(inflater, parent, savedInstanceState)
    }

    override fun createMenuItems(savedInstanceState: Bundle?) {
        val longitude = arguments?.getDouble(LON_KEY, 0.0)
        val latitude = arguments?.getDouble(LAT_KEY, 0.0)
        val location = LatLon(latitude!!, longitude!!)


        val locale = app!!.settings.MAP_PREFERRED_LOCALE.get()
        val transliterate = app!!.settings.MAP_TRANSLITERATE_NAMES.get()

        val searchLatLon = LatLon(location.latitude, location.longitude)

        var settings = searchUICore!!.searchSettings.setOriginalLocation(
                LatLon(searchLatLon.latitude, searchLatLon.longitude))
        settings = settings.setLang(locale, transliterate)
        searchUICore!!.updateSettings(settings)


        val title = resources.getString(R.string.plugin_tmc_choose_route)
        items.add(TitleItem(title))

        val shortRoute = SimpleBottomSheetItem.Builder()
                .setTitle(getString(R.string.tmc_short_route_length))
                .setLayoutId(R.layout.bottom_sheet_item_simple_right_icon)
                .setOnClickListener {
                    magic()
                    println("logger shortRoute lon $longitude lat $latitude")
                }
                .create()
        items.add(shortRoute)

        val mediumRoute = SimpleBottomSheetItem.Builder()
                .setTitle(getString(R.string.tmc_medium_route_length))
                .setLayoutId(R.layout.bottom_sheet_item_simple_right_icon)
                .setOnClickListener { println("logger mediumRoute lon $longitude lat $latitude") }
                .create()
        items.add(mediumRoute)

        val longRoute = SimpleBottomSheetItem.Builder()
                .setTitle(getString(R.string.tmc_long_route_length))
                .setLayoutId(R.layout.bottom_sheet_item_simple_right_icon)
                .setOnClickListener { println("logger longRoute lon $longitude lat $latitude") }
                .create()
        items.add(longRoute)
    }
    private var searchUICore: SearchUICore? = null


    private fun magic() {
        // filtry tutaj magic
        val helper = app?.poiFilters
        val filter = helper?.getFilterById("user_custom_id");
        val poiCategory = app!!.poiTypes.getCategories(false)[19]


        filter?.selectSubTypesToAccept(poiCategory, linkedSetOf("attraction"))
        val sr = SearchResult(searchUICore?.phrase)
        sr.localeName = filter?.name
        sr.`object` = filter
        sr.priority = 0.0
        sr.objectType = ObjectType.POI_TYPE
        searchUICore!!.selectSearchResult(sr)

        val settings = searchUICore!!.searchSettings
                .setEmptyQueryAllowed(true)
                .setSortByName(false)
                .setRadiusLevel(1)

        searchUICore!!.updateSettings(settings)

        searchUICore?.search("Tourist attraction ", false, object : ResultMatcher<SearchResult?> {
            var regionResultCollection: SearchUICore.SearchResultCollection? = null
            var regionResultApi: SearchCoreAPI? = null
            var results: MutableList<SearchResult> = ArrayList()
            override fun publish(`object`: SearchResult?): Boolean {
                when (`object`?.objectType) {
                    ObjectType.SEARCH_STARTED -> {
                        println("logger start")
                    }
                    ObjectType.SEARCH_FINISHED -> {
                        println("logger finished")
                    }
                    ObjectType.SEARCH_API_FINISHED -> {
                        val searchApi = `object`.`object` as SearchCoreAPI
                        val apiResults: List<SearchResult>
                        val regionApi = regionResultApi
                        val regionCollection = regionResultCollection
                        val hasRegionCollection = searchApi === regionApi && regionCollection != null
                        apiResults = if (hasRegionCollection) {
                            regionCollection!!.currentSearchResults
                        } else {
                            results
                        }
                        regionResultApi = null
                        regionResultCollection = null
                        results = ArrayList()
                        print("logger lista: ")
                        apiResults.forEach { print(" ${it.localeName}; ") }
                        println()
                    }
                    else -> results.add(`object`!!)
                }
                return true
            }


            override fun isCancelled(): Boolean {
                return false
            }
        })
    }
}