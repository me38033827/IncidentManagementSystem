import React, { useState, useEffect } from 'react';
import IncidentList from './components/IncidentList';
import IncidentForm from './components/IncidentForm';
import IncidentDetails from './components/IncidentDetails';
import { getAllIncidents, deleteIncident, updateIncident } from './services/api';

const App = () => {
  const [selectedIncident, setSelectedIncident] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const [incidents, setIncidents] = useState([]);

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      const data = await getAllIncidents();
      setIncidents(data);
    } catch (error) {
      console.error('Error fetching incidents:', error);
    }
  };

  const handleIncidentSave = async (incident) => {
    try {
      if (incident.id) {
        await updateIncident(incident.id, incident);
      }
      fetchIncidents();
      setShowDetails(false);
    } catch (error) {
      console.error('Error saving incident:', error);
    }
  };

  const handleIncidentDelete = async (id) => {
    try {
      await deleteIncident(id);
      fetchIncidents();
      setShowDetails(false);
    } catch (error) {
      console.error('Error deleting incident:', error);
    }
  };

  const handleIncidentSelection = (incident) => {
    setSelectedIncident(incident);
    setShowDetails(true);
  };

  return (
      <div>
        <h1>Incident Management</h1>
        {showDetails ? (
            <IncidentDetails
                incident={selectedIncident}
                onClose={() => setShowDetails(false)}
                onSave={handleIncidentSave}
                onDelete={handleIncidentDelete}
            />
        ) : (
            <>
              <IncidentForm onSuccess={fetchIncidents} />
              <IncidentList incidents={incidents} onSelectIncident={handleIncidentSelection} />
            </>
        )}
      </div>
  );
};

export default App;
