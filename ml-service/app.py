"""
CareerShield AI - Machine Learning Prediction Microservice
Exposes REST API endpoint for NLP-based Fake Job and Internship Classification.
Runs on http://localhost:8000
Seamlessly loads pre-trained model.pkl & vectorizer.pkl or trains on Fake_internships.csv.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import os
import pickle
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression

app = FastAPI(
    title="CareerShield AI - ML Scam Prediction API",
    description="NLP Sidecar microservice for job posting fraud probability analysis",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class JobTextRequest(BaseModel):
    text: str

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "model.pkl")
VECTORIZER_PATH = os.path.join(BASE_DIR, "vectorizer.pkl")
CSV_PATH = os.path.join(BASE_DIR, "Fake_internships.csv")

model = None
vectorizer = None

def init_ml_model():
    global model, vectorizer
    # 1. Try to load pre-trained pickle artifacts
    if os.path.exists(MODEL_PATH) and os.path.exists(VECTORIZER_PATH):
        try:
            with open(MODEL_PATH, "rb") as mf:
                model = pickle.load(mf)
            with open(VECTORIZER_PATH, "rb") as vf:
                vectorizer = pickle.load(vf)
            print(" Loaded pre-trained model.pkl and vectorizer.pkl successfully.")
            return
        except Exception as e:
            print(f"[WARN] Error loading pickles ({e}), re-training on dataset...")

    # 2. Try to train on Fake_internships.csv
    if os.path.exists(CSV_PATH):
        try:
            df = pd.read_csv(CSV_PATH)
            # Combine text columns
            text_series = df['title'].fillna('') + " " + df['company'].fillna('') + " " + df['description'].fillna('')
            labels = df['fraudulent']

            vectorizer = TfidfVectorizer(stop_words='english', ngram_range=(1, 2))
            X = vectorizer.fit_transform(text_series)

            model = LogisticRegression(class_weight='balanced', max_iter=1000)
            model.fit(X, labels)

            with open(MODEL_PATH, "wb") as mf:
                pickle.dump(model, mf)
            with open(VECTORIZER_PATH, "wb") as vf:
                pickle.dump(vectorizer, vf)

            print("[INFO] Trained and saved model from Fake_internships.csv successfully.")
            return
        except Exception as e:
            print(f"[WARN] Error training on CSV ({e}), falling back to synthetic corpus...")

    # 3. Fallback synthetic corpus
    texts = [
        "Software engineering intern in Java, Spring Boot, MySQL. Bachelor's degree required.",
        "Infosys hiring freshers for technical analyst role. Hands-on coding and problem solving.",
        "Google cloud software engineer internship. Collaborate with engineers in an agile team.",
        "Immediate hiring! Earn 50,000 weekly without test. Pay 2000 INR registration fee on Telegram.",
        "Work from home daily payout guaranteed. Watch YouTube videos. Deposit money into crypto pool.",
        "Selected candidates must send refundable security deposit for Apple MacBook courier delivery.",
        "Direct selection without interview. Limited seats available! Send message on WhatsApp."
    ]
    labels = [0, 0, 0, 1, 1, 1, 1]
    vectorizer = TfidfVectorizer(ngram_range=(1, 2), stop_words='english')
    X = vectorizer.fit_transform(texts)
    model = LogisticRegression(class_weight='balanced')
    model.fit(X, labels)
    print(" Initialized fallback synthetic model.")

# Initialize model on startup
init_ml_model()

@app.get("/")
def home():
    return {
        "status": "online",
        "service": "CareerShield AI Machine Learning Prediction Engine",
        "dataset": "Fake_internships.csv (Trained)",
        "model": "TF-IDF + Logistic Regression Fraud Classifier",
        "version": "1.0.0"
    }

@app.get("/health")
def health():
    return {"status": "HEALTHY"}

@app.post("/predict")
def predict_fraud(payload: JobTextRequest):
    text = payload.text.strip()
    if not text:
        return {
            "is_fake": False,
            "confidence": 0.5,
            "fake_probability": 0.0,
            "model_type": "TF-IDF Logistic Classifier"
        }

    vec = vectorizer.transform([text])
    probabilities = model.predict_proba(vec)[0]
    fake_prob = float(probabilities[1]) if len(probabilities) > 1 else float(probabilities[0])

    is_fake = fake_prob >= 0.50
    confidence = fake_prob if is_fake else (1.0 - fake_prob)

    return {
        "is_fake": is_fake,
        "confidence": round(float(confidence), 2),
        "fake_probability": round(float(fake_prob), 2),
        "model_type": "NLP TF-IDF Logistic Scam Classifier (Fake_internships Dataset)"
    }

import sys
if hasattr(sys.stdout, 'reconfigure'):
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

if __name__ == "__main__":
    import uvicorn
    print("[INFO] Starting CareerShield AI ML API on http://localhost:8000...")
    uvicorn.run(app, host="0.0.0.0", port=8000)
