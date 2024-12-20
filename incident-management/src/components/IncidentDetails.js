import React, { useState } from 'react';

const IncidentDetails = ({ incident, onClose, onSave, onDelete }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [title, setTitle] = useState(incident.title);
    const [description, setDescription] = useState(incident.description);
    const [status, setStatus] = useState(incident.status);

    const handleSave = () => {
        onSave({ ...incident, title, description, status });
        setIsEditing(false);
    };

    if (!incident) {
        return null;
    }

    return (
        <div>
            <h2>Incident Details</h2>
            {!isEditing ? (
                <>
                    <p><strong>ID:</strong> {incident.id}</p>
                    <p><strong>Title:</strong> {incident.title}</p>
                    <p><strong>Description:</strong> {incident.description}</p>
                    <p><strong>Status:</strong> {incident.status}</p>
                    <button onClick={() => setIsEditing(true)}>Edit</button>
                </>
            ) : (
                <>
                    <input value={title} onChange={(e) => setTitle(e.target.value)} />
                    <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
                    <select value={status} onChange={(e) => setStatus(e.target.value)}>
                        <option value="OPEN">OPEN</option>
                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                        <option value="CLOSED">CLOSED</option>
                    </select>
                    <button onClick={handleSave}>Save</button>
                    <button onClick={() => setIsEditing(false)}>Cancel</button>
                </>
            )}
            <button onClick={onDelete}>Delete</button>
            <button onClick={onClose}>Close</button>
        </div>
    );
};

export default IncidentDetails;
