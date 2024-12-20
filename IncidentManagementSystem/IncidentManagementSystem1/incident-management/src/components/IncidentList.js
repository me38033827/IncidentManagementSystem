import React from 'react';

const IncidentList = ({ incidents, onSelectIncident }) => {
    return (
        <div>
            <h2>Incident List</h2>
            <ul>
                {incidents.map((incident) => (
                    <li key={incident.id}>
                        {incident.title} - {incident.status}
                        <button onClick={() => onSelectIncident(incident)}>Details</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default IncidentList;
