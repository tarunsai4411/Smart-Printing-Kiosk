package com.tarun.PrintJobsSpring.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tarun.PrintJobsSpring.dto.UploadResponse;
import com.tarun.PrintJobsSpring.entity.PrintJob;
import com.tarun.PrintJobsSpring.service.PrintJobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/printjobs")
public class PrintJobController {

    private final PrintJobService service;
    private final Path uploadDirectory = Paths.get("uploads").toAbsolutePath().normalize();

    public PrintJobController(PrintJobService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PrintJob> addJob(@Valid @RequestBody PrintJob job) {
        return ResponseEntity.ok(service.saveJob(job));
    }

    @GetMapping
    public ResponseEntity<List<PrintJob>> getAllJobs() {
        return ResponseEntity.ok(service.getAllJobs());
    }

    @GetMapping("/track/{id:[0-9]+}")
    public ResponseEntity<PrintJob> trackJob(@PathVariable int id) {
        return ResponseEntity.ok(service.getJobById(id));
    }

    @GetMapping("/current")
    public ResponseEntity<PrintJob> getCurrent() {
        PrintJob job = service.getCurrentPrintingJob();
        return job == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(job);
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<PrintJob> updateJob(@PathVariable int id, @Valid @RequestBody PrintJob job) {
        job.setJobId(id);
        return ResponseEntity.ok(service.updateJob(job));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<PrintJob> updateStatus(@PathVariable int id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.updateStatus(id, request.get("status")));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<PrintJob> cancelJob(@PathVariable int id) {
        return ResponseEntity.ok(service.cancelJob(id));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<String> deleteJob(@PathVariable int id) {
        service.deleteJob(id);
        return ResponseEntity.ok("Print Job Deleted Successfully!");
    }

    @GetMapping("/history")
    public ResponseEntity<List<PrintJob>> getHistory() {
        return ResponseEntity.ok(service.getJobsByStatus("COMPLETED"));
    }

    @GetMapping("/count") public ResponseEntity<Long> count() { return ResponseEntity.ok(service.getTotalJobs()); }
    @GetMapping("/pending/count") public ResponseEntity<Long> pendingCount() { return ResponseEntity.ok(service.getPendingJobs()); }
    @GetMapping("/printing/count") public ResponseEntity<Long> printingCount() { return ResponseEntity.ok(service.getPrintingJobs()); }
    @GetMapping("/completed/count") public ResponseEntity<Long> completedCount() { return ResponseEntity.ok(service.getCompletedJobs()); }
    @GetMapping("/revenue") public ResponseEntity<Double> revenue() { return ResponseEntity.ok(service.getTotalRevenue()); }
    @GetMapping("/revenue/today") public ResponseEntity<Double> todayRevenue() { return ResponseEntity.ok(service.getTodayRevenue()); }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PrintJob>> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.getJobsByStatus(status));
    }

    @GetMapping("/payment/{paymentStatus}")
    public ResponseEntity<List<PrintJob>> byPayment(@PathVariable String paymentStatus) {
        return ResponseEntity.ok(service.getJobsByPaymentStatus(paymentStatus));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PrintJob>> page(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getJobsWithPagination(page, size));
    }

    @GetMapping("/sort/{field}")
    public ResponseEntity<List<PrintJob>> sort(@PathVariable String field) {
        return ResponseEntity.ok(service.getJobsWithSorting(field));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body("Please select a PDF file.");
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) return ResponseEntity.badRequest().body("Invalid file name.");

        String clean = Paths.get(original).getFileName().toString();
        if (!clean.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only PDF files are supported.");
        }

        try {
            Files.createDirectories(uploadDirectory);
            String stored = UUID.randomUUID() + "_" + clean;
            Path target = uploadDirectory.resolve(stored).normalize();
            if (!target.startsWith(uploadDirectory)) return ResponseEntity.badRequest().body("Invalid file path.");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            int pages = countPdfPages(target);
            return ResponseEntity.ok(new UploadResponse(clean, "uploads/" + stored, pages));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to upload the file.");
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewFile(@PathVariable int id) {
        PrintJob job = service.getJobById(id);
        if (job.getFilePath() == null || job.getFilePath().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The uploaded file is no longer available.");
        }
        try {
            String stored = Paths.get(job.getFilePath()).getFileName().toString();
            Path filePath = uploadDirectory.resolve(stored).normalize();
            if (!filePath.startsWith(uploadDirectory) || !Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The PDF file does not exist.");
            }
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cleanHeader(job.getFileName()) + "\"")
                    .header("X-Content-Type-Options", "nosniff")
                    .contentLength(Files.size(filePath))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to open the PDF.");
        }
    }

    private int countPdfPages(Path filePath) {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            return document.getNumberOfPages();
        } catch (IOException ex) {
            return 1;
        }
    }

    private String cleanHeader(String name) {
        return name == null ? "document.pdf" : name.replace("\"", "").replace("\r", "").replace("\n", "");
    }
}
