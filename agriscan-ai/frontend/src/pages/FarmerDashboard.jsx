import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { useToast } from '../contexts/ToastContext';
import { Scan, Activity, ArrowRight, Camera, Leaf } from 'lucide-react';
import WeatherWidget from '../components/WeatherWidget';

const FarmerDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const { addToast } = useToast();

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await api.get('/dashboard');
        setData(res.data);
      } catch (error) {
        addToast('Failed to load dashboard data', 'error');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, [addToast]);

  if (loading) {
    return <div className="h-[60vh] flex items-center justify-center">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-500 border-t-transparent"></div>
    </div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Dashboard Overview</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-1">Monitor your farm's health at a glance.</p>
        </div>
        <Link to="/detect" className="btn-primary flex items-center gap-2">
          <Camera className="w-5 h-5" />
          New Scan
        </Link>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="glass-card p-6 flex flex-col justify-between">
          <div className="flex justify-between items-start mb-4">
            <div>
              <p className="text-gray-500 dark:text-gray-400 font-medium text-sm">Total Scans</p>
              <h3 className="text-3xl font-bold mt-1">{data?.stats.total_scans || 0}</h3>
            </div>
            <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-lg text-blue-600 dark:text-blue-400">
              <Scan className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="glass-card p-6 flex flex-col justify-between">
          <div className="flex justify-between items-start mb-4">
            <div>
              <p className="text-gray-500 dark:text-gray-400 font-medium text-sm">Healthy Crops</p>
              <h3 className="text-3xl font-bold mt-1 text-green-600 dark:text-green-400">{data?.stats.healthy_crops || 0}</h3>
            </div>
            <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-lg text-green-600 dark:text-green-400">
              <Activity className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="glass-card p-6 flex flex-col justify-between">
          <div className="flex justify-between items-start mb-4">
            <div>
              <p className="text-gray-500 dark:text-gray-400 font-medium text-sm">Diseased Crops</p>
              <h3 className="text-3xl font-bold mt-1 text-red-600 dark:text-red-400">{data?.stats.diseased_crops || 0}</h3>
            </div>
            <div className="p-3 bg-red-100 dark:bg-red-900/30 rounded-lg text-red-600 dark:text-red-400">
              <Activity className="w-6 h-6" />
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        {/* Recent Scans Table */}
        <div className="lg:col-span-2 glass-card p-6">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-lg font-bold">Recent Predictions</h3>
            <Link to="/history" className="text-sm text-primary-600 font-medium flex items-center hover:underline">
              View All <ArrowRight className="w-4 h-4 ml-1" />
            </Link>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-gray-200 dark:border-dark-border text-gray-500 text-sm">
                  <th className="pb-3 font-medium">Crop & Disease</th>
                  <th className="pb-3 font-medium">Date</th>
                  <th className="pb-3 font-medium">Status</th>
                  <th className="pb-3 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {data?.recent_predictions?.length > 0 ? (
                  data.recent_predictions.map((scan) => (
                    <tr key={scan.id} className="border-b border-gray-100 dark:border-dark-border/50 last:border-0 hover:bg-gray-50/50 dark:hover:bg-gray-800/20 transition-colors">
                      <td className="py-4">
                        <div className="flex items-center gap-3">
                          <img src={`http://localhost:5000${scan.image_path}`} alt="crop" className="w-10 h-10 rounded object-cover bg-gray-200" />
                          <div>
                            <p className="font-semibold text-gray-900 dark:text-gray-100">{scan.crop_name || 'Unknown'}</p>
                            <p className="text-xs text-gray-500">{scan.disease_name}</p>
                          </div>
                        </div>
                      </td>
                      <td className="py-4 text-sm text-gray-600 dark:text-gray-400">
                        {new Date(scan.scan_date).toLocaleDateString()}
                      </td>
                      <td className="py-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          scan.is_healthy ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                        }`}>
                          {scan.is_healthy ? 'Healthy' : 'Diseased'}
                        </span>
                      </td>
                      <td className="py-4 text-right">
                        <Link to={`/result/${scan.id}`} className="text-primary-600 hover:text-primary-700 text-sm font-medium">
                          Details
                        </Link>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="4" className="py-8 text-center text-gray-500">No recent scans found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Weather Widget */}
        <div className="lg:col-span-1">
          <WeatherWidget />
          
          {/* Quick Tip */}
          <div className="glass-card p-6 mt-6 bg-gradient-to-br from-primary-50 to-white dark:from-primary-900/20 dark:to-dark-card border-primary-100 dark:border-primary-900/30">
            <div className="flex items-start gap-3">
              <div className="p-2 bg-primary-100 dark:bg-primary-900/50 rounded-lg text-primary-600">
                <Leaf className="w-5 h-5" />
              </div>
              <div>
                <h4 className="font-bold text-gray-900 dark:text-gray-100 mb-1">Crop Tip of the Day</h4>
                <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                  Water tomatoes deeply at the base, 1–2 inches per week. Avoid overhead watering to reduce fungal disease risk.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FarmerDashboard;
