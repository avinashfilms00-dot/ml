import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useToast } from '../contexts/ToastContext';
import { Users, Scan, AlertTriangle, Download, Trash2 } from 'lucide-react';

const AdminDashboard = () => {
  const [data, setData] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const { addToast } = useToast();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [dashRes, usersRes] = await Promise.all([
        api.get('/admin/dashboard'),
        api.get('/admin/users')
      ]);
      setData(dashRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      addToast('Failed to load admin data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (id) => {
    if (!window.confirm('Delete this user and all their scans?')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      setUsers(users.filter(u => u.id !== id));
      addToast('User deleted', 'success');
    } catch (e) {
      addToast('Failed to delete user', 'error');
    }
  };

  if (loading) return <div className="p-10 flex justify-center"><div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-500 border-t-transparent"></div></div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Admin Dashboard</h1>
          <p className="text-gray-500">Platform overview and user management</p>
        </div>
        <div className="flex gap-2">
          <button className="btn-secondary flex items-center gap-2"><Download className="w-4 h-4" /> PDF Report</button>
          <button className="btn-primary flex items-center gap-2"><Download className="w-4 h-4" /> Excel</button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="glass-card p-6 flex items-center gap-4 border-l-4 border-blue-500">
          <div className="p-4 bg-blue-100 dark:bg-blue-900/30 text-blue-600 rounded-xl"><Users className="w-8 h-8"/></div>
          <div><p className="text-gray-500 text-sm font-medium">Total Farmers</p><h3 className="text-2xl font-bold">{data?.stats.total_users}</h3></div>
        </div>
        <div className="glass-card p-6 flex items-center gap-4 border-l-4 border-green-500">
          <div className="p-4 bg-green-100 dark:bg-green-900/30 text-green-600 rounded-xl"><Scan className="w-8 h-8"/></div>
          <div><p className="text-gray-500 text-sm font-medium">Total Scans</p><h3 className="text-2xl font-bold">{data?.stats.total_scans}</h3></div>
        </div>
        <div className="glass-card p-6 flex items-center gap-4 border-l-4 border-red-500">
          <div className="p-4 bg-red-100 dark:bg-red-900/30 text-red-600 rounded-xl"><AlertTriangle className="w-8 h-8"/></div>
          <div><p className="text-gray-500 text-sm font-medium">Disease Types</p><h3 className="text-2xl font-bold">{data?.disease_distribution?.length || 0}</h3></div>
        </div>
      </div>

      <div className="glass-card p-6">
        <h3 className="text-lg font-bold mb-4">User Management</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b text-gray-500 text-sm">
                <th className="pb-3 font-medium">Name</th>
                <th className="pb-3 font-medium">Email</th>
                <th className="pb-3 font-medium">Joined</th>
                <th className="pb-3 font-medium text-center">Total Scans</th>
                <th className="pb-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id} className="border-b last:border-0 hover:bg-gray-50 dark:hover:bg-gray-800/20">
                  <td className="py-3 font-medium">{user.name}</td>
                  <td className="py-3 text-gray-600 dark:text-gray-400">{user.email}</td>
                  <td className="py-3 text-gray-500 text-sm">{user.created_at?.split('T')[0]}</td>
                  <td className="py-3 text-center">{user.scan_count || 0}</td>
                  <td className="py-3 text-right">
                    <button onClick={() => handleDeleteUser(user.id)} className="text-red-500 hover:bg-red-50 p-2 rounded">
                      <Trash2 className="w-4 h-4"/>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
