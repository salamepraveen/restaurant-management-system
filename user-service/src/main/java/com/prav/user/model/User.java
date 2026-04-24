package com.prav.user.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    private Long assignedRestaurantId;

    private String phoneNumber;
    
    private String address;

    @ElementCollection
    @CollectionTable(name = "user_known_restaurants",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "restaurant_id")
    private List<Long> knownRestaurantIds = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getAssignedRestaurantId() {
		return assignedRestaurantId;
	}

	public void setAssignedRestaurantId(Long assignedRestaurantId) {
		this.assignedRestaurantId = assignedRestaurantId;
	}

	public List<Long> getKnownRestaurantIds() {
		return knownRestaurantIds;
	}

	public void setKnownRestaurantIds(List<Long> knownRestaurantIds) {
		this.knownRestaurantIds = knownRestaurantIds;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}