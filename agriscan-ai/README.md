# AgriScan AI – Smart Crop Disease Detection Platform

A complete full-stack AI-powered Crop Disease Detection System for farmers. 
Built with React, Flask, MySQL, and TensorFlow.

## Project Structure

```text
agriscan-ai/
├── frontend/       # React + Vite + Tailwind CSS frontend
├── backend/        # Python Flask REST API
├── ml/             # TensorFlow MobileNetV2 Model & Training scripts
├── database/       # MySQL Schema & Seed data
└── docs/           # Additional documentation
```

## Prerequisites

1. **Node.js** (v18+) & **npm** - For running the React frontend.
2. **Python** (3.9+) - For the Flask backend and ML model training.
3. **MySQL Server** - For the database.

---

## 1. Database Setup

1. Open MySQL Command Line or Workbench.
2. Run the SQL schema:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
3. This creates the `agriscan_db` database and all required tables.

---

## 2. Backend Setup (Flask API)

1. Navigate to the backend folder:
   ```bash
   cd agriscan-ai/backend
   ```
2. Create and activate a Python virtual environment:
   ```bash
   python -m venv venv
   # On Windows:
   venv\Scripts\activate
   # On Mac/Linux:
   source venv/bin/activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Copy the environment variables template to the root folder:
   ```bash
   cp ../.env.example ../.env
   ```
5. Edit `../.env` and update your MySQL `DB_PASSWORD`.
6. Seed the database with initial disease metadata and admin account:
   ```bash
   python ../database/seed_diseases.py
   ```
7. Start the Flask server:
   ```bash
   python run.py
   ```
   *The backend will start at `http://localhost:5000`*

---

## 3. Machine Learning Setup (Optional - Training)

A pre-trained model `agriscan_model.h5` is required for prediction to work.
You can train it yourself using the provided script:

1. Navigate to the ML folder:
   ```bash
   cd agriscan-ai/ml
   ```
2. Install TensorFlow and Datasets (if not already installed in your venv):
   ```bash
   pip install tensorflow tensorflow-datasets matplotlib
   ```
3. Run the training script:
   ```bash
   python train.py
   ```
   *Note: This will download the PlantVillage dataset (~800MB) and begin 2-phase transfer learning on MobileNetV2. It is highly recommended to run this on a GPU.*

If you bypass this step, you must place a valid `agriscan_model.h5` file inside the `ml/` folder.

---

## 4. Frontend Setup (React UI)

1. Navigate to the frontend folder:
   ```bash
   cd agriscan-ai/frontend
   ```
2. Install Node modules:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will start at `http://localhost:5173`*

---

## Default Accounts

After running `seed_diseases.py`, the following account is available:

**Admin Account:**
- **Email:** admin@agriscan.ai
- **Password:** Admin@123

To create a Farmer account, simply use the "Sign Up" page on the frontend.

## Key Features Developed
- **Complete Real ML Pipeline**: Transfer learning with MobileNetV2 using tfds PlantVillage dataset.
- **Full Backend API**: Secure JWT auth, Image handling, ORM models.
- **Premium Frontend UI**: React + Tailwind with glassmorphism, Recharts, Framer Motion animations.
- **Multi-Role Support**: Distinct Farmer and Admin dashboards.
- **PDF & Excel Reports**: Admin analytics export capabilities.
