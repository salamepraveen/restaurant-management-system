package com.prav.user.repository;

import com.prav.user.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RestaurantRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RestaurantRepository repo;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setName("Pizza Palace");
        restaurant.setOwnerId(1L);
    }

    @Test
    void save_andFindById() {
        Restaurant saved = repo.save(restaurant);
        assertTrue(repo.findById(saved.getId()).isPresent());
        assertEquals("Pizza Palace", repo.findById(saved.getId()).get().getName());
    }

    @Test
    void findAll_returnsAll() {
        em.persistAndFlush(restaurant);
        Restaurant r2 = new Restaurant();
        r2.setName("Burger King");
        r2.setOwnerId(2L);
        em.persistAndFlush(r2);
        assertEquals(2, repo.findAll().size());
    }

    @Test
    void findAll_empty() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void deleteById() {
        Restaurant saved = em.persistAndFlush(restaurant);
        em.clear();
        repo.deleteById(saved.getId());
        assertTrue(repo.findById(saved.getId()).isEmpty());
    }

    @Test
    void save_withoutOwner() {
        restaurant.setOwnerId(null);
        Restaurant saved = repo.save(restaurant);
        assertNotNull(saved.getId());
        assertNull(saved.getOwnerId());
    }

    @Test
    void save_updateName() {
        Restaurant saved = em.persistAndFlush(restaurant);
        em.clear();
        saved.setName("New Name");
        repo.save(saved);
        assertEquals("New Name", repo.findById(saved.getId()).get().getName());
    }
}