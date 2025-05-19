package com.uams.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users")
@EqualsAndHashCode(exclude = "users")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "building_name")
    private String buildingName;

    @NotBlank(message = "Street is required")
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false)
    private String state;

    @NotBlank(message = "Country is required")
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "Pincode is required")
    @Column(name = "pincode", nullable = false)
    private String pincode;

    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
    
    // Override toString to prevent infinite recursion
    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", buildingName='" + buildingName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", pincode='" + pincode + '\'' +
                '}';
    }
}