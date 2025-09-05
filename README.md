# AIDS Tracker & Management App

An Android application integrated with Machine Learning to help patients and doctors manage and monitor HIV/AIDS progression.

The app allows patients to upload medical reports, track CD4/CD8 counts, visualize progress with graphs, and get stage predictions with recommendations for diet and medication.

#  Features

📤 Report Upload – Patients can upload blood test reports (CD4/CD8 counts).

📊 Progress Tracking – Graphs showing health improvement, decline, or stability.

🧠 ML Model Integration – Predicts:

         Current HIV stage (based on CD4/CD8 counts).

💬 Chatbot – Provides guidance, FAQs, and patient support.

📋 Diet & Medicine Recommendations – Personalized suggestions based on stage.

🔒 Login & Signup.

# 🛠️ Tech Stack

Frontend (Mobile App): Android (Java + XML, Android Studio)

Backend (API): FastAPI (Python)

Database: SQLite

Machine Learning:

     Used Classification models (Random Forest):

     CD4/CD8 trend prediction

     Hiv Stage Detection

# AIDS-Tracker-Management/
│

├── app/ 

│   ├── java/com/example/aidsapp/

│   │   ├── MainActivity.java

│   │   ├── LoginActivity.java

│   │   ├── SignupActivity.java

│   │   ├── UploadActivity.java

│   │   ├── ResultActivity.java

│   │   ├── ProgressActivity.java

│   │   ├── ProfileActivity.java

│   │   ├── DatabaseHelper.java

│   │   ├── ProgressChartActivity.java

│   │   └── ChatbotActivity.java

│   └── res/layout/           

│

├── backend/                  

│   ├── main.py               

│   ├── models/               

│   └── database/             

│

├── ml_models/                

│   ├── hiv_stage_model.pkl

│   └── cd4_prediction_model.pkl

│

└── README.md                

# Screenshots of project 
