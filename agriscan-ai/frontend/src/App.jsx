import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './contexts/AuthContext'

// Layouts
import MainLayout from './layouts/MainLayout'

// Pages
import LandingPage from './pages/LandingPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import FarmerDashboard from './pages/FarmerDashboard'
import DetectionPage from './pages/DetectionPage'
import PredictionResult from './pages/PredictionResult'
import ScanHistory from './pages/ScanHistory'
import AdminDashboard from './pages/AdminDashboard'
import AnalyticsPage from './pages/AnalyticsPage'
import ProfilePage from './pages/ProfilePage'

function App() {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-bg">
      <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-500 border-t-transparent"></div>
    </div>
  }

  // Protected Route Wrapper
  const ProtectedRoute = ({ children, requiredRole }) => {
    if (!isAuthenticated) return <Navigate to="/login" replace />
    if (requiredRole && user?.role !== requiredRole) {
      return <Navigate to={user?.role === 'admin' ? '/admin' : '/dashboard'} replace />
    }
    return <MainLayout>{children}</MainLayout>
  }

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to={user?.role === 'admin' ? '/admin' : '/dashboard'} />} />
      <Route path="/register" element={!isAuthenticated ? <RegisterPage /> : <Navigate to="/dashboard" />} />

      {/* Farmer Protected Routes */}
      <Route path="/dashboard" element={<ProtectedRoute requiredRole="farmer"><FarmerDashboard /></ProtectedRoute>} />
      <Route path="/detect" element={<ProtectedRoute requiredRole="farmer"><DetectionPage /></ProtectedRoute>} />
      <Route path="/result/:id" element={<ProtectedRoute requiredRole="farmer"><PredictionResult /></ProtectedRoute>} />
      <Route path="/history" element={<ProtectedRoute requiredRole="farmer"><ScanHistory /></ProtectedRoute>} />

      {/* Admin Protected Routes */}
      <Route path="/admin" element={<ProtectedRoute requiredRole="admin"><AdminDashboard /></ProtectedRoute>} />
      <Route path="/analytics" element={<ProtectedRoute requiredRole="admin"><AnalyticsPage /></ProtectedRoute>} />

      {/* Shared Protected Routes */}
      <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
