package com.mclebtec.thread.future.scheduler;

import com.mclebtec.thread.future.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CarScheduler {

    @Autowired
    private CarService carService;

    @Scheduled(cron = "0/2 * * ? * *")
    public void pollData() throws InterruptedException {
        log.info("Entering the method to print data");
        carService.processMessages();
    }
}
