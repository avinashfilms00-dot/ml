"""
AgriScan AI – Database Seeder
Seeds the diseases table with complete PlantVillage class information.
Also creates the default admin user.

Usage: python seed_diseases.py
"""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'backend'))

from app import create_app
from models import db
from models.user import User
from models.disease import Disease
import bcrypt

app = create_app()

DISEASES_DATA = [
    # ─── APPLE ───────────────────────────────────────────────────────────
    {
        "class_name": "Apple___Apple_scab",
        "crop": "Apple", "disease_name": "Apple Scab", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Apple scab is a fungal disease caused by Venturia inaequalis. It is one of the most common and serious diseases of apple trees worldwide.",
        "symptoms": ["Olive-green or brown spots on leaves", "Velvety or scabby lesions on fruit", "Premature leaf drop", "Cracked and deformed fruit", "Dark, corky lesions on twigs"],
        "causes": ["Fungal pathogen Venturia inaequalis", "Wet and cool spring weather", "Poor air circulation in orchard", "Infected leaf litter from previous season"],
        "prevention": ["Plant resistant apple varieties", "Rake and destroy fallen leaves", "Prune for good air circulation", "Apply fungicide sprays from bud break", "Avoid overhead irrigation"],
        "treatment": ["Apply captan or mancozeb fungicide", "Use myclobutanil or tebuconazole for systemic control", "Remove infected plant material", "Apply lime sulfur during dormancy"],
        "fertilizers": ["Balanced NPK 10-10-10", "Calcium-rich fertilizers to strengthen cell walls", "Avoid excess nitrogen which promotes succulent growth"],
    },
    {
        "class_name": "Apple___Black_rot",
        "crop": "Apple", "disease_name": "Black Rot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Black rot of apple is caused by the fungus Botryosphaeria obtusa. It affects leaves, fruit, and bark, causing significant crop losses.",
        "symptoms": ["Circular brown spots with purple margins on leaves", "Mummified black fruit", "Cankers on branches", "Frog-eye leaf spot pattern", "Rotting fruit with concentric rings"],
        "causes": ["Fungal infection by Botryosphaeria obtusa", "Wounds from insects or pruning", "Warm and wet conditions", "Stressed or weakened trees"],
        "prevention": ["Prune out dead or diseased wood", "Remove mummified fruit", "Maintain tree vigor with proper fertilization", "Avoid wounding trees unnecessarily"],
        "treatment": ["Apply captan, mancozeb, or thiophanate-methyl", "Remove and destroy infected tissue", "Paint pruning wounds with wound dressing"],
        "fertilizers": ["Balanced NPK fertilizer", "Potassium fertilizers to improve fruit quality"],
    },
    {
        "class_name": "Apple___Cedar_apple_rust",
        "crop": "Apple", "disease_name": "Cedar Apple Rust", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Cedar apple rust is caused by the fungus Gymnosporangium juniperi-virginianae. It requires both apple/crabapple and eastern red cedar/juniper hosts to complete its life cycle.",
        "symptoms": ["Yellow-orange spots on upper leaf surface", "Orange, tube-like structures on leaf undersides", "Premature defoliation", "Malformed fruit with lesions"],
        "causes": ["Alternate host fungus Gymnosporangium juniperi-virginianae", "Presence of both apple and juniper trees nearby", "Wet spring weather", "Wind-dispersed spores from juniper galls"],
        "prevention": ["Remove nearby juniper trees", "Plant rust-resistant apple varieties", "Apply preventive fungicides in spring", "Use physical barriers"],
        "treatment": ["Apply myclobutanil or triadimefon fungicide", "Remove infected leaves", "Spray during wet spring periods"],
        "fertilizers": ["Balanced NPK", "Micronutrient supplements with zinc and manganese"],
    },
    {
        "class_name": "Apple___healthy",
        "crop": "Apple", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "The apple plant appears healthy with no signs of disease. Continue good agricultural practices.",
        "symptoms": ["Green, vibrant leaves", "No spots or lesions", "Normal fruit development"],
        "causes": [],
        "prevention": ["Regular monitoring", "Proper irrigation", "Balanced fertilization", "Annual pruning"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["NPK 10-10-10 during growing season", "Compost for soil health"],
    },
    # ─── BLUEBERRY ───────────────────────────────────────────────────────
    {
        "class_name": "Blueberry___healthy",
        "crop": "Blueberry", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Blueberry plant is healthy and shows no disease symptoms.",
        "symptoms": ["Deep green leaves", "Normal berry development"],
        "causes": [],
        "prevention": ["Maintain acidic soil pH 4.5–5.5", "Proper mulching", "Regular watering"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Ammonium sulfate for acid-loving plants", "Rhododendron fertilizer"],
    },
    # ─── CHERRY ──────────────────────────────────────────────────────────
    {
        "class_name": "Cherry_(including_sour)___Powdery_mildew",
        "crop": "Cherry", "disease_name": "Powdery Mildew", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Powdery mildew on cherry is caused by Podosphaera clandestina. It appears as white powdery growth on young leaves and shoots.",
        "symptoms": ["White powdery coating on leaves", "Curling and distortion of young leaves", "Stunted shoot growth", "Premature defoliation in severe cases"],
        "causes": ["Fungal pathogen Podosphaera clandestina", "High humidity with warm temperatures", "Poor air circulation", "Shaded growing conditions"],
        "prevention": ["Prune for good airflow", "Avoid overhead irrigation", "Plant resistant varieties", "Apply preventive fungicides"],
        "treatment": ["Sulfur-based fungicides", "Potassium bicarbonate sprays", "Neem oil applications", "Myclobutanil or trifloxystrobin"],
        "fertilizers": ["Balanced NPK", "Avoid excess nitrogen"],
    },
    {
        "class_name": "Cherry_(including_sour)___healthy",
        "crop": "Cherry", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Cherry plant is healthy with no disease symptoms.",
        "symptoms": ["Glossy green leaves", "Normal fruit set"],
        "causes": [],
        "prevention": ["Regular pruning", "Proper irrigation", "Pest monitoring"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Balanced fruit tree fertilizer", "Potassium in summer"],
    },
    # ─── CORN (MAIZE) ────────────────────────────────────────────────────
    {
        "class_name": "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot",
        "crop": "Corn", "disease_name": "Gray Leaf Spot / Cercospora Leaf Spot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Gray leaf spot (GLS) is caused by the fungus Cercospora zeae-maydis. It is one of the most significant yield-limiting diseases of corn worldwide.",
        "symptoms": ["Rectangular gray-tan lesions on leaves", "Lesions parallel to leaf veins", "Lesions may coalesce causing blight", "Lesions surrounded by yellow halo", "Premature leaf death in severe cases"],
        "causes": ["Cercospora zeae-maydis fungus", "High humidity and extended leaf wetness", "Warm temperatures 25–30°C", "Minimum tillage retaining infected residue", "Dense planting reducing airflow"],
        "prevention": ["Use resistant hybrids", "Rotate crops with non-host plants", "Tillage to reduce infected residue", "Ensure proper plant spacing", "Monitor weather and apply fungicides proactively"],
        "treatment": ["Apply azoxystrobin or pyraclostrobin fungicide", "Propiconazole or trifloxystrobin sprays", "Apply at VT/R1 growth stage for best results"],
        "fertilizers": ["Balanced NPK especially nitrogen for vigorous growth", "Silicon fertilizers improve disease resistance", "Potassium to strengthen cell walls"],
    },
    {
        "class_name": "Corn_(maize)___Common_rust_",
        "crop": "Corn", "disease_name": "Common Rust", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Common rust of corn is caused by Puccinia sorghi. While usually not catastrophic, severe infections can reduce yield by reducing photosynthesis.",
        "symptoms": ["Small, circular to elongated reddish-brown pustules on leaves", "Pustules on both leaf surfaces", "Yellow halo around pustules", "Shredding of leaf tissue in severe cases", "Premature plant death if severe"],
        "causes": ["Puccinia sorghi fungus", "Cool temperatures 15–25°C", "High humidity and dew periods", "Wind dispersal of spores from alternate hosts"],
        "prevention": ["Plant resistant hybrids", "Early planting to avoid peak rust season", "Monitor fields regularly", "Crop rotation"],
        "treatment": ["Azoxystrobin, pyraclostrobin, or trifloxystrobin", "Propiconazole fungicide", "Apply when first pustules appear"],
        "fertilizers": ["Adequate phosphorus and potassium", "Balanced NPK 20-10-10"],
    },
    {
        "class_name": "Corn_(maize)___Northern_Leaf_Blight",
        "crop": "Corn", "disease_name": "Northern Leaf Blight", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Northern leaf blight (NLB) is caused by Exserohilum turcicum. It can cause significant yield losses, especially when it infects plants early.",
        "symptoms": ["Large, cigar-shaped gray-green lesions", "Lesions 2.5–15 cm long", "Tan centers with dark green to gray borders", "Lesions produce dark spores in humid conditions", "Blighting of entire leaves in severe cases"],
        "causes": ["Exserohilum turcicum fungus", "Moderate temperatures 18–27°C", "High humidity and prolonged leaf wetness", "Infected crop debris in soil", "Susceptible varieties"],
        "prevention": ["Plant resistant hybrids with Ht genes", "Crop rotation", "Tillage to decompose infected residue", "Adequate plant spacing", "Avoid overhead irrigation"],
        "treatment": ["Azoxystrobin or propiconazole fungicide", "Apply at VT/R1 growth stage", "Repeat applications if disease pressure is high"],
        "fertilizers": ["High nitrogen for vigorous growth", "Balanced NPK", "Sulfur micronutrients"],
    },
    {
        "class_name": "Corn_(maize)___healthy",
        "crop": "Corn", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Corn plant is healthy and growing normally. Continue current agronomic practices.",
        "symptoms": ["Dark green upright leaves", "Proper ear development", "No lesions or discoloration"],
        "causes": [],
        "prevention": ["Crop rotation", "Proper plant spacing", "Regular scouting", "Balanced fertilization"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Urea or ammonium nitrate for nitrogen", "NPK 28-0-0 top dressing", "Micronutrients zinc and boron"],
    },
    # ─── GRAPE ───────────────────────────────────────────────────────────
    {
        "class_name": "Grape___Black_rot",
        "crop": "Grape", "disease_name": "Black Rot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Grape black rot, caused by Guignardia bidwellii, can destroy up to 80% of the fruit crop in wet seasons.",
        "symptoms": ["Brown circular lesions with black borders on leaves", "Small black pycnidia within lesions", "Infected berries become shriveled mummies", "Tan lesions on shoots and tendrils"],
        "causes": ["Guignardia bidwellii fungus", "Warm temperatures 26–28°C", "Wet weather with frequent rain", "Infected mummified berries from previous season"],
        "prevention": ["Remove mummified berries and infected canes", "Apply fungicides from bud break", "Improve canopy airflow through pruning", "Avoid excessive foliage density"],
        "treatment": ["Mancozeb, captan, or myclobutanil", "Ziram or ferbam applications", "Apply preventively every 10–14 days during wet periods"],
        "fertilizers": ["Balanced vine nutrition", "Adequate potassium for strong wood"],
    },
    {
        "class_name": "Grape___Esca_(Black_Measles)",
        "crop": "Grape", "disease_name": "Esca (Black Measles)", "is_healthy": False,
        "severity": "Critical", "severity_color": "#7c3aed",
        "description": "Esca is a complex grapevine trunk disease caused by multiple fungal pathogens. It is a serious, chronic disease with no cure.",
        "symptoms": ["Tiger-stripe pattern on leaves", "Berry spotting and shriveling", "Apoplexy – sudden vine collapse", "Necrotic wood in trunk cross-section", "Bleached wood in canes"],
        "causes": ["Phaeomoniella chlamydospora and Phaeoacremonium species", "Infection through pruning wounds", "Esca fungi complex", "Old vines more susceptible"],
        "prevention": ["Avoid large pruning wounds", "Apply wound protectants after pruning", "Delay pruning during wet weather", "Use clean pruning tools", "Remove infected wood promptly"],
        "treatment": ["No curative treatment available", "Remove and destroy infected vines", "Apply wound sealants containing trichoderma", "Trunk surgery in early stages"],
        "fertilizers": ["Balanced nutrition to maintain vine vigor", "Avoid nitrogen stress"],
    },
    {
        "class_name": "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
        "crop": "Grape", "disease_name": "Isariopsis Leaf Spot (Leaf Blight)", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Isariopsis leaf spot is caused by Pseudocercospora vitis. It causes premature defoliation and may impact fruit quality.",
        "symptoms": ["Irregular dark brown to black spots on leaves", "Yellow halo around spots", "Premature leaf drop", "Small, dark lesions on berries in severe cases"],
        "causes": ["Pseudocercospora vitis fungus", "Warm, wet conditions", "High humidity", "Poor canopy management"],
        "prevention": ["Improve air circulation through canopy management", "Avoid overhead irrigation", "Apply preventive fungicides"],
        "treatment": ["Copper-based fungicides", "Mancozeb applications", "Bordeaux mixture"],
        "fertilizers": ["Balanced NPK", "Micronutrients to improve plant immunity"],
    },
    {
        "class_name": "Grape___healthy",
        "crop": "Grape", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Grape vine is healthy with no signs of disease.",
        "symptoms": ["Vigorous green shoots", "Normal leaf color", "Good fruit set"],
        "causes": [],
        "prevention": ["Annual pruning", "Proper trellising", "Regular scouting for pests and disease"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Balanced vine fertilizer", "Potassium in summer for fruit quality"],
    },
    # ─── ORANGE ──────────────────────────────────────────────────────────
    {
        "class_name": "Orange___Haunglongbing_(Citrus_greening)",
        "crop": "Orange", "disease_name": "Huanglongbing (Citrus Greening)", "is_healthy": False,
        "severity": "Critical", "severity_color": "#7c3aed",
        "description": "Huanglongbing (HLB), also called citrus greening, is the most devastating citrus disease worldwide. Caused by Candidatus Liberibacter asiaticus, it has no cure.",
        "symptoms": ["Yellow mottling (blotchy mottle) on leaves", "Asymmetric yellowing unlike nutrient deficiency", "Small, lopsided fruit", "Bitter tasting fruit", "Premature fruit drop", "Dieback of twigs and branches"],
        "causes": ["Candidatus Liberibacter asiaticus (bacteria)", "Spread by Asian citrus psyllid (Diaphorina citri)", "Grafting from infected trees", "No cure once infected"],
        "prevention": ["Control Asian citrus psyllid with insecticides", "Use certified disease-free nursery trees", "Remove and destroy infected trees promptly", "Quarantine infected areas"],
        "treatment": ["No cure available", "Remove infected trees immediately", "Control psyllid vector with imidacloprid or other systemic insecticides", "Nutritional treatments to extend productive life"],
        "fertilizers": ["Micronutrient foliar sprays to support symptomatic trees", "Balanced citrus fertilizer to maintain vigor"],
    },
    # ─── PEACH ───────────────────────────────────────────────────────────
    {
        "class_name": "Peach___Bacterial_spot",
        "crop": "Peach", "disease_name": "Bacterial Spot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Bacterial spot of peach is caused by Xanthomonas arboricola pv. pruni. It affects leaves, fruit, and twigs.",
        "symptoms": ["Water-soaked spots on leaves turning angular and brown", "Shot-hole appearance as lesions drop out", "Dark, sunken lesions on fruit", "Fruit cracking and deformation", "Twig cankers"],
        "causes": ["Xanthomonas arboricola pv. pruni bacteria", "Warm, wet, and windy conditions", "Spread by rain and wind", "Infected nursery stock"],
        "prevention": ["Plant resistant varieties", "Avoid overhead irrigation", "Prune for good air circulation", "Protective copper sprays", "Use clean nursery stock"],
        "treatment": ["Copper hydroxide or copper sulfate sprays", "Oxytetracycline applications", "Apply during dormancy and early season"],
        "fertilizers": ["Balanced NPK", "Adequate calcium to strengthen cell walls"],
    },
    {
        "class_name": "Peach___healthy",
        "crop": "Peach", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Peach tree is healthy with no disease symptoms.",
        "symptoms": ["Bright green leaves", "Normal fruit development", "No spots or lesions"],
        "causes": [],
        "prevention": ["Annual pruning", "Proper thinning of fruit", "Pest and disease monitoring"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Balanced fruit tree fertilizer", "Nitrogen in spring"],
    },
    # ─── PEPPER ──────────────────────────────────────────────────────────
    {
        "class_name": "Pepper,_bell___Bacterial_spot",
        "crop": "Pepper", "disease_name": "Bacterial Spot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Bacterial spot of pepper is caused by Xanthomonas campestris pv. vesicatoria. It is a serious disease in warm, wet growing regions.",
        "symptoms": ["Water-soaked lesions on leaves", "Brown angular spots with yellow halo", "Defoliation under severe infection", "Raised, scab-like lesions on fruit", "Fruit rot in wet conditions"],
        "causes": ["Xanthomonas campestris bacteria", "Warm temperatures 24–30°C", "High humidity and rain splash", "Infected seed", "Mechanical damage creates entry points"],
        "prevention": ["Use certified disease-free seed", "Avoid overhead irrigation", "Copper sprays as protectant", "Rotate crops away from solanaceous crops", "Disinfect tools"],
        "treatment": ["Copper hydroxide + mancozeb tank mix", "Fixed copper bactericides", "Remove heavily infected plants"],
        "fertilizers": ["Balanced NPK with potassium emphasis", "Calcium nitrate to prevent tip burn and strengthen plants"],
    },
    {
        "class_name": "Pepper,_bell___healthy",
        "crop": "Pepper", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Bell pepper plant is healthy and showing normal growth.",
        "symptoms": ["Dark green glossy leaves", "Normal flower and fruit development"],
        "causes": [],
        "prevention": ["Regular watering", "Balanced fertilization", "Pest monitoring", "Staking for support"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["NPK 5-10-10 for fruiting", "Calcium and magnesium supplements"],
    },
    # ─── POTATO ──────────────────────────────────────────────────────────
    {
        "class_name": "Potato___Early_blight",
        "crop": "Potato", "disease_name": "Early Blight", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Potato early blight is caused by Alternaria solani. It typically affects older leaves first and can cause significant defoliation and yield loss.",
        "symptoms": ["Dark brown circular spots with concentric rings (target pattern)", "Spots appear first on older lower leaves", "Yellow halo surrounding lesions", "Leaf yellowing and premature defoliation", "Dark, sunken lesions on tubers"],
        "causes": ["Alternaria solani fungus", "Warm temperatures 24–29°C", "Wet conditions alternating with dry periods", "Plant stress from nutrient deficiency", "Infected plant debris"],
        "prevention": ["Use certified disease-free seed potatoes", "Rotate crops (avoid solanaceous plants for 3 years)", "Apply preventive fungicides", "Maintain adequate plant nutrition", "Avoid excessive plant stress"],
        "treatment": ["Mancozeb, chlorothalonil, or azoxystrobin", "Difenoconazole + azoxystrobin combined products", "Apply every 7–10 days during wet conditions"],
        "fertilizers": ["Adequate nitrogen for vigorous canopy", "Potassium for disease resistance", "Balanced NPK 15-15-15"],
    },
    {
        "class_name": "Potato___Late_blight",
        "crop": "Potato", "disease_name": "Late Blight", "is_healthy": False,
        "severity": "Critical", "severity_color": "#7c3aed",
        "description": "Potato late blight, caused by Phytophthora infestans, is the most devastating disease of potato, historically responsible for the Irish Famine. It can destroy an entire crop within days under favorable conditions.",
        "symptoms": ["Water-soaked pale green to brown lesions on leaves", "White sporulation on underside of leaves in humid conditions", "Dark brown to black stem lesions", "Rotten, firm brown lesions on tubers", "Rapid spreading and plant collapse in severe cases", "Foul smell from infected tissue"],
        "causes": ["Oomycete Phytophthora infestans", "Cool temperatures 10–20°C", "High humidity above 90%", "Rain and fog for extended periods", "Infected seed tubers", "Wind dispersal of spores"],
        "prevention": ["Use certified blight-free seed tubers", "Plant resistant varieties", "Apply preventive fungicides before disease appears", "Ensure good drainage", "Avoid overhead irrigation", "Scout fields regularly", "Destroy cull piles and volunteer plants"],
        "treatment": ["Metalaxyl-mancozeb (Ridomil Gold) for systemic control", "Cymoxanil + mancozeb", "Fluopicolide or mandipropamid", "Apply preventively every 5–7 days during high-risk periods", "Do not use metalaxyl alone (resistance risk)"],
        "fertilizers": ["Adequate phosphorus and potassium", "Calcium to strengthen cell walls", "Avoid excess nitrogen which promotes succulent growth"],
    },
    {
        "class_name": "Potato___healthy",
        "crop": "Potato", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Potato plant is healthy with vigorous growth and no signs of disease.",
        "symptoms": ["Dark green upright leaves", "Normal stem and foliage development", "No spots, lesions, or discoloration"],
        "causes": [],
        "prevention": ["Use certified seed potatoes", "Rotate crops annually", "Proper earthing up", "Regular irrigation and fertilization"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["NPK 12-12-17 for high potassium requirement", "Sulphate of potash", "Boron micronutrient during tuber formation"],
    },
    # ─── RASPBERRY ───────────────────────────────────────────────────────
    {
        "class_name": "Raspberry___healthy",
        "crop": "Raspberry", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Raspberry cane is healthy with no disease symptoms.",
        "symptoms": ["Green healthy canes", "Normal leaf color", "Good fruit development"],
        "causes": [],
        "prevention": ["Remove old fruiting canes after harvest", "Proper row spacing for airflow", "Mulching to reduce soil splash"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Balanced berry fertilizer", "Nitrogen in spring"],
    },
    # ─── SOYBEAN ─────────────────────────────────────────────────────────
    {
        "class_name": "Soybean___healthy",
        "crop": "Soybean", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Soybean plant is healthy with vigorous growth.",
        "symptoms": ["Bright trifoliate leaves", "Normal pod development", "No lesions"],
        "causes": [],
        "prevention": ["Crop rotation", "Use disease-resistant varieties", "Proper seed treatment"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Inoculant with Bradyrhizobium for nitrogen fixation", "Phosphorus and potassium fertilizers"],
    },
    # ─── SQUASH ──────────────────────────────────────────────────────────
    {
        "class_name": "Squash___Powdery_mildew",
        "crop": "Squash", "disease_name": "Powdery Mildew", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Powdery mildew on squash is caused by Podosphaera xanthii (or Erysiphe cichoracearum). It is very common in squash and other cucurbits.",
        "symptoms": ["White powdery spots on upper leaf surface", "Yellowing leaves", "Premature defoliation", "Reduced fruit size and quality", "Powdery coating on stems"],
        "causes": ["Podosphaera xanthii fungus", "Dry warm days with cool nights", "High humidity", "Poor air circulation"],
        "prevention": ["Plant resistant varieties", "Improve air circulation", "Avoid dense planting", "Apply preventive fungicides or biologicals"],
        "treatment": ["Potassium bicarbonate spray", "Neem oil", "Sulfur-based fungicides", "Trifloxystrobin or myclobutanil"],
        "fertilizers": ["Balanced NPK", "Avoid excess nitrogen"],
    },
    # ─── STRAWBERRY ──────────────────────────────────────────────────────
    {
        "class_name": "Strawberry___Leaf_scorch",
        "crop": "Strawberry", "disease_name": "Leaf Scorch", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Strawberry leaf scorch is caused by Diplocarpon earlianum. It appears as small dark spots that merge and cause leaf edges to appear scorched.",
        "symptoms": ["Small, dark purple spots on leaves", "Spots with reddish-purple borders", "Centers become tan/brown", "Leaves look scorched around edges", "Premature defoliation"],
        "causes": ["Diplocarpon earlianum fungus", "Wet conditions in spring and fall", "Infected plant material", "Infected strawberry beds"],
        "prevention": ["Plant disease-free runners", "Remove old leaves in winter", "Avoid overhead irrigation", "Apply protective fungicides"],
        "treatment": ["Captan or myclobutanil fungicide", "Remove and destroy infected leaves", "Apply copper fungicides"],
        "fertilizers": ["Balanced strawberry fertilizer", "Potassium for fruit quality"],
    },
    {
        "class_name": "Strawberry___healthy",
        "crop": "Strawberry", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Strawberry plant is healthy with no visible disease.",
        "symptoms": ["Green, glossy leaves", "Normal runner production", "Good fruit set"],
        "causes": [],
        "prevention": ["Annual renovation", "Proper irrigation", "Pest monitoring"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["Strawberry-specific NPK fertilizer", "Potassium before fruiting"],
    },
    # ─── TOMATO ──────────────────────────────────────────────────────────
    {
        "class_name": "Tomato___Bacterial_spot",
        "crop": "Tomato", "disease_name": "Bacterial Spot", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Tomato bacterial spot is caused by Xanthomonas vesicatoria. It is a major disease in warm, wet climates affecting leaves, stems, and fruit.",
        "symptoms": ["Small, water-soaked leaf spots turning brown", "Yellow halo around lesions", "Spots may look greasy", "Raised, scab-like spots on fruit", "Defoliation in severe cases"],
        "causes": ["Xanthomonas vesicatoria bacteria", "Warm temperatures 25–30°C", "High humidity and rain splash", "Infected seed", "Splashing soil and water"],
        "prevention": ["Use disease-free certified seed", "Apply copper bactericides preventively", "Avoid overhead irrigation", "Rotate crops for 2–3 years"],
        "treatment": ["Copper hydroxide + mancozeb", "Fixed copper bactericide every 5–7 days", "Remove infected plant material"],
        "fertilizers": ["Balanced NPK", "Calcium nitrate to strengthen tissue"],
    },
    {
        "class_name": "Tomato___Early_blight",
        "crop": "Tomato", "disease_name": "Early Blight", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Tomato early blight is caused by Alternaria solani. It typically starts on older leaves and moves upward, causing significant defoliation and reduced yields.",
        "symptoms": ["Dark brown spots with distinctive concentric rings (bull's-eye pattern)", "Yellow halo surrounding lesions", "Starts on lower, older leaves", "Defoliation from bottom up", "Dark, sunken cankers at stem base (collar rot)"],
        "causes": ["Alternaria solani fungus", "Warm temperatures 24–29°C", "Wet, humid conditions", "Plant stress and poor nutrition", "Infected debris in soil"],
        "prevention": ["Avoid overhead irrigation", "Proper spacing for air circulation", "Stake plants to keep foliage off soil", "Apply mulch to prevent soil splash", "Remove infected leaves promptly"],
        "treatment": ["Mancozeb, chlorothalonil, or copper fungicide", "Azoxystrobin or propiconazole for systemic control", "Apply every 7–10 days during wet season"],
        "fertilizers": ["Balanced tomato fertilizer NPK 8-32-16", "Calcium for strong cell walls", "Potassium for disease resistance"],
    },
    {
        "class_name": "Tomato___Late_blight",
        "crop": "Tomato", "disease_name": "Late Blight", "is_healthy": False,
        "severity": "Critical", "severity_color": "#7c3aed",
        "description": "Tomato late blight, caused by Phytophthora infestans, is the most destructive tomato disease. Under favorable conditions it can destroy an entire field within days.",
        "symptoms": ["Water-soaked olive-green spots on leaves", "White downy growth on underside of leaves", "Brown to black stem lesions", "Greasy, firm, brown lesions on fruit", "Rapid plant collapse", "Foul odor from infected tissue"],
        "causes": ["Oomycete Phytophthora infestans", "Cool, wet weather 10–20°C", "High humidity above 90%", "Overhead irrigation", "Infected transplants"],
        "prevention": ["Use resistant varieties (Mountain Magic, Defiant)", "Apply preventive fungicides before disease appears", "Ensure good air circulation", "Avoid overhead watering", "Remove infected plants immediately"],
        "treatment": ["Metalaxyl-mancozeb (Ridomil Gold)", "Cymoxanil + mancozeb combination", "Fluopicolide (Profiler)", "Phosphorous acid products", "Apply every 5–7 days when conditions favor disease"],
        "fertilizers": ["Balanced NPK", "Potassium and calcium to strengthen cell walls", "Avoid excess nitrogen"],
    },
    {
        "class_name": "Tomato___Leaf_Mold",
        "crop": "Tomato", "disease_name": "Leaf Mold", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Tomato leaf mold is caused by Passalora fulva (formerly Fulvia fulva). It is primarily a problem in greenhouse tomatoes but also occurs in field crops.",
        "symptoms": ["Pale green to yellow spots on upper leaf surface", "Olive-green to grayish-purple velvety mold on lower leaf surface", "Leaves curl and wither", "Affected leaves turn brown and drop", "Flower and fruit infection in severe cases"],
        "causes": ["Passalora fulva fungus", "High humidity above 85%", "Poor air circulation", "Temperature 22–24°C", "Dense plant canopy"],
        "prevention": ["Increase ventilation in greenhouses", "Reduce humidity below 85%", "Space plants adequately", "Prune lower leaves for airflow", "Use resistant varieties"],
        "treatment": ["Chlorothalonil or mancozeb", "Azoxystrobin or trifloxystrobin", "Copper-based fungicides", "Apply at first sign of disease"],
        "fertilizers": ["Balanced tomato fertilizer", "Avoid excessive nitrogen which promotes dense foliage"],
    },
    {
        "class_name": "Tomato___Septoria_leaf_spot",
        "crop": "Tomato", "disease_name": "Septoria Leaf Spot", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Septoria leaf spot is caused by Septoria lycopersici. It is one of the most common and destructive tomato diseases worldwide.",
        "symptoms": ["Small circular spots 3–6mm with dark borders and tan/white centers", "Tiny black specks (pycnidia) in center of spots", "Starts on lower leaves", "Severe defoliation", "Reduced fruit quality and yield"],
        "causes": ["Septoria lycopersici fungus", "Warm wet weather 20–25°C", "Extended leaf wetness", "Infected plant debris", "Overhead irrigation"],
        "prevention": ["Avoid overhead irrigation", "Mulch to reduce soil splash", "Remove infected leaves", "Rotate crops", "Space plants for airflow"],
        "treatment": ["Mancozeb, chlorothalonil, or copper", "Azoxystrobin + propiconazole", "Apply every 7–10 days when wet"],
        "fertilizers": ["Balanced tomato fertilizer", "Calcium for strong tissue"],
    },
    {
        "class_name": "Tomato___Spider_mites Two-spotted_spider_mite",
        "crop": "Tomato", "disease_name": "Two-spotted Spider Mite", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Two-spotted spider mite (Tetranychus urticae) is a major pest of tomatoes that causes stippling, bronzing, and leaf drop through feeding damage.",
        "symptoms": ["Fine stippling or bronzing on leaf surface", "Yellowing leaves", "Fine silk webbing on undersides", "Bronze or silver sheen on leaves", "Leaf drop in severe cases", "Distorted new growth"],
        "causes": ["Tetranychus urticae mite infestation", "Hot, dry conditions favor rapid reproduction", "Dusty environments", "Pesticide overuse killing natural predators"],
        "prevention": ["Maintain plant moisture – mites prefer dry conditions", "Preserve natural enemies (predatory mites)", "Avoid broad-spectrum pesticides", "Reflective mulch can deter mites", "Regular monitoring with hand lens"],
        "treatment": ["Abamectin or spiromesifen miticides", "Neem oil or insecticidal soap", "Predatory mites (Phytoseiulus persimilis) for biological control", "Water sprays to dislodge mites", "Rotate miticide modes of action"],
        "fertilizers": ["Adequate nitrogen to maintain plant vigor", "Avoid drought stress which favors mite outbreaks"],
    },
    {
        "class_name": "Tomato___Target_Spot",
        "crop": "Tomato", "disease_name": "Target Spot", "is_healthy": False,
        "severity": "Moderate", "severity_color": "#f59e0b",
        "description": "Target spot of tomato is caused by Corynespora cassiicola. It affects leaves, stems, and fruit, often causing significant defoliation.",
        "symptoms": ["Circular spots with concentric rings (target pattern) on leaves", "Dark brown lesions with yellow halo", "Lesions on stems and petioles", "Sunken spots on fruit", "Defoliation from lower canopy up"],
        "causes": ["Corynespora cassiicola fungus", "Warm temperatures 23–25°C", "High humidity and leaf wetness", "Dense canopy"],
        "prevention": ["Improve canopy airflow through pruning", "Avoid overhead watering", "Apply preventive fungicides", "Crop rotation"],
        "treatment": ["Azoxystrobin, pyraclostrobin, or difenoconazole", "Chlorothalonil for protectant activity", "Apply at first sign of disease"],
        "fertilizers": ["Balanced tomato fertilizer", "Potassium to strengthen tissue"],
    },
    {
        "class_name": "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
        "crop": "Tomato", "disease_name": "Tomato Yellow Leaf Curl Virus", "is_healthy": False,
        "severity": "Critical", "severity_color": "#7c3aed",
        "description": "Tomato Yellow Leaf Curl Virus (TYLCV) is a begomovirus transmitted by the silverleaf whitefly. It is one of the most damaging tomato viruses globally.",
        "symptoms": ["Upward curling and yellowing of leaf margins", "Interveinal chlorosis", "Stunted plant growth", "Flower and fruit drop", "Small and distorted leaves", "Severe yield loss"],
        "causes": ["TYLCV begomovirus transmitted by Bemisia tabaci whitefly", "Whitefly infestations", "Infected transplants", "High temperatures favor both virus and vector"],
        "prevention": ["Use resistant/tolerant varieties (TY varieties)", "Use insect-proof nets in nurseries", "Control whitefly with systemic insecticides", "Remove infected plants immediately", "Yellow sticky traps to monitor whitefly"],
        "treatment": ["No cure – infected plants must be removed", "Control whitefly vector with imidacloprid or thiamethoxam", "Apply neonicotinoid systemic insecticide at transplanting", "Mineral oil sprays to repel whitefly"],
        "fertilizers": ["Maintain plant nutrition to support partially tolerant varieties", "Balanced NPK"],
    },
    {
        "class_name": "Tomato___Tomato_mosaic_virus",
        "crop": "Tomato", "disease_name": "Tomato Mosaic Virus", "is_healthy": False,
        "severity": "High", "severity_color": "#ef4444",
        "description": "Tomato mosaic virus (ToMV) is a tobamovirus that can cause significant yield loss. It is highly persistent in soil and on surfaces.",
        "symptoms": ["Mosaic pattern of light and dark green on leaves", "Leaf distortion and puckering", "Stunted plant growth", "Mottling on fruit", "Fern-leaf symptom on young leaves", "Reduced fruit size"],
        "causes": ["Tomato mosaic virus (tobamovirus)", "Transmission by contact – infected hands, tools, clothing", "Infected seed", "Extremely stable – persists years in soil", "Not insect-transmitted (unlike many viruses)"],
        "prevention": ["Use certified virus-free seed", "Use resistant varieties (Tm-2 gene)", "Disinfect tools with 20% skim milk or 10% bleach", "Wash hands before handling plants", "Control tobacco use around plants", "Remove infected plants"],
        "treatment": ["No chemical cure", "Remove and destroy infected plants", "Strict sanitation protocols", "Heat treatment of seed (70°C for 2 days)"],
        "fertilizers": ["Maintain balanced nutrition to support immune plant growth"],
    },
    {
        "class_name": "Tomato___healthy",
        "crop": "Tomato", "disease_name": "Healthy", "is_healthy": True,
        "severity": "None", "severity_color": "#22c55e",
        "description": "Tomato plant is healthy, vigorous, and showing no disease symptoms. Keep up your excellent crop management!",
        "symptoms": ["Dark green healthy leaves", "Normal stem structure", "Good flower and fruit set", "No spots, lesions, or discoloration"],
        "causes": [],
        "prevention": ["Regular scouting every 3–5 days", "Consistent watering and fertilization", "Proper staking and pruning", "Crop rotation", "Integrated pest management"],
        "treatment": ["No treatment needed"],
        "fertilizers": ["NPK 8-32-16 at planting for root development", "Calcium nitrate every 2 weeks during fruiting", "Magnesium sulfate if deficiency symptoms appear"],
    },
]

CROP_TIPS = [
    {"crop": "Tomato", "category": "watering", "tip_en": "Water tomatoes deeply at the base, 1–2 inches per week. Avoid overhead watering to reduce fungal disease risk.", "tip_hi": "टमाटरों को सप्ताह में 1-2 इंच पानी दें। फंगल रोग से बचने के लिए ऊपर से पानी देने से बचें।"},
    {"crop": "Potato", "category": "watering", "tip_en": "Potatoes need consistent moisture. Irrigate regularly but avoid waterlogging to prevent late blight.", "tip_hi": "आलू को नियमित सिंचाई चाहिए। पानी का ठहराव न हो, इससे लेट ब्लाइट हो सकता है।"},
    {"crop": "Corn", "category": "fertilizing", "tip_en": "Apply nitrogen fertilizer in two splits: at planting and at knee-high stage for optimal yield.", "tip_hi": "नाइट्रोजन उर्वरक को दो बार दें: बुआई के समय और घुटने की ऊंचाई पर।"},
    {"crop": "General", "category": "general", "tip_en": "Inspect your crops early morning when symptoms are most visible and pests are active.", "tip_hi": "सुबह जल्दी फसलों की जांच करें जब रोग के लक्षण सबसे स्पष्ट होते हैं।"},
    {"crop": "General", "category": "pest_control", "tip_en": "Rotate crops each season to break pest and disease cycles in the soil.", "tip_hi": "हर मौसम में फसल चक्र अपनाएं ताकि मिट्टी में कीट और रोग का चक्र टूट जाए।"},
]


def seed():
    with app.app_context():
        print("🌱 Seeding database...")

        # Create admin user
        admin = User.query.filter_by(email='admin@agriscan.ai').first()
        if not admin:
            password = 'Admin@123'
            hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()
            admin = User(
                name='Admin',
                email='admin@agriscan.ai',
                password=hashed,
                role='admin',
                is_active=True
            )
            db.session.add(admin)
            print("✅ Admin user created: admin@agriscan.ai / Admin@123")
        else:
            print("ℹ️  Admin user already exists")

        # Seed diseases
        for data in DISEASES_DATA:
            existing = Disease.query.filter_by(class_name=data['class_name']).first()
            if not existing:
                disease = Disease(
                    class_name=data['class_name'],
                    crop=data['crop'],
                    disease_name=data['disease_name'],
                    is_healthy=data['is_healthy'],
                    description=data.get('description', ''),
                    symptoms=data.get('symptoms', []),
                    causes=data.get('causes', []),
                    prevention=data.get('prevention', []),
                    treatment=data.get('treatment', []),
                    fertilizers=data.get('fertilizers', []),
                    severity=data.get('severity', 'Moderate'),
                    severity_color=data.get('severity_color', '#f59e0b'),
                )
                db.session.add(disease)

        from models.disease import CropTip
        for tip in CROP_TIPS:
            existing = CropTip.query.filter_by(tip_en=tip['tip_en']).first()
            if not existing:
                ct = CropTip(**tip)
                db.session.add(ct)

        db.session.commit()
        print(f"✅ Seeded {len(DISEASES_DATA)} disease records")
        print(f"✅ Seeded {len(CROP_TIPS)} crop tips")
        print("\n🎉 Database seeding complete!")
        print("\nDefault Admin Login:")
        print("  Email: admin@agriscan.ai")
        print("  Password: Admin@123")


if __name__ == '__main__':
    seed()
