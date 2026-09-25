package com.gianteyes.gaarigar.utils;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.exceptions.LocationNotAvailableException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;


@Service
public class GeoUtils {
    public Geometry wktToGeometry(String wellKnownText)
            throws ParseException {
        Geometry geometry = new WKTReader().read(wellKnownText);
        geometry.setSRID(4326);
        return geometry;
    }

    public String geometryToWkt(Geometry geometry) {
        return geometry.toText();
    }

    public Double[] getLatLongFromWkt(String wellKnownText) {
        String[] latLong = wellKnownText.split(" ");
        return new Double[]{Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])};
    }

    public Double[] geometryToLatLong(Geometry geometry) {
        if (geometry == null) {
            throw new LocationNotAvailableException();
        }
        // get coords from geometry
        Coordinate latLong = geometry.getCoordinate();
        return new Double[]{latLong.x, latLong.y};
    }

    public Point convertLocationToPoint(Location location) throws ParseException {
        String str = String.format("POINT (%f %f)", location.getLongitude(), location.getLatitude());
        return (Point) wktToGeometry(str);
    }

    public Location convertPointToLocation(Point point) {
        return Location.builder().latitude(point.getY()).longitude(point.getX()).build();
    }
}
