package com.mclebtec.thread.future.dao.repository;


import com.mclebtec.thread.future.dao.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
