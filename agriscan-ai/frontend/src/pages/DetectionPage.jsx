import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { UploadCloud, Camera, Image as ImageIcon, X, Loader2 } from 'lucide-react';
import { motion } from 'framer-motion';
import api from '../api/axios';
import { useToast } from '../contexts/ToastContext';

const DetectionPage = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const fileInputRef = useRef(null);
  const navigate = useNavigate();
  const { addToast } = useToast();

  const handleFileSelect = (file) => {
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      addToast('Please upload an image file', 'error');
      return;
    }
    
    setSelectedFile(file);
    const objectUrl = URL.createObjectURL(file);
    setPreview(objectUrl);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      handleFileSelect(e.dataTransfer.files[0]);
    }
  };

  const clearSelection = () => {
    setSelectedFile(null);
    setPreview(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const handleAnalyze = async () => {
    if (!selectedFile) return;
    
    setIsAnalyzing(true);
    const formData = new FormData();
    formData.append('image', selectedFile);

    try {
      // Allow longer timeout for ML inference
      const res = await api.post('/predict', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 30000 
      });
      
      addToast('Analysis complete!', 'success');
      navigate(`/result/${res.data.scan_id}`);
    } catch (error) {
      console.error(error);
      addToast(error.response?.data?.message || 'Analysis failed. Please try again.', 'error');
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto py-8">
      <div className="text-center mb-10">
        <h1 className="text-3xl font-bold font-display mb-3">Crop Disease Detection</h1>
        <p className="text-gray-500 dark:text-gray-400">Upload an image of a crop leaf for instant AI analysis and treatment recommendations.</p>
      </div>

      <div className="glass-card p-8 shadow-xl">
        {!preview ? (
          <div
            className={`border-3 border-dashed rounded-2xl p-12 text-center transition-all duration-300
              ${isDragging 
                ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20 scale-[1.02]' 
                : 'border-gray-300 dark:border-dark-border hover:border-primary-400 dark:hover:border-primary-600'}`}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
          >
            <div className="flex justify-center mb-6">
              <div className="w-20 h-20 bg-primary-100 dark:bg-primary-900/30 rounded-full flex items-center justify-center text-primary-600 dark:text-primary-400">
                <UploadCloud className="w-10 h-10" />
              </div>
            </div>
            
            <h3 className="text-xl font-bold mb-2">Drag & Drop Image</h3>
            <p className="text-gray-500 dark:text-gray-400 mb-8">or click below to browse from your device</p>
            
            <input 
              type="file" 
              ref={fileInputRef} 
              onChange={(e) => handleFileSelect(e.target.files[0])} 
              accept="image/*" 
              className="hidden" 
            />
            
            <div className="flex flex-col sm:flex-row justify-center gap-4">
              <button 
                onClick={() => fileInputRef.current?.click()}
                className="btn-primary flex items-center justify-center gap-2"
              >
                <ImageIcon className="w-5 h-5" />
                Browse Gallery
              </button>
              <button 
                onClick={() => fileInputRef.current?.click()}
                className="btn-secondary flex items-center justify-center gap-2"
              >
                <Camera className="w-5 h-5" />
                Use Camera
              </button>
            </div>
          </div>
        ) : (
          <motion.div 
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="relative"
          >
            <button 
              onClick={clearSelection}
              disabled={isAnalyzing}
              className="absolute -top-4 -right-4 w-10 h-10 bg-white dark:bg-gray-800 rounded-full shadow-lg flex items-center justify-center text-gray-500 hover:text-red-500 transition-colors z-10 disabled:opacity-50"
            >
              <X className="w-6 h-6" />
            </button>
            
            <div className="rounded-2xl overflow-hidden bg-black aspect-video flex items-center justify-center border border-gray-200 dark:border-dark-border shadow-inner relative">
              <img src={preview} alt="Crop Preview" className={`max-w-full max-h-[500px] object-contain transition-all ${isAnalyzing ? 'blur-sm brightness-50' : ''}`} />
              
              {/* Scanning Animation Overlay */}
              {isAnalyzing && (
                <div className="absolute inset-0 flex flex-col items-center justify-center z-20">
                  <div className="w-full h-1 bg-primary-500 shadow-[0_0_15px_rgba(34,197,94,0.8)] animate-[scan_2s_ease-in-out_infinite] absolute top-0"></div>
                  <Loader2 className="w-16 h-16 text-white animate-spin mb-4 drop-shadow-lg" />
                  <p className="text-white font-bold text-xl drop-shadow-lg tracking-wide">AI is analyzing the leaf...</p>
                </div>
              )}
            </div>
            
            <div className="mt-8 flex justify-center">
              <button 
                onClick={handleAnalyze} 
                disabled={isAnalyzing}
                className="btn-primary w-full md:w-auto md:px-16 py-4 text-lg font-bold shadow-primary-500/40"
              >
                {isAnalyzing ? 'Processing Image...' : 'Detect Disease'}
              </button>
            </div>
          </motion.div>
        )}
        
        {/* Supported Crops Note */}
        {!preview && (
          <div className="mt-10 text-center">
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">Supported Crops</p>
            <div className="flex flex-wrap justify-center gap-2">
              {['Tomato', 'Potato', 'Corn', 'Apple', 'Grape', 'Peach', 'Strawberry'].map((crop) => (
                <span key={crop} className="px-3 py-1 bg-gray-100 dark:bg-dark-card rounded-full text-xs font-medium border border-gray-200 dark:border-dark-border">
                  {crop}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
      
      {/* Global scan animation keyframes */}
      <style dangerouslySetInnerHTML={{__html: `
        @keyframes scan {
          0% { top: 0%; opacity: 0; }
          10% { opacity: 1; }
          90% { opacity: 1; }
          100% { top: 100%; opacity: 0; }
        }
      `}} />
    </div>
  );
};

export default DetectionPage;
