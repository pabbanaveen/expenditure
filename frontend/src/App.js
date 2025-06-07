import { useEffect, useState } from "react";
import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import axios from "axios";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Home = () => {
  const [chitties, setChitties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchChitties = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API}/chitties`);
      console.log('API Response:', response.data);
      if (response.data.success) {
        setChitties(response.data.data);
      } else {
        setError('Failed to fetch chitties');
      }
    } catch (e) {
      console.error('Error fetching chitties:', e);
      setError('Error connecting to backend');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchChitties();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-IN');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
          <p className="mt-4 text-lg text-gray-600">Loading Chitty Manager...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-500 text-lg">{error}</p>
          <button 
            onClick={fetchChitties}
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Chitty Manager</h1>
              <p className="text-gray-600">Manage your chit fund operations</p>
            </div>
            <div className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium">
              ✅ Connected to Spring Boot
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {chitties.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">No chitties found</p>
          </div>
        ) : (
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-gray-900">Active Chitties</h2>
              <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm">
                {chitties.length} Active
              </span>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {chitties.map((chitty) => (
                <div key={chitty.id} className="bg-white rounded-lg shadow-md p-6 border border-gray-200 hover:shadow-lg transition-shadow">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-xl font-semibold text-gray-900">{chitty.name}</h3>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      chitty.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    }`}>
                      {chitty.active ? 'Active' : 'Inactive'}
                    </span>
                  </div>

                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Total Amount:</span>
                      <span className="font-semibold text-green-600">{formatCurrency(chitty.amount)}</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-gray-600">Members:</span>
                      <span className="font-medium">{chitty.totalMembers}</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-gray-600">Duration:</span>
                      <span className="font-medium">{chitty.totalMonths} months</span>
                    </div>

                    <div className="border-t pt-3">
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Regular Payment:</span>
                        <span className="font-medium text-blue-600">{formatCurrency(chitty.regularPayment)}</span>
                      </div>
                      <div className="flex justify-between text-sm mt-1">
                        <span className="text-gray-600">Lifted Payment:</span>
                        <span className="font-medium text-orange-600">{formatCurrency(chitty.liftedPayment)}</span>
                      </div>
                    </div>

                    <div className="border-t pt-3">
                      <div className="text-sm text-gray-600">
                        <div>Started: {formatDate(chitty.startDate)}</div>
                        <div>Updated: {formatDate(chitty.updatedAt)}</div>
                      </div>
                    </div>
                  </div>

                  <div className="mt-4 flex space-x-2">
                    <button className="flex-1 bg-blue-500 text-white px-3 py-2 rounded text-sm hover:bg-blue-600 transition-colors">
                      View Details
                    </button>
                    <button className="flex-1 bg-green-500 text-white px-3 py-2 rounded text-sm hover:bg-green-600 transition-colors">
                      Manage
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t mt-12">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <p className="text-gray-600">Chitty Manager - Expenditure Tracking System</p>
            <div className="flex items-center space-x-4 text-sm text-gray-500">
              <span>Spring Boot Backend ✅</span>
              <span>React Frontend ✅</span>
              <span>MongoDB Database ✅</span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />}>
            <Route index element={<Home />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
