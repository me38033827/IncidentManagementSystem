import React, { useState } from 'react';
import { createIncident, updateIncident } from '../services/api';

const IncidentForm = ({ selectedIncident, onSuccess }) => {
    const [title, setTitle] = useState(selectedIncident?.title || '');
    const [description, setDescription] = useState(selectedIncident?.description || '');
    const [status, setStatus] = useState(selectedIncident?.status || 'OPEN');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const incident = { title, description, status };

        try {
            if (selectedIncident?.id) {
                await updateIncident(selectedIncident.id, incident);
            } else {
                await createIncident(incident);
            }
            onSuccess(); // Notify parent component
        } catch (error) {
            console.error('Error creating/updating incident:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>{selectedIncident ? 'Edit Incident' : 'Create Incident'}</h2>
            <input
                type="text"
                placeholder="Title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
            />
            <textarea
                placeholder="Description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
            />
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="OPEN">OPEN</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="CLOSED">CLOSED</option>
            </select>
            <button type="submit">{selectedIncident ? 'Update' : 'Create'}</button>
        </form>
    );
};

export default IncidentForm;
