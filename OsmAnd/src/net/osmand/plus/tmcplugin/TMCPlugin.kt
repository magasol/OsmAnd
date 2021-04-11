package net.osmand.plus.tmcplugin

import android.os.Bundle
import net.osmand.aidlapi.OsmAndCustomizationConstants
import net.osmand.plus.ContextMenuAdapter
import net.osmand.plus.ContextMenuAdapter.ItemClickListener
import net.osmand.plus.ContextMenuItem.ItemBuilder
import net.osmand.plus.OsmandApplication
import net.osmand.plus.OsmandPlugin
import net.osmand.plus.R
import net.osmand.plus.activities.MapActivity

class TMCPlugin(app: OsmandApplication?) : OsmandPlugin(app) {

    companion object {
        const val TMC_POS_ITEM_ORDER = 10800;
        const val ID = "tmc.plugin"
        const val COMPONENT = "net.osmand.TMCPlugin"
    }

    override fun getDescription(): CharSequence {
        return app.getString(R.string.plugin_tmc_descr)
    }

    override fun getName(): String {
        return app.getString(R.string.plugin_tmc_name)
    }

    override fun getLogoResourceId(): Int {
        return R.drawable.ic_plugin_tmc
    }

    override fun isMarketPlugin(): Boolean {
        return true
    }

    override fun getComponentId1(): String {
        return COMPONENT
    }

    override fun getId(): String {
        return ID
    }

    override fun registerMapContextMenuActions(mapActivity: MapActivity?,
                                               latitude: Double, longitude: Double,
                                               adapter: ContextMenuAdapter, selectedObj: Any?, configureMenu: Boolean) {
        val addListener = ItemClickListener { adapter, resId, pos, isChecked, viewCoordinates ->
            if (resId == R.string.context_menu_item_tmc) {
                showChooseLengthDialog(mapActivity, latitude, longitude)
            }
            true
        }
        adapter.addItem(ItemBuilder()
                .setTitleId(R.string.context_menu_item_tmc, mapActivity)
                .setId(OsmAndCustomizationConstants.MAP_CONTEXT_MENU_TMC)
                .setIcon(R.drawable.ic_plugin_tmc)
                .setOrder(TMC_POS_ITEM_ORDER)
                .setListener(addListener)
                .createItem())
    }

    private fun showChooseLengthDialog(mapActivity: MapActivity?, latitude: Double, longitude: Double) {
        val args = Bundle()
        args.putDouble(RouteLengthBottomSheetDialogFragment.LAT_KEY, latitude)
        args.putDouble(RouteLengthBottomSheetDialogFragment.LON_KEY, longitude)
        val fragmentManager = mapActivity!!.supportFragmentManager
        val fragment = RouteLengthBottomSheetDialogFragment()
        fragment.arguments = args
        fragment.show(fragmentManager, RouteLengthBottomSheetDialogFragment.TAG)
    }
}