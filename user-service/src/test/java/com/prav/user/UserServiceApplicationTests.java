package com.prav.user;

import com.prav.user.model.Restaurant;
import com.prav.user.model.User;
import com.prav.user.repository.RestaurantRepository;
import com.prav.user.repository.UserRepository;
import com.prav.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceApplicationTests {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RestaurantRepository restaurantRepo;

    @InjectMocks
    private UserService userService;

    private Restaurant sampleRestaurant;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleRestaurant = new Restaurant();
        sampleRestaurant.setId(1L);
        sampleRestaurant.setName("Pizza Palace");

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("john");
        sampleUser.setPassword("encoded-pass");
        sampleUser.setRole("USER");
        sampleUser.setKnownRestaurantIds(List.of(1L));
    }

    @Test
    void testCreateUser_Success() {
        when(restaurantRepo.findAll()).thenReturn(List.of(sampleRestaurant));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User newUser = new User();
        newUser.setUsername("john");
        newUser.setPassword("password123");

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER", result.getRole());
        assertEquals(List.of(1L), result.getKnownRestaurantIds());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void testCreateUser_NoRestaurants_StillWorks() {
        when(restaurantRepo.findAll()).thenReturn(Collections.emptyList());
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        User newUser = new User();
        newUser.setUsername("john");
        newUser.setPassword("password123");

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("USER", result.getRole());
        assertTrue(result.getKnownRestaurantIds().isEmpty());
    }

    @Test
    void testNotifyNewRestaurant_AddsToAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setRole("USER");
        user1.setKnownRestaurantIds(new ArrayList<>(List.of(1L)));

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setRole("USER");
        user2.setKnownRestaurantIds(new ArrayList<>(List.of(1L)));

        when(userRepo.findByRole("USER")).thenReturn(List.of(user1, user2));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.notifyNewRestaurant(2L);

        verify(userRepo, times(2)).save(any(User.class));
    }

    @Test
    void testNotifyNewRestaurant_UserAlreadyKnowsRestaurant_Skips() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setRole("USER");
        user.setKnownRestaurantIds(new ArrayList<>(List.of(1L, 2L)));

        when(userRepo.findByRole("USER")).thenReturn(List.of(user));

        userService.notifyNewRestaurant(2L);

        verify(userRepo, never()).save(any());
    }

    @Test
    void testNotifyNewRestaurant_NullKnownRestaurantIds_HandlesGracefully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setRole("USER");
        user.setKnownRestaurantIds(null);

        when(userRepo.findByRole("USER")).thenReturn(List.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.notifyNewRestaurant(3L);

        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testNotifyNewRestaurant_NoUsers_NothingHappens() {
        when(userRepo.findByRole("USER")).thenReturn(Collections.emptyList());

        userService.notifyNewRestaurant(5L);

        verify(userRepo, never()).save(any());
    }

    // ========== promoteUser ==========

    @Test
    void testPromoteUser_ToStaff_Success() {
        when(userRepo.findById(2L)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.promoteUser(2L, 1L, "STAFF");

        assertEquals("STAFF", result.getRole());
        assertEquals(1L, result.getAssignedRestaurantId());
        assertEquals(List.of(1L), result.getKnownRestaurantIds());
        verify(userRepo).save(any());
    }

    @Test
    void testPromoteUser_ToAdmin_Success() {
        when(userRepo.findById(2L)).thenReturn(Optional.of(sampleUser));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.promoteUser(2L, 1L, "admin");

        assertEquals("ADMIN", result.getRole());
        assertEquals(1L, result.getAssignedRestaurantId());
    }

    @Test
    void testPromoteUser_UserNotFound_ThrowsException() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.promoteUser(99L, 1L, "STAFF"));
    }

    @Test
    void testPromoteUser_AlreadyStaff_ThrowsException() {
        sampleUser.setRole("STAFF");

        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(RuntimeException.class, () -> userService.promoteUser(1L, 1L, "ADMIN"));
        verify(userRepo, never()).save(any());
    }

    @Test
    void testPromoteUser_UserDoesNotKnowRestaurant_ThrowsException() {
        sampleUser.setKnownRestaurantIds(List.of(99L));

        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(RuntimeException.class, () -> userService.promoteUser(1L, 1L, "STAFF"));
        verify(userRepo, never()).save(any());
    }

    @Test
    void testPromoteUser_NullKnownRestaurants_ThrowsException() {
        sampleUser.setKnownRestaurantIds(null);

        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(RuntimeException.class, () -> userService.promoteUser(1L, 1L, "STAFF"));
    }

    // ========== demoteToUser ==========

    @Test
    void testDemoteToUser_Success() {
        User staffUser = new User();
        staffUser.setId(1L);
        staffUser.setUsername("staff1");
        staffUser.setRole("STAFF");
        staffUser.setAssignedRestaurantId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(staffUser));
        when(restaurantRepo.findAll()).thenReturn(List.of(sampleRestaurant));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.demoteToUser(1L);

        assertEquals("USER", result.getRole());
        assertNull(result.getAssignedRestaurantId());
        assertEquals(List.of(1L), result.getKnownRestaurantIds());
        verify(userRepo).save(any());
    }

    @Test
    void testDemoteToUser_NotFound_ThrowsException() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.demoteToUser(99L));
    }
}