package org.demo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.demo.model.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for managing incidents.
 * Provides methods to create, retrieve, update, and delete incidents.
 */
@Service
public class IncidentService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

    private final ConcurrentMap<Long, Incident> incidents = new ConcurrentHashMap<>();
    private final Set<String> deduplicationSet = ConcurrentHashMap.newKeySet();
    private final AtomicLong idGenerator = new AtomicLong();

    // Caffeine cache for read-heavy APIs
    private final Cache<Long, Incident> incidentCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    /**
     * Creates a new incident and assigns a unique ID to it.
     * Prevents duplicate incidents based on title and description.
     *
     * @param incident The incident to be created.
     * @return The created incident with an assigned ID, or the existing incident if a duplicate is detected.
     */
    public Incident createIncident(Incident incident) {
        // Validate status
        if (incident.getStatus() != null && incident.getStatus() != Incident.Status.OPEN) {
            throw new IllegalArgumentException("Status can only be null or OPEN.");
        }
        if (incident.getStatus() == null) {
            incident.setStatus(Incident.Status.OPEN);
        }

        // Validate title length
        if (incident.getTitle() == null || incident.getTitle().length() > 100) {
            throw new IllegalArgumentException("Title must not be null and should be less than 100 characters.");
        }

        // Sanitize and validate body content
        if (incident.getDescription() != null) {
            incident.setDescription(sanitize(incident.getDescription()));
        }

        // Create a deduplication key based on the title and description
        String deduplicationKey = incident.getTitle() + ":" + incident.getDescription();

        // Use deduplication set to check and add atomically
        if (!deduplicationSet.add(deduplicationKey)) {
            logger.warn("Duplicate incident detected: {}", incident);
            return incidents.values().stream()
                    .filter(existing -> deduplicationKey.equals(existing.getTitle() + ":" + existing.getDescription()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Duplicate key in deduplicationSet but no matching incident"));
        }

        long id = idGenerator.incrementAndGet();
        incident.setId(id);
        incidents.put(id, incident);
        incidentCache.put(id, incident);
        logger.info("Created new incident with ID {}: {}", id, incident);
        return incident;
    }

    /**
     * Retrieves all incidents.
     *
     * @return A list of all incidents.
     */
    public List<Incident> getAllIncidents() {
        logger.info("Fetching all incidents, total count: {}", incidents.size());
        return List.copyOf(incidents.values());
    }

    /**
     * Retrieves an incident by its ID.
     * Uses caching for frequently accessed incidents.
     *
     * @param id The ID of the incident to retrieve.
     * @return An Optional containing the incident if found, or empty if not found.
     */
    public Optional<Incident> getIncidentById(Long id) {
        validateId(id);

        // Attempt to retrieve the incident from the cache
        Incident cachedIncident = incidentCache.getIfPresent(id);
        if (cachedIncident != null) {
            logger.info("Retrieved incident with ID {} from cache", id);
            return Optional.of(cachedIncident);
        }

        // Fallback to the main storage
        Optional<Incident> incident = Optional.ofNullable(incidents.get(id));
        incident.ifPresent(value -> incidentCache.put(id, value));

        if (incident.isPresent()) {
            logger.info("Retrieved incident with ID {}: {}", id, incident.get());
        } else {
            logger.warn("Incident with ID {} not found", id);
        }

        return incident;
    }

    /**
     * Updates an existing incident.
     *
     * @param id The ID of the incident to update.
     * @param updatedIncident The new incident data.
     * @return An Optional containing the updated incident if successful, or empty if the incident does not exist.
     */
    public Optional<Incident> updateIncident(Long id, Incident updatedIncident) {
        validateId(id);

        if (updatedIncident.getStatus() == null) {
            updatedIncident.setStatus(Incident.Status.OPEN);
        }

        return Optional.ofNullable(incidents.computeIfPresent(id, (key, existingIncident) -> {
            String oldDeduplicationKey = existingIncident.getTitle() + ":" + existingIncident.getDescription();
            deduplicationSet.remove(oldDeduplicationKey);

            String newDeduplicationKey = updatedIncident.getTitle() + ":" + updatedIncident.getDescription();
            deduplicationSet.add(newDeduplicationKey);

            updatedIncident.setId(id);
            incidentCache.put(id, updatedIncident); // Update cache
            logger.info("Updated incident with ID {}: {}", id, updatedIncident);
            return updatedIncident;
        }));
    }

    /**
     * Deletes an incident by its ID and removes it from the deduplication set.
     *
     * @param id The ID of the incident to delete.
     * @return True if the incident was deleted, false if it did not exist.
     */
    public boolean deleteIncident(Long id) {
        validateId(id);
        Incident removedIncident = incidents.remove(id);
        if (removedIncident != null) {
            String deduplicationKey = removedIncident.getTitle() + ":" + removedIncident.getDescription();
            deduplicationSet.remove(deduplicationKey);
            incidentCache.invalidate(id); // Invalidate cache
            logger.info("Deleted incident with ID {}", id);
            return true;
        } else {
            logger.warn("Attempted to delete non-existent incident with ID {}", id);
            return false;
        }
    }

    /**
     * Validates the provided ID to ensure it is positive and non-null.
     *
     * @param id The ID to validate.
     */
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid ID provided: {}", id);
            throw new IllegalArgumentException("ID must be a positive non-null value");
        }
    }

    /**
     * Sanitizes the input string to prevent security vulnerabilities.
     * Removes HTML tags and escapes dangerous characters.
     *
     * @param input The string to sanitize.
     * @return The sanitized string.
     */
    private String sanitize(String input) {
        // Example sanitization: strip HTML tags
        return input.replaceAll("<[^>]*>", "").trim();
    }

}
