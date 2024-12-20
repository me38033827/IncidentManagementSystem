package org.demo.stresstest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IncidentControllerStressTest {

    private static final String BASE_URL = "http://localhost:8080/api/incidents";
    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void stressTestCreateIncident() {
        int threadCount = 1000;
        int requestCount = 100000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String incidentJson = "{" +
                        "\"title\":\"Stress Test Incident\"," +
                        "\"description\":\"Created during stress testing\"," +
                        "\"status\":\"OPEN\"}";
                HttpEntity<String> request = new HttpEntity<>(incidentJson, headers);

                try {
                    restTemplate.postForEntity(BASE_URL, request, String.class);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        logPerformanceMetrics(requestCount, successCount.get(), failureCount.get(), startTime, endTime);
    }

    @Test
    void stressTestGetAllIncidents() {
        int threadCount = 50;
        int requestCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    restTemplate.getForEntity(BASE_URL, String.class);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        logPerformanceMetrics(requestCount, successCount.get(), failureCount.get(), startTime, endTime);
    }

    @Test
    void stressTestUpdateIncident() {
        // Pre-create an incident to update
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String initialIncidentJson = "{" +
                "\"title\":\"Initial Incident\"," +
                "\"description\":\"For update testing\"," +
                "\"status\":\"OPEN\"}";
        HttpEntity<String> initialRequest = new HttpEntity<>(initialIncidentJson, headers);
        String incidentId = restTemplate.postForObject(BASE_URL, initialRequest, String.class);

        int threadCount = 50;
        int requestCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                String updateIncidentJson = "{" +
                        "\"title\":\"Updated Incident\"," +
                        "\"description\":\"Updated during stress testing\"," +
                        "\"status\":\"CLOSED\"}";
                HttpEntity<String> updateRequest = new HttpEntity<>(updateIncidentJson, headers);

                try {
                    restTemplate.put(BASE_URL + "/" + incidentId, updateRequest);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        logPerformanceMetrics(requestCount, successCount.get(), failureCount.get(), startTime, endTime);
    }

    @Test
    void stressTestDeleteIncident() {
        // Pre-create an incident to delete
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String initialIncidentJson = "{" +
                "\"title\":\"Incident to Delete\"," +
                "\"description\":\"Created for delete testing\"," +
                "\"status\":\"OPEN\"}";
        HttpEntity<String> initialRequest = new HttpEntity<>(initialIncidentJson, headers);
        String incidentId = restTemplate.postForObject(BASE_URL, initialRequest, String.class);

        int threadCount = 50;
        int requestCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    restTemplate.delete(BASE_URL + "/" + incidentId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        logPerformanceMetrics(requestCount, successCount.get(), failureCount.get(), startTime, endTime);
    }

    private void logPerformanceMetrics(int requestCount, int successCount, int failureCount, long startTime, long endTime) {
        long duration = endTime - startTime;
        double avgLatency = (double) duration / requestCount;
        double throughput = (double) requestCount / (duration / 1000.0);

        System.out.println("Stress Test Results:");
        System.out.println("Total Requests: " + requestCount);
        System.out.println("Successful Requests: " + successCount);
        System.out.println("Failed Requests: " + failureCount);
        System.out.println("Total Time (ms): " + duration);
        System.out.println("Average Latency (ms): " + avgLatency);
        System.out.println("Throughput (requests/sec): " + throughput);
    }
}
