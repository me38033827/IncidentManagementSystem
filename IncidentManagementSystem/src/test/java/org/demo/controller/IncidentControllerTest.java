package org.demo.controller;

import org.demo.model.Incident;
import org.demo.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidentService incidentService;

    @Test
    void testCreateIncident() throws Exception {
        Incident incident = new Incident(1L, "Title 1", "Description 1", Incident.Status.OPEN);
        Mockito.when(incidentService.createIncident(any(Incident.class))).thenReturn(incident);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Title 1\", \"description\":\"Description 1\", \"status\":\"OPEN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void testGetAllIncidents() throws Exception {
        Incident incident1 = new Incident(1L, "Title 1", "Description 1", Incident.Status.OPEN);
        Incident incident2 = new Incident(2L, "Title 2", "Description 2", Incident.Status.CLOSED);

        Mockito.when(incidentService.getAllIncidents()).thenReturn(Arrays.asList(incident1, incident2));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    void testGetIncidentById() throws Exception {
        Incident incident = new Incident(1L, "Title 1", "Description 1", Incident.Status.OPEN);
        Mockito.when(incidentService.getIncidentById(1L)).thenReturn(Optional.of(incident));

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title 1"));
    }

    @Test
    void testUpdateIncident() throws Exception {
        Incident incident = new Incident(1L, "Updated Title", "Updated Description", Incident.Status.CLOSED);
        Mockito.when(incidentService.updateIncident(eq(1L), any(Incident.class))).thenReturn(Optional.of(incident));

        mockMvc.perform(put("/api/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\", \"description\":\"Updated Description\", \"status\":\"CLOSED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void testDeleteIncident() throws Exception {
        Mockito.when(incidentService.deleteIncident(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isNoContent());
    }
}
