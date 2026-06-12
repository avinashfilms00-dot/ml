import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axios';
import { useToast } from '../contexts/ToastContext';
import { User, Phone, MapPin, Globe, Lock, Loader2 } from 'lucide-react';

const ProfilePage = () => {
  const { user, setUser } = useAuth();
  const { addToast } = useToast();
  
  const [formData, setFormData] = useState({
    name: user?.name || '',
    phone: user?.phone || '',
    location: user?.location || '',
    language: user?.language || 'en'
  });
  
  const [passData, setPassData] = useState({
    current_password: '',
    new_password: '',
    confirm_password: ''
  });

  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPass, setSavingPass] = useState(false);

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setSavingProfile(true);
    try {
      const res = await api.put('/profile', formData);
      setUser(res.data.user);
      addToast('Profile updated successfully', 'success');
    } catch (error) {
      addToast('Failed to update profile', 'error');
    } finally {
      setSavingProfile(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (passData.new_password !== passData.confirm_password) {
      addToast('New passwords do not match', 'error');
      return;
    }
    setSavingPass(true);
    try {
      await api.put('/profile/password', {
        current_password: passData.current_password,
        new_password: passData.new_password
      });
      addToast('Password changed successfully', 'success');
      setPassData({ current_password: '', new_password: '', confirm_password: '' });
    } catch (error) {
      addToast(error.response?.data?.message || 'Failed to change password', 'error');
    } finally {
      setSavingPass(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Profile Settings</h1>
        <p className="text-gray-500">Manage your account details and preferences.</p>
      </div>

      <div className="grid md:grid-cols-3 gap-6">
        <div className="md:col-span-2 space-y-6">
          <div className="glass-card p-6">
            <h3 className="text-lg font-bold mb-4 border-b pb-2 dark:border-dark-border">Personal Information</h3>
            <form onSubmit={handleProfileSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Full Name</label>
                <div className="relative">
                  <User className="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
                  <input type="text" className="input-field pl-10" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Phone Number</label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
                    <input type="text" className="input-field pl-10" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} placeholder="+1 234 567 890" />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Language</label>
                  <div className="relative">
                    <Globe className="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
                    <select className="input-field pl-10 appearance-none" value={formData.language} onChange={e => setFormData({...formData, language: e.target.value})}>
                      <option value="en">English</option>
                      <option value="hi">Hindi (हिन्दी)</option>
                    </select>
                  </div>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Farm Location</label>
                <div className="relative">
                  <MapPin className="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
                  <input type="text" className="input-field pl-10" value={formData.location} onChange={e => setFormData({...formData, location: e.target.value})} placeholder="City, Region" />
                </div>
              </div>
              <div className="pt-2">
                <button type="submit" disabled={savingProfile} className="btn-primary flex items-center gap-2">
                  {savingProfile ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>

          <div className="glass-card p-6">
            <h3 className="text-lg font-bold mb-4 border-b pb-2 dark:border-dark-border">Security</h3>
            <form onSubmit={handlePasswordSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Current Password</label>
                <div className="relative">
                  <Lock className="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
                  <input type="password" required className="input-field pl-10" value={passData.current_password} onChange={e => setPassData({...passData, current_password: e.target.value})} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">New Password</label>
                  <input type="password" required minLength={6} className="input-field" value={passData.new_password} onChange={e => setPassData({...passData, new_password: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Confirm New Password</label>
                  <input type="password" required minLength={6} className="input-field" value={passData.confirm_password} onChange={e => setPassData({...passData, confirm_password: e.target.value})} />
                </div>
              </div>
              <div className="pt-2">
                <button type="submit" disabled={savingPass} className="btn-secondary flex items-center gap-2">
                  {savingPass ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Update Password'}
                </button>
              </div>
            </form>
          </div>
        </div>

        <div className="md:col-span-1">
          <div className="glass-card p-6 text-center border-t-4 border-primary-500">
            <div className="w-24 h-24 mx-auto rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-3xl text-white font-bold mb-4 shadow-lg">
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            <h3 className="text-xl font-bold">{user?.name}</h3>
            <p className="text-gray-500 text-sm mb-4">{user?.email}</p>
            <span className="inline-block px-3 py-1 bg-primary-100 text-primary-800 dark:bg-primary-900/30 dark:text-primary-300 rounded-full text-xs font-bold uppercase tracking-wide">
              {user?.role}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
