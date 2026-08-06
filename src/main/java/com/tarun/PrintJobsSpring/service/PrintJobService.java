package com.tarun.PrintJobsSpring.service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarun.PrintJobsSpring.entity.PrintJob;
import com.tarun.PrintJobsSpring.repository.PrintJobRepository;

@Service
public class PrintJobService {

    private static final Logger logger = LoggerFactory.getLogger(PrintJobService.class);
    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "PRINTING", "COMPLETED", "CANCELLED");
    private static final Set<String> VALID_PAYMENT_STATUSES = Set.of("PENDING", "PAID");

    private final PrintJobRepository repository;

    public PrintJobService(PrintJobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PrintJob saveJob(PrintJob job) {
        validateJob(job);
        job.setAmount(calculateAmount(job.getPages(), job.getCopies(), job.getPrintType()));

        Integer lastQueue = repository.findLastQueueNumber();
        job.setQueueNumber((lastQueue == null || lastQueue < 100 ? 100 : lastQueue) + 1);
        job.setStatus("PENDING");

        String payment = job.getPaymentStatus() == null || job.getPaymentStatus().isBlank()
                ? "PENDING" : job.getPaymentStatus().trim().toUpperCase();
        validatePaymentStatus(payment);
        job.setPaymentStatus(payment);

        PrintJob saved = repository.save(job);
        logger.info("Created print job id={}, queue={}, payment={}", saved.getJobId(), saved.getQueueNumber(), saved.getPaymentStatus());
        return saved;
    }

    public List<PrintJob> getAllJobs() { return repository.findAllByOrderByQueueNumberAsc(); }

    public PrintJob getJobById(int id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Print Job Not Found"));
    }

    public PrintJob getCurrentPrintingJob() {
        return repository.findByStatusAndPaymentStatusOrderByQueueNumberAsc("PRINTING", "PAID")
                .stream().findFirst().orElse(null);
    }

    @Transactional
    public PrintJob updateJob(PrintJob incoming) {
        PrintJob existing = getJobById(incoming.getJobId());
        validateJob(incoming);

        existing.setFileName(incoming.getFileName());
        existing.setPages(incoming.getPages());
        existing.setCopies(incoming.getCopies());
        existing.setPrintType(incoming.getPrintType());
        if (incoming.getFilePath() != null && !incoming.getFilePath().isBlank()) {
            existing.setFilePath(incoming.getFilePath());
        }

        String status = incoming.getStatus() == null ? existing.getStatus() : incoming.getStatus().trim().toUpperCase();
        String payment = incoming.getPaymentStatus() == null ? existing.getPaymentStatus() : incoming.getPaymentStatus().trim().toUpperCase();
        validateStatus(status);
        validatePaymentStatus(payment);
        existing.setStatus(status);
        existing.setPaymentStatus(payment);
        existing.setAmount(calculateAmount(existing.getPages(), existing.getCopies(), existing.getPrintType()));

        PrintJob saved = repository.save(existing);
        logger.info("Updated print job id={}", saved.getJobId());
        return saved;
    }

    @Transactional
    public void deleteJob(int id) {
        PrintJob job = getJobById(id);
        deleteUploadedFile(job.getFilePath());
        repository.delete(job);
        logger.info("Deleted print job id={}", id);
    }

    @Transactional
    public PrintJob updateStatus(int id, String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status is required");
        String normalized = status.trim().toUpperCase();
        validateStatus(normalized);

        PrintJob job = getJobById(id);
        String payment = job.getPaymentStatus() == null ? "PENDING" : job.getPaymentStatus().trim().toUpperCase();
        if (("PRINTING".equals(normalized) || "COMPLETED".equals(normalized)) && !"PAID".equals(payment)) {
            throw new IllegalArgumentException("Payment must be completed before printing");
        }

        String oldStatus = job.getStatus();
        job.setStatus(normalized);
        job.setPaymentStatus(payment);

        if ("COMPLETED".equals(normalized) || "CANCELLED".equals(normalized)) {
            deleteUploadedFile(job.getFilePath());
            job.setFilePath(null);
        }

        PrintJob saved = repository.save(job);
        logger.info("Changed job id={} status {} -> {}", id, oldStatus, normalized);
        return saved;
    }

    @Transactional
    public PrintJob cancelJob(int id) {
        PrintJob job = getJobById(id);
        if (!"PENDING".equalsIgnoreCase(job.getStatus())) {
            throw new IllegalArgumentException("Only pending jobs can be cancelled");
        }
        return updateStatus(id, "CANCELLED");
    }

    public List<PrintJob> getJobsByStatus(String status) {
        String normalized = requireAndUpper(status, "Status is required");
        validateStatus(normalized);
        return repository.findByStatus(normalized);
    }

    public List<PrintJob> getJobsByPaymentStatus(String paymentStatus) {
        String normalized = requireAndUpper(paymentStatus, "Payment status is required");
        validatePaymentStatus(normalized);
        return repository.findByPaymentStatus(normalized);
    }

    public long getTotalJobs() { return repository.count(); }
    public long getCompletedJobs() { return repository.countByStatus("COMPLETED"); }
    public long getPendingJobs() { return repository.countByStatus("PENDING"); }
    public long getPrintingJobs() { return repository.countByStatus("PRINTING"); }
    public Double getTotalRevenue() { return safe(repository.getTotalRevenue()); }
    public Double getTodayRevenue() { return safe(repository.getRevenueSince(LocalDate.now().atStartOfDay())); }

    public Page<PrintJob> getJobsWithPagination(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page number cannot be negative");
        if (size < 1) throw new IllegalArgumentException("Page size must be greater than 0");
        return repository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "queueNumber")));
    }

    public List<PrintJob> getJobsWithSorting(String field) {
        Set<String> allowed = Set.of("jobId", "queueNumber", "fileName", "pages", "copies", "printType", "amount", "status", "paymentStatus", "createdAt");
        if (!allowed.contains(field)) throw new IllegalArgumentException("Invalid sorting field");
        return repository.findAll(Sort.by(Sort.Direction.ASC, field));
    }

    private void deleteUploadedFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            String normalized = filePath.trim();
            File file = normalized.startsWith("/uploads/") ? new File("." + normalized) : new File(normalized);
            if (!file.exists()) {
                logger.warn("Uploaded file not found: {}", file.getAbsolutePath());
                return;
            }
            if (file.delete()) logger.info("Deleted uploaded file: {}", file.getAbsolutePath());
            else logger.error("Unable to delete uploaded file: {}", file.getAbsolutePath());
        } catch (SecurityException ex) {
            logger.error("Permission denied while deleting file {}", filePath, ex);
        }
    }

    private double calculateAmount(int pages, int copies, String printType) {
        if (printType == null || printType.isBlank()) throw new IllegalArgumentException("Print Type is required");
        double rate;
        if ("COLOR".equalsIgnoreCase(printType)) rate = 10.0;
        else if ("BLACK & WHITE".equalsIgnoreCase(printType)) rate = 2.0;
        else throw new IllegalArgumentException("Invalid Print Type");
        return pages * copies * rate;
    }

    private void validateJob(PrintJob job) {
        if (job == null) throw new IllegalArgumentException("Print Job data is required");
        if (job.getFileName() == null || job.getFileName().isBlank()) throw new IllegalArgumentException("File Name is required");
        if (job.getPrintType() == null || job.getPrintType().isBlank()) throw new IllegalArgumentException("Print Type is required");
        if (job.getPages() < 1) throw new IllegalArgumentException("Pages must be greater than 0");
        if (job.getCopies() < 1) throw new IllegalArgumentException("Copies must be greater than 0");
    }

    private void validateStatus(String status) {
        if (!VALID_STATUSES.contains(status)) throw new IllegalArgumentException("Invalid status: " + status);
    }

    private void validatePaymentStatus(String paymentStatus) {
        if (!VALID_PAYMENT_STATUSES.contains(paymentStatus)) throw new IllegalArgumentException("Invalid payment status: " + paymentStatus);
    }

    private String requireAndUpper(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim().toUpperCase();
    }

    private Double safe(Double value) { return value == null ? 0.0 : value; }
}
