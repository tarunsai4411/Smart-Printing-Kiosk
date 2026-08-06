package com.tarun.PrintJobsSpring.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tarun.PrintJobsSpring.entity.PrintJob;
import com.tarun.PrintJobsSpring.repository.PrintJobRepository;
import com.tarun.PrintJobsSpring.service.PrintJobService;

@Component
public class PrintJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PrintJobScheduler.class);
    private final PrintJobRepository repository;
    private final PrintJobService service;

    public PrintJobScheduler(PrintJobRepository repository, PrintJobService service) {
        this.repository = repository;
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.delay-ms:30000}")
    public void processQueue() {
        List<PrintJob> printing = repository
                .findByStatusAndPaymentStatusOrderByQueueNumberAsc("PRINTING", "PAID");

        if (!printing.isEmpty()) {
            PrintJob current = printing.get(0);
            service.updateStatus(current.getJobId(), "COMPLETED");
            logger.info("Automatically completed queue {}", current.getQueueNumber());
            return;
        }

        List<PrintJob> pending = repository
                .findByStatusAndPaymentStatusOrderByQueueNumberAsc("PENDING", "PAID");

        if (!pending.isEmpty()) {
            PrintJob next = pending.get(0);
            service.updateStatus(next.getJobId(), "PRINTING");
            logger.info("Automatically started queue {}", next.getQueueNumber());
        }
    }
}
