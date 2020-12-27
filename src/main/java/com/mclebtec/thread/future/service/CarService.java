package com.mclebtec.thread.future.service;


import com.mclebtec.thread.future.dao.entity.Car;
import com.mclebtec.thread.future.dao.repository.CarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Async
    @Transactional
    public CompletableFuture<List<Car>> saveCars(final InputStream inputStream) throws Exception {
        final long start = System.currentTimeMillis();
        List<Car> cars = parseCSVFile(inputStream);
        log.info("Current running thread = {}", Thread.currentThread().getName());
        Thread.sleep(10000);
        log.info("Saving a list of cars of size {} records", cars.size());
        cars = carRepository.saveAll(cars);
        log.info("Saved a list of cars of size {} records", cars.size());
        log.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return CompletableFuture.completedFuture(cars);
    }

    @Transactional
    public List<Car> saveCars1(final InputStream inputStream) throws Exception {
        final long start = System.currentTimeMillis();
        List<Car> cars = parseCSVFile(inputStream);
        log.info("Current running thread = {}", Thread.currentThread().getName());
        Thread.sleep(10000);
        log.info("Saving a list of cars of size {} records", cars.size());
        cars = carRepository.saveAll(cars);
        log.info("Saved a list of cars of size {} records", cars.size());
        log.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return cars;
    }

    @Async
    public CompletableFuture<List<Car>> getAllCars() throws InterruptedException {
        log.info("Request to get a list of cars");
        final long start = System.currentTimeMillis();
        log.info("Current thread detail = {}", Thread.currentThread().getName());
        Thread.sleep(10000);
        final List<Car> cars = carRepository.findAll();
        log.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return CompletableFuture.completedFuture(cars);
    }


    public List<Car> getAllCars1() throws InterruptedException {
        final long start = System.currentTimeMillis();
        log.info("Current thread detail = {}", Thread.currentThread().getName());
        Thread.sleep(10000);
        final List<Car> cars = carRepository.findAll();
        log.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return cars;
    }

    public void processMessages() throws InterruptedException {
        log.info("Processing message");
        final long start = System.currentTimeMillis();
        Thread.sleep(5000);
        final List<Car> cars = carRepository.findAll();
        log.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        log.info("Updated car details = {}, current thread name = {}", cars, Thread.currentThread().getName());
    }

    private List<Car> parseCSVFile(final InputStream inputStream) throws Exception {
        final List<Car> cars = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(";");
                    final Car car = new Car();
                    car.setManufacturer(data[0]);
                    car.setModel(data[1]);
                    car.setType(data[2]);
                    cars.add(car);
                }
                return cars;
            }
        } catch (final IOException e) {
            log.error("Failed to parse CSV file = {}", e.getMessage());
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }


}
