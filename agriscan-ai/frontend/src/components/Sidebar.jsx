import { NavLink } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  Home, Scan, History, User, Settings, LogOut, 
  Leaf, X, BarChart2, Users 
} from 'lucide-react';

const Sidebar = ({ isOpen, setIsOpen }) => {
  const { user, logout } = useAuth();
  
  const farmerLinks = [
    { name: 'Dashboard', path: '/dashboard', icon: Home },
    { name: 'Disease Detection', path: '/detect', icon: Scan },
    { name: 'Scan History', path: '/history', icon: History },
  ];
  
  const adminLinks = [
    { name: 'Admin Dashboard', path: '/admin', icon: Home },
    { name: 'Users', path: '/admin/users', icon: Users },
    { name: 'Analytics', path: '/analytics', icon: BarChart2 },
  ];
  
  const links = user?.role === 'admin' ? adminLinks : farmerLinks;

  return (
    <aside 
      className={`fixed inset-y-0 left-0 z-50 w-64 glass-card border-l-0 border-t-0 border-b-0 rounded-none flex flex-col
      transition-transform duration-300 ease-in-out lg:translate-x-0
      ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}
    >
      <div className="h-16 flex items-center justify-between px-6 border-b border-gray-200 dark:border-dark-border">
        <div className="flex items-center gap-2 text-primary-600 dark:text-primary-400">
          <Leaf className="w-8 h-8" />
          <span className="text-xl font-bold font-display">AgriScan AI</span>
        </div>
        <button onClick={() => setIsOpen(false)} className="lg:hidden text-gray-500">
          <X className="w-6 h-6" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto py-6 flex flex-col gap-2 px-4">
        {links.map((link) => {
          const Icon = link.icon;
          return (
            <NavLink
              key={link.name}
              to={link.path}
              onClick={() => setIsOpen(false)}
              className={({ isActive }) => `
                flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all duration-200
                ${isActive 
                  ? 'bg-primary-50 dark:bg-primary-900/20 text-primary-600 dark:text-primary-400' 
                  : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-dark-card hover:text-gray-900 dark:hover:text-gray-200'}
              `}
            >
              <Icon className="w-5 h-5" />
              {link.name}
            </NavLink>
          );
        })}
      </div>

      <div className="p-4 border-t border-gray-200 dark:border-dark-border flex flex-col gap-2">
        <NavLink
          to="/profile"
          onClick={() => setIsOpen(false)}
          className={({ isActive }) => `
            flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all duration-200
            ${isActive ? 'bg-primary-50 dark:bg-primary-900/20 text-primary-600 dark:text-primary-400' : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-dark-card'}
          `}
        >
          <User className="w-5 h-5" />
          Profile
        </NavLink>
        <button
          onClick={logout}
          className="flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-all duration-200 text-left w-full"
        >
          <LogOut className="w-5 h-5" />
          Logout
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
