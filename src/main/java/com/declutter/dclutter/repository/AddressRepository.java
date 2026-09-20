package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a JOIN a.users u WHERE u.username = ?1")
    List<Address> findAddressesByUsername(String username);
}