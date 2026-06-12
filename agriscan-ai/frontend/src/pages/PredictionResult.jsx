import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, CheckCircle, AlertTriangle, Info, 
  Droplets, Shield, Stethoscope, Leaf
} from 'lucide-react';

const PredictionResult = () => {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchResult = async () => {
      try {
        const res = await api.get(`/history/${id}`);
        setData(res.data);
      } catch (error) {
        console.error(error);
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    };
    fetchResult();
  }, [id, navigate]);

  if (loading) {
    return <div className="h-[60vh] flex items-center justify-center">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-500 border-t-transparent"></div>
    </div>;
  }

  if (!data) return null;

  const isHealthy = data.is_healthy;
  const confidencePercent = Math.round(data.confidence * 100);
  const info = data.disease_info || {};
  const badgeColor = isHealthy ? 'bg-green-500' : 'bg-red-500';

  return (
    <div className="max-w-5xl mx-auto pb-12">
      <Link to="/dashboard" className="inline-flex items-center text-sm font-medium text-gray-500 hover:text-primary-600 mb-6 transition-colors">
        <ArrowLeft className="w-4 h-4 mr-1" /> Back to Dashboard
      </Link>

      <div className="grid md:grid-cols-3 gap-8">
        {/* Left Column - Image & Quick Stats */}
        <div className="md:col-span-1 space-y-6">
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="glass-card rounded-2xl overflow-hidden shadow-lg border-2"
            style={{ borderColor: isHealthy ? '#22c55e' : '#ef4444' }}
          >
            <div className="relative aspect-square">
              <img 
                src={`http://localhost:5000${data.image_path}`} 
                alt="Analyzed leaf" 
                className="w-full h-full object-cover"
              />
              <div className={`absolute top-4 right-4 ${badgeColor} text-white px-3 py-1 rounded-full text-sm font-bold flex items-center shadow-lg backdrop-blur-md bg-opacity-90`}>
                {isHealthy ? <CheckCircle className="w-4 h-4 mr-1" /> : <AlertTriangle className="w-4 h-4 mr-1" />}
                {isHealthy ? 'Healthy' : 'Diseased'}
              </div>
            </div>
            <div className="p-5 text-center bg-white dark:bg-dark-card relative z-10">
              <p className="text-gray-500 dark:text-gray-400 text-sm font-medium uppercase tracking-widest">{data.crop_name}</p>
              <h2 className="text-xl font-bold mt-1 text-gray-900 dark:text-white">{data.disease_name}</h2>
              <div className="mt-4 pt-4 border-t border-gray-100 dark:border-dark-border">
                <p className="text-sm text-gray-500 mb-1">AI Confidence</p>
                <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2.5">
                  <div className={`${badgeColor} h-2.5 rounded-full`} style={{ width: `${confidencePercent}%` }}></div>
                </div>
                <p className="text-right text-xs mt-1 font-bold">{confidencePercent}%</p>
              </div>
            </div>
          </motion.div>

          {/* Top 3 Predictions */}
          {data.top_predictions && data.top_predictions.length > 1 && (
            <motion.div 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-5"
            >
              <h3 className="font-bold mb-4 text-gray-800 dark:text-gray-200">Other Possibilities</h3>
              <div className="space-y-3">
                {data.top_predictions.slice(1).map((pred, idx) => (
                  <div key={idx} className="flex justify-between items-center text-sm">
                    <span className="text-gray-600 dark:text-gray-400">{pred.disease}</span>
                    <span className="font-semibold">{Math.round(pred.confidence * 100)}%</span>
                  </div>
                ))}
              </div>
            </motion.div>
          )}
        </div>

        {/* Right Column - Detailed Information */}
        <div className="md:col-span-2 space-y-6">
          <motion.div 
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.2 }}
            className="glass-card p-8"
          >
            <div className="flex items-center gap-3 mb-4 text-primary-600 dark:text-primary-400">
              <Info className="w-6 h-6" />
              <h2 className="text-2xl font-bold font-display">Overview</h2>
            </div>
            <p className="text-gray-600 dark:text-gray-300 leading-relaxed text-lg">
              {info.description || (isHealthy 
                ? "This crop appears to be in excellent condition. Maintain current farming practices." 
                : "A disease has been detected. Please refer to the treatment guidelines below.")}
            </p>
          </motion.div>

          {!isHealthy && (
            <>
              {/* Symptoms & Causes */}
              <motion.div 
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.3 }}
                className="grid sm:grid-cols-2 gap-6"
              >
                <div className="glass-card p-6 border-t-4 border-yellow-500">
                  <div className="flex items-center gap-2 mb-4 text-yellow-600 dark:text-yellow-500">
                    <AlertTriangle className="w-5 h-5" />
                    <h3 className="font-bold text-lg">Symptoms</h3>
                  </div>
                  <ul className="list-disc pl-5 space-y-2 text-gray-600 dark:text-gray-300">
                    {info.symptoms ? info.symptoms.map((s, i) => <li key={i}>{s}</li>) : <li>Visual lesions on leaves</li>}
                  </ul>
                </div>
                <div className="glass-card p-6 border-t-4 border-orange-500">
                  <div className="flex items-center gap-2 mb-4 text-orange-600 dark:text-orange-500">
                    <Leaf className="w-5 h-5" />
                    <h3 className="font-bold text-lg">Causes</h3>
                  </div>
                  <ul className="list-disc pl-5 space-y-2 text-gray-600 dark:text-gray-300">
                    {info.causes ? info.causes.map((c, i) => <li key={i}>{c}</li>) : <li>Fungal or bacterial pathogen</li>}
                  </ul>
                </div>
              </motion.div>

              {/* Treatment */}
              <motion.div 
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.4 }}
                className="glass-card p-6 border-l-4 border-red-500 bg-red-50/50 dark:bg-red-900/10"
              >
                <div className="flex items-center gap-2 mb-4 text-red-600 dark:text-red-500">
                  <Stethoscope className="w-6 h-6" />
                  <h3 className="font-bold text-xl">Recommended Treatment</h3>
                </div>
                <ul className="space-y-3">
                  {info.treatment ? info.treatment.map((t, i) => (
                    <li key={i} className="flex gap-3 text-gray-700 dark:text-gray-200 bg-white/50 dark:bg-black/20 p-3 rounded-lg">
                      <div className="mt-1 w-2 h-2 rounded-full bg-red-500 shrink-0"></div>
                      <span>{t}</span>
                    </li>
                  )) : <p>Consult local agronomist for specific fungicide treatments.</p>}
                </ul>
              </motion.div>

              {/* Prevention & Fertilizers */}
              <motion.div 
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.5 }}
                className="grid sm:grid-cols-2 gap-6"
              >
                <div className="glass-card p-6">
                  <div className="flex items-center gap-2 mb-4 text-blue-500">
                    <Shield className="w-5 h-5" />
                    <h3 className="font-bold text-lg">Prevention</h3>
                  </div>
                  <ul className="list-disc pl-5 space-y-2 text-gray-600 dark:text-gray-300 text-sm">
                    {info.prevention ? info.prevention.map((p, i) => <li key={i}>{p}</li>) : <li>Improve air circulation</li>}
                  </ul>
                </div>
                <div className="glass-card p-6">
                  <div className="flex items-center gap-2 mb-4 text-green-500">
                    <Droplets className="w-5 h-5" />
                    <h3 className="font-bold text-lg">Fertilizer Advice</h3>
                  </div>
                  <ul className="list-disc pl-5 space-y-2 text-gray-600 dark:text-gray-300 text-sm">
                    {info.fertilizers ? info.fertilizers.map((f, i) => <li key={i}>{f}</li>) : <li>Balanced NPK required</li>}
                  </ul>
                </div>
              </motion.div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default PredictionResult;
