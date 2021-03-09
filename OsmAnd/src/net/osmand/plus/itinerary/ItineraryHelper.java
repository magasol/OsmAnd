package net.osmand.plus.itinerary;

import androidx.annotation.NonNull;

import net.osmand.GPXUtilities;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.GPXUtilities.WptPt;
import net.osmand.PlatformUtil;
import net.osmand.data.FavouritePoint;
import net.osmand.data.LatLon;
import net.osmand.plus.FavoriteGroup;
import net.osmand.plus.GpxSelectionHelper.SelectedGpxFile;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.Version;
import net.osmand.plus.mapmarkers.MapMarker;
import net.osmand.plus.mapmarkers.MapMarkersDbHelper;
import net.osmand.plus.mapmarkers.MapMarkersGroup;
import net.osmand.util.Algorithms;

import org.apache.commons.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItineraryHelper {

	protected static final String POINT_ID = "point_id";
	protected static final String POINT_TYPE = "point_type";
	private static final String FILE_TO_SAVE = "itinerary.gpx";
	private static final Log log = PlatformUtil.getLog(ItineraryHelper.class);

	private OsmandApplication app;

	private List<ItineraryItem> items = new ArrayList<>();

	public enum ItineraryType {
		MARKER,
		FAVOURITE,
		GPX
	}

	public ItineraryHelper(@NonNull OsmandApplication app) {
		this.app = app;
	}

	public void addItineraryItem(ItineraryItem item) {
		items.add(item);
	}

	public void loadItinerary() {
		items.clear();

		File internalFile = getInternalFile();
		if (!internalFile.exists()) {
			loadAndCheckDatabasePoints();
			saveCurrentPointsIntoFile();
		}
		Map<String, ItineraryItem> points = new LinkedHashMap<String, ItineraryItem>();
		loadGPXFile(internalFile, points);
	}

	public void saveCurrentPointsIntoFile() {
		try {
			saveFile(items, getInternalFile());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public Exception saveFile(List<ItineraryItem> itineraryItems, File f) {
		GPXFile gpx = asGpxFile(itineraryItems);
		return GPXUtilities.writeGpxFile(f, gpx);
	}

	public GPXFile asGpxFile(List<ItineraryItem> itineraryItems) {
		GPXFile gpx = new GPXFile(Version.getFullVersion(app));
		for (ItineraryItem item : itineraryItems) {
			app.getSelectedGpxHelper().addPoint(item.toWpt(app), gpx);
		}
		return gpx;
	}

	private boolean loadGPXFile(File file, Map<String, ItineraryItem> points) {
		if (!file.exists()) {
			return false;
		}
		GPXFile res = GPXUtilities.loadGPXFile(file);
		if (res.error != null) {
			return false;
		}
		for (WptPt p : res.getPoints()) {
			ItineraryItem fp = ItineraryItem.fromWpt(p, app);
			if (fp != null) {
				items.add(fp);
			}
		}
		return true;
	}

	private void loadAndCheckDatabasePoints() {
		MapMarkersDbHelper markersDbHelper = app.getMapMarkersDbHelper();
		List<MapMarker> mapMarkers = markersDbHelper.getActiveMarkers();
		Map<String, MapMarkersGroup> groupsMap = markersDbHelper.getAllGroupsMap();

		if (!Algorithms.isEmpty(mapMarkers)) {
			for (MapMarker marker : mapMarkers) {
				items.add(new ItineraryItem(marker, marker.point, marker.mapObjectName));
			}
		}
		if (!Algorithms.isEmpty(groupsMap)) {
			for (MapMarker marker : mapMarkers) {
				items.add(new ItineraryItem(marker, marker.point, marker.mapObjectName));
			}
		}
	}

	private File getInternalFile() {
		return app.getFileStreamPath(FILE_TO_SAVE);
	}

	public void updateItineraryItems() {
		items.clear();

		List<MapMarker> mapMarkers = app.getMapMarkersHelper().getMapMarkers();
		List<FavoriteGroup> favoriteGroups = app.getFavorites().getFavoriteGroups();
		List<SelectedGpxFile> selectedGpxFiles = app.getSelectedGpxHelper().getSelectedGPXFiles();

		if (!Algorithms.isEmpty(mapMarkers)) {
			for (MapMarker marker : mapMarkers) {
				items.add(new ItineraryItem(marker, marker.point, marker.mapObjectName));
			}
		}
		if (!Algorithms.isEmpty(favoriteGroups)) {
			for (FavoriteGroup group : favoriteGroups) {
				for (FavouritePoint point : group.getPoints()) {
					LatLon latLon = new LatLon(point.getLatitude(), point.getLongitude());
					items.add(new ItineraryItem(point, latLon, point.getName()));
				}
			}
		}
		if (!Algorithms.isEmpty(selectedGpxFiles)) {
			for (SelectedGpxFile selectedGpxFile : selectedGpxFiles) {
				for (WptPt point : selectedGpxFile.getGpxFile().getPoints()) {
					LatLon latLon = new LatLon(point.getLatitude(), point.getLongitude());
					items.add(new ItineraryItem(point, latLon, point.name));
				}
			}
		}
	}

	public void addItineraryItem(Object object, LatLon latLon, String mapObjectName) {
		ItineraryItem item = null;
		if (object instanceof WptPt) {
			WptPt wptPt = (WptPt) object;
		} else if (object instanceof FavouritePoint) {

		} else {

		}
		item = new ItineraryItem(object, latLon, mapObjectName);
		items.add(item);
	}

	public boolean containsItemForObject(Object selectedObj) {
		for (ItineraryItem item : items) {
			if (Algorithms.objectEquals(item.object, selectedObj)) {
				return true;
			}
		}
		return false;
	}
}