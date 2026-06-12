import { Link } from 'react-router-dom';
import { Leaf, ShieldCheck, Zap, Activity } from 'lucide-react';
import { motion } from 'framer-motion';

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-bg selection:bg-primary-500/30 transition-colors duration-300">
      {/* Navbar */}
      <nav className="fixed w-full z-50 glass-card border-x-0 border-t-0 rounded-none px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-2 text-primary-600 dark:text-primary-400">
          <Leaf className="w-8 h-8" />
          <span className="text-2xl font-bold font-display tracking-tight">AgriScan AI</span>
        </div>
        <div className="flex gap-4">
          <Link to="/login" className="btn-secondary">Login</Link>
          <Link to="/register" className="btn-primary">Get Started</Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="pt-32 pb-20 px-6 relative overflow-hidden">
        {/* Background Gradients */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-3xl h-[500px] bg-primary-500/20 rounded-full blur-[120px] -z-10 pointer-events-none"></div>
        
        <div className="max-w-5xl mx-auto text-center mt-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <span className="inline-block py-1 px-3 rounded-full bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300 text-sm font-semibold mb-6 border border-primary-200 dark:border-primary-800">
              AI-Powered Agriculture 🌾
            </span>
            <h1 className="text-5xl md:text-7xl font-bold tracking-tight mb-8 text-gray-900 dark:text-white leading-tight">
              Detect Crop Diseases <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary-600 to-green-400">
                Instantly with AI
              </span>
            </h1>
            <p className="text-xl text-gray-600 dark:text-gray-300 mb-10 max-w-2xl mx-auto leading-relaxed">
              Upload a photo of your crop leaf and our advanced machine learning model will instantly diagnose diseases and provide actionable treatment recommendations.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link to="/register" className="btn-primary text-lg py-4 px-8">Start Free Scan</Link>
              <a href="#features" className="btn-secondary text-lg py-4 px-8">Learn More</a>
            </div>
          </motion.div>
        </div>

        {/* Hero Mockup */}
        <motion.div 
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7, delay: 0.2 }}
          className="max-w-4xl mx-auto mt-20 relative z-10"
        >
          <div className="glass-card p-2 rounded-3xl shadow-2xl border-white/40">
            <div className="bg-gray-100 dark:bg-gray-800 rounded-2xl aspect-video overflow-hidden relative flex items-center justify-center">
              {/* Fake dashboard mockup */}
              <div className="w-full h-full bg-gradient-to-br from-gray-50 to-gray-200 dark:from-dark-bg dark:to-gray-900 p-8 flex flex-col">
                 <div className="h-8 w-full flex gap-2 mb-8">
                   <div className="w-3 h-3 rounded-full bg-red-400"></div>
                   <div className="w-3 h-3 rounded-full bg-yellow-400"></div>
                   <div className="w-3 h-3 rounded-full bg-green-400"></div>
                 </div>
                 <div className="flex gap-8 flex-1">
                   <div className="w-1/3 bg-white/50 dark:bg-white/5 rounded-xl border border-white/20"></div>
                   <div className="w-2/3 flex flex-col gap-4">
                     <div className="h-1/2 bg-white/50 dark:bg-white/5 rounded-xl border border-white/20"></div>
                     <div className="flex gap-4 h-1/2">
                       <div className="flex-1 bg-white/50 dark:bg-white/5 rounded-xl border border-white/20"></div>
                       <div className="flex-1 bg-white/50 dark:bg-white/5 rounded-xl border border-white/20"></div>
                     </div>
                   </div>
                 </div>
              </div>
            </div>
          </div>
        </motion.div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-24 px-6 bg-white dark:bg-slate-900">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">Why Choose AgriScan AI?</h2>
            <p className="text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">Our platform combines cutting-edge AI with agronomic expertise to help you maximize your yield.</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="p-8 rounded-2xl bg-gray-50 dark:bg-dark-card border border-gray-100 dark:border-dark-border hover:shadow-xl transition-all hover:-translate-y-1">
              <div className="w-14 h-14 rounded-xl bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center mb-6 text-primary-600 dark:text-primary-400">
                <Zap className="w-7 h-7" />
              </div>
              <h3 className="text-xl font-bold mb-3">Instant Diagnosis</h3>
              <p className="text-gray-600 dark:text-gray-400">Get results in seconds. Our MobileNetV2 architecture processes images instantly with high accuracy.</p>
            </div>
            
            <div className="p-8 rounded-2xl bg-gray-50 dark:bg-dark-card border border-gray-100 dark:border-dark-border hover:shadow-xl transition-all hover:-translate-y-1">
              <div className="w-14 h-14 rounded-xl bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center mb-6 text-blue-600 dark:text-blue-400">
                <ShieldCheck className="w-7 h-7" />
              </div>
              <h3 className="text-xl font-bold mb-3">Actionable Treatments</h3>
              <p className="text-gray-600 dark:text-gray-400">Don't just know the disease, know how to fix it. Get specific chemical and biological treatment steps.</p>
            </div>

            <div className="p-8 rounded-2xl bg-gray-50 dark:bg-dark-card border border-gray-100 dark:border-dark-border hover:shadow-xl transition-all hover:-translate-y-1">
              <div className="w-14 h-14 rounded-xl bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center mb-6 text-orange-600 dark:text-orange-400">
                <Activity className="w-7 h-7" />
              </div>
              <h3 className="text-xl font-bold mb-3">Track History</h3>
              <p className="text-gray-600 dark:text-gray-400">Keep a log of all your field scans to monitor disease spread and seasonal patterns on your farm.</p>
            </div>
          </div>
        </div>
      </section>
      
      {/* Footer */}
      <footer className="bg-gray-50 dark:bg-dark-bg py-12 px-6 border-t border-gray-200 dark:border-dark-border text-center text-gray-500">
        <p>© 2026 AgriScan AI. Built for the modern farmer.</p>
      </footer>
    </div>
  );
};

export default LandingPage;
