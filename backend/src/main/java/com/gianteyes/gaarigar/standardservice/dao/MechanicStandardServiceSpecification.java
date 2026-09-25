package com.gianteyes.gaarigar.standardservice.dao;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
import com.gianteyes.gaarigar.standardservice.StandardServiceService;
import com.gianteyes.gaarigar.utils.BaseSpecification;
import org.hibernate.spatial.predicate.JTSSpatialPredicates;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class MechanicStandardServiceSpecification extends BaseSpecification<MechanicStandardServiceModel> {

    public static Specification<MechanicStandardServiceModel> hasCategoryId(Long categoryId) {
        return (root, query, builder) -> {
            Join<MechanicStandardServiceModel, StandardServiceService> join = root.join("standardService");
            return builder.equal(join.get("category"), categoryId);
        };
    }

    public static Specification<MechanicStandardServiceModel> hasMechanicId(Long mechanicId) {
        return (root, query, builder) -> {
            Join<MechanicStandardServiceModel, MechanicModel> join = root.join("mechanic");
            return builder.equal(join.get("id"), mechanicId);
        };
    }

    public static Specification<MechanicStandardServiceModel> hasStandardServiceName(String standardServiceName) {
        return (root, query, builder) -> {
            Join<MechanicStandardServiceModel, StandardServiceService> join = root.join("standardService");
            join.alias("s");
            return builder.like(
                    builder.lower(join.get("name")),
                    "%" + standardServiceName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<MechanicStandardServiceModel> filterWithinRadius(Double longitude, Double latitude, Double radius) {
        return new Specification<MechanicStandardServiceModel>() {

            @Override
            public Predicate toPredicate(Root<MechanicStandardServiceModel> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                // first join with mechanic
                Join<MechanicStandardServiceModel, MechanicModel> mechanicJoin = root.join("mechanic");
                GeometryFactory factory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
                Point point = factory.createPoint(new Coordinate(longitude, latitude));
                return builder.lessThanOrEqualTo(
                    builder.function("ST_DistanceSphere", Double.class, mechanicJoin.get("location"), builder.literal(point)),
                    radius * 1000);
            }
        };
    }
}
