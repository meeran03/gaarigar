package com.gianteyes.gaarigar.mechanic.dao;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.utils.BaseSpecification;
import org.hibernate.spatial.predicate.JTSSpatialPredicates;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MechanicSpecification extends BaseSpecification<MechanicModel> {

    public static Specification<MechanicModel> getWithinDistance(Double longitude, Double latitude, double radius) {
        return new Specification<MechanicModel>() {
            @Override
            public Predicate toPredicate(Root<MechanicModel> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

                GeometryFactory factory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
                Point point = factory.createPoint(new Coordinate(longitude, latitude));
                return builder.lessThanOrEqualTo(
                    builder.function("ST_DistanceSphere", Double.class, root.get("location"), builder.literal(point)),
                    radius * 1000);
            }
        };
    }
}
