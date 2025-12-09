package com.salmontaker.sniffy.push.repository;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Integer> {
    Optional<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findAllByUserIdIn(Collection<Integer> userIds);

    void deleteByUserId(Integer userId);
}
