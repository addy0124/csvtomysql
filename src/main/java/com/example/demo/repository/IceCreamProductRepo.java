package com.example.demo.repository;

import com.example.demo.entity.Ice_cream_product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface IceCreamProductRepo extends JpaRepository<Ice_cream_product, Long> {
}
