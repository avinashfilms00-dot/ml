import { useState, useEffect } from 'react';
import { Cloud, Sun, CloudRain, Wind } from 'lucide-react';

const WeatherWidget = () => {
  // In a real app, you would fetch this from OpenWeatherMap using the location
  // For demo, we use static data to ensure it always renders beautifully
  
  return (
    <div className="glass-card p-6 relative overflow-hidden group">
      {/* Background decoration */}
      <div className="absolute -right-6 -top-6 w-32 h-32 bg-yellow-400/10 rounded-full blur-2xl group-hover:bg-yellow-400/20 transition-all duration-500"></div>
      
      <div className="flex justify-between items-start relative z-10">
        <div>
          <h3 className="text-gray-500 dark:text-gray-400 font-medium text-sm uppercase tracking-wider">Local Weather</h3>
          <p className="text-2xl font-bold mt-1">Pune, IN</p>
          <p className="text-gray-600 dark:text-gray-300 mt-4 text-sm font-medium">Mostly Sunny</p>
        </div>
        <div className="text-right">
          <div className="flex items-center gap-2 text-yellow-500">
            <Sun className="w-10 h-10" />
            <span className="text-4xl font-display font-bold text-gray-900 dark:text-white">32°</span>
          </div>
        </div>
      </div>
      
      <div className="grid grid-cols-3 gap-4 mt-6 pt-6 border-t border-gray-100 dark:border-dark-border relative z-10">
        <div className="flex flex-col items-center">
          <CloudRain className="w-5 h-5 text-blue-500 mb-1" />
          <span className="text-xs text-gray-500">Humidity</span>
          <span className="font-semibold text-sm">45%</span>
        </div>
        <div className="flex flex-col items-center">
          <Wind className="w-5 h-5 text-gray-400 mb-1" />
          <span className="text-xs text-gray-500">Wind</span>
          <span className="font-semibold text-sm">12 km/h</span>
        </div>
        <div className="flex flex-col items-center">
          <Cloud className="w-5 h-5 text-gray-400 mb-1" />
          <span className="text-xs text-gray-500">Rain Prob.</span>
          <span className="font-semibold text-sm">10%</span>
        </div>
      </div>
    </div>
  );
};

export default WeatherWidget;
