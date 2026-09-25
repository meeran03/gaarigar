package com.gianteyes.gaarigar.rating;

import com.gianteyes.gaarigar.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<RatingModel,Long> {
    long countByRatedTo(UserModel user);
}
