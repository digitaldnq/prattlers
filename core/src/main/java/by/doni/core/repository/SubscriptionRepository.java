package by.doni.core.repository;

import by.doni.core.entity.Subscription;
import by.doni.core.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
                SELECT s.follower
                FROM Subscription s
                WHERE s.followee.id = :followeeId 
            """)
    List<User> findFollowersByFolloweeId(@Param("followeeId") Long followeeId);

    boolean existsByFollowerId(Long followerId, Long followeeId);

    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    int deleteAllByFolloweeIdOrFollowerId(Long followeeId, Long followerId);

}
