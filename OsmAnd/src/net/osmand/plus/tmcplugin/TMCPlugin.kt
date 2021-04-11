package net.osmand.plus.tmcplugin

import net.osmand.plus.OsmandApplication
import net.osmand.plus.OsmandPlugin
import net.osmand.plus.R

class TMCPlugin(app: OsmandApplication?) : OsmandPlugin(app) {

    companion object {
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
}