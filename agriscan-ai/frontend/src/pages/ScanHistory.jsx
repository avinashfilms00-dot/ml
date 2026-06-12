import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { useToast } from '../contexts/ToastContext';
import { Search, Filter, Calendar, Trash2, ArrowRight } from 'lucide-react';

const ScanHistory = () => {
  const [scans, setScans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('all');
  const { addToast } = useToast();

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const res = await api.get('/history');
      setScans(res.data.scans);
    } catch (error) {
      addToast('Failed to load scan history', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this scan record?')) return;
    
    try {
      await api.delete(`/history/${id}`);
      setScans(scans.filter(scan => scan.id !== id));
      addToast('Record deleted successfully', 'success');
    } catch (error) {
      addToast('Failed to delete record', 'error');
    }
  };

  const filteredScans = scans.filter(scan => {
    const matchesSearch = scan.disease_name.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          (scan.crop_name && scan.crop_name.toLowerCase().includes(searchTerm.toLowerCase()));
    
    if (filter === 'healthy') return matchesSearch && scan.is_healthy;
    if (filter === 'diseased') return matchesSearch && !scan.is_healthy;
    return matchesSearch;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Scan History</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-1">Review your past disease detections.</p>
        </div>
      </div>

      {/* Filters & Search */}
      <div className="glass-card p-4 flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input 
            type="text" 
            placeholder="Search by crop or disease..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field pl-10"
          />
        </div>
        <div className="flex gap-2">
          <select 
            value={filter} 
            onChange={(e) => setFilter(e.target.value)}
            className="input-field w-auto appearance-none bg-no-repeat bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23131313%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.4-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:12px_12px] bg-[right_16px_center]"
          >
            <option value="all">All Scans</option>
            <option value="healthy">Healthy Only</option>
            <option value="diseased">Diseased Only</option>
          </select>
        </div>
      </div>

      {/* Results */}
      <div className="glass-card overflow-hidden">
        {loading ? (
          <div className="p-12 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-2 border-primary-500 border-t-transparent mx-auto"></div>
          </div>
        ) : filteredScans.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-1 p-1 bg-gray-100 dark:bg-dark-border">
            {filteredScans.map((scan) => (
              <div key={scan.id} className="bg-white dark:bg-dark-card p-4 flex flex-col justify-between hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
                <div className="flex gap-4">
                  <div className="w-20 h-20 shrink-0 rounded-lg overflow-hidden bg-gray-200">
                    <img src={`http://localhost:5000${scan.image_path}`} alt="crop" className="w-full h-full object-cover" />
                  </div>
                  <div>
                    <h3 className="font-bold text-gray-900 dark:text-gray-100 line-clamp-1">{scan.disease_name}</h3>
                    <p className="text-sm text-gray-500 mb-2">{scan.crop_name}</p>
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                      scan.is_healthy ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                    }`}>
                      {scan.is_healthy ? 'Healthy' : 'Diseased'}
                    </span>
                  </div>
                </div>
                
                <div className="mt-4 pt-4 border-t border-gray-100 dark:border-dark-border flex justify-between items-center">
                  <div className="flex items-center text-xs text-gray-500">
                    <Calendar className="w-3.5 h-3.5 mr-1" />
                    {new Date(scan.scan_date).toLocaleDateString()}
                  </div>
                  <div className="flex gap-2">
                    <button onClick={() => handleDelete(scan.id)} className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded">
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <Link to={`/result/${scan.id}`} className="p-1.5 text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded">
                      <ArrowRight className="w-4 h-4" />
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="p-12 text-center text-gray-500">
            <p>No scans found matching your criteria.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ScanHistory;
