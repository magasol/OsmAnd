package net.osmand.plus.itinerary;

import net.osmand.GPXUtilities.WptPt;
import net.osmand.data.FavouritePoint;
import net.osmand.data.LatLon;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.itinerary.ItineraryHelper.ItineraryType;
import net.osmand.plus.mapmarkers.MapMarker;

import static net.osmand.plus.FavouritesDbHelper.DELIMETER;
import static net.osmand.plus.itinerary.ItineraryHelper.POINT_ID;
import static net.osmand.plus.itinerary.ItineraryHelper.POINT_TYPE;

public class ItineraryItem {

	private String name;
	public LatLon point;
	public Object object;

	public ItineraryItem(Object object, LatLon point, String name) {
		this.object = object;
		this.point = point;
		this.name = name;
	}

	public static ItineraryItem fromWpt(WptPt p, OsmandApplication app) {
		return new ItineraryItem(p, new LatLon(p.lat, p.lon), p.name);
	}

	public WptPt toWpt(OsmandApplication app) {
		WptPt wptPt = null;
		if (object instanceof WptPt) {
			WptPt pt = (WptPt) object;
			wptPt = new WptPt(pt);
			wptPt.getExtensionsToWrite().put(POINT_ID, "" + pt.lat + DELIMETER + pt.lon + DELIMETER + ItineraryType.GPX.name());
			wptPt.getExtensionsToWrite().put(POINT_TYPE, ItineraryType.GPX.name());
		} else if (object instanceof MapMarker) {
			MapMarker marker = (MapMarker) object;
			wptPt = new WptPt();
			wptPt.lat = marker.point.getLatitude();
			wptPt.lon = marker.point.getLongitude();
			wptPt.getExtensionsToWrite().put(POINT_ID, "" + marker.point.getLatitude() + DELIMETER + marker.point.getLongitude() + DELIMETER + ItineraryType.MARKER.name());
			wptPt.getExtensionsToWrite().put(POINT_TYPE, ItineraryType.MARKER.name());
		} else if (object instanceof FavouritePoint) {
			FavouritePoint favouritePoint = (FavouritePoint) object;
			wptPt = new WptPt();
			wptPt.lat = favouritePoint.getLatitude();
			wptPt.lon = favouritePoint.getLongitude();
			wptPt.getExtensionsToWrite().put(POINT_ID, "" + favouritePoint.getLatitude() + DELIMETER + favouritePoint.getLongitude() + DELIMETER + ItineraryType.FAVOURITE.name());
			wptPt.getExtensionsToWrite().put(POINT_TYPE, ItineraryType.FAVOURITE.name());
		}
		return wptPt;
	}
}
