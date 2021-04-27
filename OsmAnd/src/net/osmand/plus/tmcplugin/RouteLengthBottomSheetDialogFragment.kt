package net.osmand.plus.tmcplugin

import android.os.Bundle
import net.osmand.plus.R
import net.osmand.plus.base.MenuBottomSheetDialogFragment
import net.osmand.plus.base.bottomsheetmenu.SimpleBottomSheetItem
import net.osmand.plus.base.bottomsheetmenu.simpleitems.TitleItem

class RouteLengthBottomSheetDialogFragment: MenuBottomSheetDialogFragment() {
    companion object {
        val TAG = "RouteLengthBottomSheetDialogFragment"
        val LAT_KEY = "latitude"
        val LON_KEY = "longitude"
    }

    override fun createMenuItems(savedInstanceState: Bundle?) {
        val longitude = arguments?.getDouble(LON_KEY, 0.0)
        val latitude = arguments?.getDouble(LAT_KEY, 0.0)
        val title = resources.getString(R.string.plugin_tmc_choose_route)
        items.add(TitleItem(title))

        val shortRoute = SimpleBottomSheetItem.Builder()
                .setTitle(getString(R.string.tmc_short_route_length))
                .setLayoutId(R.layout.bottom_sheet_item_simple_right_icon)
                .setOnClickListener { println("logger shortRoute lon $longitude lat $latitude") }
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
}