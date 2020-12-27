package com.mclebtec.thread.future.controller;

import com.mclebtec.thread.future.dao.entity.Car;
import com.mclebtec.thread.future.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/api/car")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity uploadFile(@RequestParam(value = "files") MultipartFile[] files) {
        try {
            for (final MultipartFile file : files) {
                carService.saveCars(file.getInputStream());
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping(value = "list/single")
    public @ResponseBody
    CompletableFuture<ResponseEntity> getSingleCar() throws InterruptedException {
        return carService.getAllCars().<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleGetCarFailure);
    }

    @GetMapping(value = "list/no-thread")
    public @ResponseBody
    ResponseEntity getAllCarDetailsWithoutThreading() throws InterruptedException {
        List<Car> cars1 = carService.getAllCars1();
        List<Car> cars2 = carService.getAllCars1();
        List<Car> cars3 = carService.getAllCars1();
        log.info("Car sizes 1={}, 2={}, 3={}", cars1.size(), cars1.size(), cars1.size());
        Set<Car> cars = new HashSet<>();
        cars.addAll(cars1);
        cars.addAll(cars2);
        cars.addAll(cars3);
        log.info("Final Car Size = {}", cars.size());
        return new ResponseEntity(cars, HttpStatus.OK);
    }

    @GetMapping(value = "list/multiple")
    public @ResponseBody
    ResponseEntity getAllCars() {
        try {
            CompletableFuture<List<Car>> cars1 = carService.getAllCars();
            CompletableFuture<List<Car>> cars2 = carService.getAllCars();
            CompletableFuture<List<Car>> cars3 = carService.getAllCars();
            CompletableFuture.allOf(cars1, cars2, cars3).join();
            log.info("Car sizes 1={}, 2={}, 3={}", cars1.get().size(), cars1.get().size(), cars1.get().size());
            Set<Car> cars = new HashSet<>();
            cars.addAll(cars1.get());
            cars.addAll(cars2.get());
            cars.addAll(cars3.get());
            log.info("Final Car Size = {}", cars.size());
            return new ResponseEntity(cars, HttpStatus.OK);
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static Function<Throwable, ResponseEntity<? extends List<Car>>> handleGetCarFailure = throwable -> {
        log.error("Failed to read records= {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };


}
