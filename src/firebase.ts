import { initializeApp, getApps, getApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || 'AIzaSyALh36Z8w5pyIHi9ybjGbHkMfVF13TenSs',
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || 'gen-lang-client-0556535763.firebaseapp.com',
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || 'gen-lang-client-0556535763',
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || 'gen-lang-client-0556535763.firebasestorage.app',
  messagingSenderId: '181037209010',
  appId: import.meta.env.VITE_FIREBASE_APP_ID || '1:181037209010:web:ausbildung-radar-germany',
};

const app = !getApps().length ? initializeApp(firebaseConfig) : getApp();

export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });

// Custom database named during provisioning, with fallback to default
let dbInstance;
try {
  dbInstance = getFirestore(app, 'ai-studio-android-ausbildu-d760f243-40ee-46f2-8e1d-33ea02fbf2df');
} catch (e) {
  dbInstance = getFirestore(app);
}

export const db = dbInstance;
export default app;
