package org.demo.service;

import org.demo.model.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentServiceTest {

    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentService = new IncidentService();
    }

    @Test
    void testCreateIncident() {
        Incident incident = new Incident(null, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident createdIncident = incidentService.createIncident(incident);

        assertNotNull(createdIncident.getId());
        assertEquals("Title 1", createdIncident.getTitle());
        assertEquals("Description 1", createdIncident.getDescription());
        assertEquals(Incident.Status.OPEN, createdIncident.getStatus());
    }

    @Test
    void testCreateIncidentWithDefaultStatus() {
        Incident incident = new Incident(null, "Title 1", "Description 1", null);
        Incident createdIncident = incidentService.createIncident(incident);

        assertNotNull(createdIncident.getId());
        assertEquals("Title 1", createdIncident.getTitle());
        assertEquals("Description 1", createdIncident.getDescription());
        assertEquals(Incident.Status.OPEN, createdIncident.getStatus());
    }

    @Test
    void testGetAllIncidents() {
        Incident incident1 = new Incident(null, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident incident2 = new Incident(null, "Title 2", "Description 2", Incident.Status.OPEN);

        incidentService.createIncident(incident1);
        incidentService.createIncident(incident2);

        List<Incident> allIncidents = incidentService.getAllIncidents();

        assertEquals(2, allIncidents.size());
    }

    @Test
    void testGetIncidentById() {
        Incident incident = new Incident(null, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident createdIncident = incidentService.createIncident(incident);

        Optional<Incident> foundIncident = incidentService.getIncidentById(createdIncident.getId());

        assertTrue(foundIncident.isPresent());
        assertEquals("Title 1", foundIncident.get().getTitle());
    }

    @Test
    void testGetIncidentByIdNotFound() {
        Optional<Incident> foundIncident = incidentService.getIncidentById(999L);

        assertFalse(foundIncident.isPresent());
    }

    @Test
    void testGetIncidentByInvalidId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incidentService.getIncidentById(null);
        });
        assertEquals("ID must be a positive non-null value", exception.getMessage());
    }

    @Test
    void testUpdateIncident() {
        Incident incident = new Incident(null, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident createdIncident = incidentService.createIncident(incident);

        Incident updatedIncident = new Incident(null, "Updated Title", "Updated Description", Incident.Status.CLOSED);
        Optional<Incident> result = incidentService.updateIncident(createdIncident.getId(), updatedIncident);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Description", result.get().getDescription());
        assertEquals(Incident.Status.CLOSED, result.get().getStatus());
    }

    @Test
    void testUpdateIncidentNotFound() {
        Incident updatedIncident = new Incident(null, "Updated Title", "Updated Description", Incident.Status.CLOSED);
        Optional<Incident> result = incidentService.updateIncident(999L, updatedIncident);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteIncident() {
        Incident incident = new Incident(null, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident createdIncident = incidentService.createIncident(incident);

        boolean isDeleted = incidentService.deleteIncident(createdIncident.getId());

        assertTrue(isDeleted);
        assertTrue(incidentService.getAllIncidents().isEmpty());
    }

    @Test
    void testDeleteIncidentNotFound() {
        boolean isDeleted = incidentService.deleteIncident(999L);

        assertFalse(isDeleted);
    }

    @Test
    void testDeleteIncidentWithInvalidId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            incidentService.deleteIncident(null);
        });
        assertEquals("ID must be a positive non-null value", exception.getMessage());
    }
}
