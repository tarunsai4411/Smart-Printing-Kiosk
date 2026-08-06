package com.tarun.PrintJobsSpring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tarun.PrintJobsSpring.entity.PrintJob;

public interface PrintJobRepository extends JpaRepository<PrintJob, Integer> {

    @Query("SELECT MAX(p.queueNumber) FROM PrintJob p")
    Integer findLastQueueNumber();

    List<PrintJob> findByStatus(String status);
    List<PrintJob> findByPaymentStatus(String paymentStatus);
    List<PrintJob> findByStatusOrderByQueueNumberAsc(String status);
    List<PrintJob> findByStatusAndPaymentStatusOrderByQueueNumberAsc(String status, String paymentStatus);
    List<PrintJob> findAllByOrderByQueueNumberAsc();

    long countByStatus(String status);
    long countByPaymentStatus(String paymentStatus);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PrintJob p WHERE UPPER(p.paymentStatus) = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PrintJob p WHERE UPPER(p.paymentStatus) = 'PAID' AND p.createdAt >= :start")
    Double getRevenueSince(LocalDateTime start);
}
