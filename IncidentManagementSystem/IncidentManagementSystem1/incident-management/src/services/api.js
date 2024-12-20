import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/incidents'; // Update with your backend base URL

export const getAllIncidents = async () => {
    const response = await axios.get(API_BASE_URL);
    return response.data;
};

export const getIncidentById = async (id) => {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
};

export const createIncident = async (incident) => {
    const response = await axios.post(API_BASE_URL, incident);
    return response.data;
};

export const updateIncident = async (id, updatedIncident) => {
    const response = await axios.put(`${API_BASE_URL}/${id}`, updatedIncident);
    return response.data;
};

export const deleteIncident = async (id) => {
    await axios.delete(`${API_BASE_URL}/${id}`);
};
