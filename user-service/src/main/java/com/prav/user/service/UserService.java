package com.prav.user.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.prav.user.model.Restaurant;
import com.prav.user.model.User;
import com.prav.user.repository.RestaurantRepository;
import com.prav.user.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;

    public UserService(UserRepository userRepo, RestaurantRepository restaurantRepo) {
        this.userRepo = userRepo;
        this.restaurantRepo = restaurantRepo;
    }

    
    public User createUser(User user) {
    	if (userRepo.findFirstByUsername(user.getUsername()).isPresent()) {
    	    throw new RuntimeException("User already exists with username: " + user.getUsername());
    	}

        user.setRole("USER");
        List<Long> allRestaurantIds = getAllRestaurantIds();
        user.setKnownRestaurantIds(allRestaurantIds);
        return userRepo.save(user);
    }

  
    public void notifyNewRestaurant(Long restaurantId) {
        List<User> allUsers = userRepo.findByRole("USER");
        for (User user : allUsers) {
            if (user.getKnownRestaurantIds() == null) {
                user.setKnownRestaurantIds(new ArrayList<>());
            }
            if (!user.getKnownRestaurantIds().contains(restaurantId)) {
                user.getKnownRestaurantIds().add(restaurantId);
                userRepo.save(user);
            }
        }
    }

    // PROMOTE USER 
    public User promoteUser(Long userId, Long restaurantId, String newRole) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        if (!"USER".equals(user.getRole())) {
            throw new RuntimeException("Only USER role can be promoted");
        }

       
        if (user.getKnownRestaurantIds() == null || 
            !user.getKnownRestaurantIds().contains(restaurantId)) {
            throw new RuntimeException("User does not know this restaurant");
        }

        
        user.setRole(newRole.toUpperCase());
        user.setAssignedRestaurantId(restaurantId);

        List<Long> single = new ArrayList<>();
        single.add(restaurantId);
        user.setKnownRestaurantIds(single);

        return userRepo.save(user);
    }

   
    public User demoteToUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole("USER");
        user.setAssignedRestaurantId(null);
        user.setKnownRestaurantIds(getAllRestaurantIds());

        return userRepo.save(user);
    }

    public User updateProfile(Long userId, String email, String phoneNumber, String address) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        if (address != null) {
            user.setAddress(address);
        }
        
        return userRepo.save(user);
    }

    
    public List<Restaurant> getAllPublicRestaurants() {
        return restaurantRepo.findAll();
    }

    private List<Long> getAllRestaurantIds() {
        List<Long> ids = new ArrayList<>();
        restaurantRepo.findAll().forEach(r -> ids.add(r.getId()));
        return ids;
    }
}
