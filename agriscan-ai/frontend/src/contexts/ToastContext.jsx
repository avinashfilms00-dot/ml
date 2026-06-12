import { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext();

export const useToast = () => useContext(ToastContext);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info') => {
    const id = Math.random().toString(36).substr(2, 9);
    setToasts((prev) => [...prev, { id, message, type }]);
    
    setTimeout(() => {
      removeToast(id);
    }, 5000);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ addToast }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
        {toasts.map((toast) => (
          <div 
            key={toast.id}
            className={`px-4 py-3 rounded-lg shadow-lg text-white font-medium animate-slide-up flex items-center justify-between min-w-[250px]
              ${toast.type === 'success' ? 'bg-green-600' : 
                toast.type === 'error' ? 'bg-red-600' : 
                toast.type === 'warning' ? 'bg-yellow-600' : 'bg-blue-600'}`}
          >
            <span>{toast.message}</span>
            <button onClick={() => removeToast(toast.id)} className="ml-4 text-white/80 hover:text-white">
              ✕
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};
