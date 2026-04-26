package com.prav.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.prav.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findFirstByUsername(String username);
    List<User> findByAssignedRestaurantId(Long assignedRestaurantId);
    List<User> findByAssignedRestaurantIdOrAssignedRestaurantIdIsNull(Long assignedRestaurantId);
    List<User> findByRole(String role);
    List<User> findByRoleAndKnownRestaurantIdsContaining(String role, Long restaurantId);
    Optional<User> findByEmail(String email);
}