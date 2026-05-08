import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private currentLang = new BehaviorSubject<string>('en');
  currentLang$ = this.currentLang.asObservable();

  private translations: any = {
    en: {
      'LOGIN': {
        'BRAND_TITLE': 'Sachintha Timber Mall',
        'BRAND_SUBTITLE': 'Wood & Furniture Management System',
        'FEATURE_INVENTORY': 'Comprehensive Inventory Management',
        'FEATURE_TRACKING': 'Real-time Order Tracking',
        'FEATURE_SECURE': 'Secure & Reliable',
        'WELCOME_BACK': 'Welcome Back',
        'SIGN_IN_PROMPT': 'Please sign in to your account',
        'USERNAME': 'Username',
        'PASSWORD': 'Password',
        'USERNAME_PLACEHOLDER': 'Enter your username',
        'PASSWORD_PLACEHOLDER': 'Enter your password',
        'REMEMBER_ME': 'Remember me',
        'RESET_PASSWORD_LINK': 'Reset Password',
        'CHANGE_PASSWORD_LINK': 'Change Password?',
        'SIGN_IN_BTN': 'Sign In',
        'SIGNING_IN_BTN': 'Signing In...',
        'RESET_MODAL_TITLE': 'Reset Password?',
        'RESET_MODAL_BODY': 'Are you sure you want to reset the password for user',
        'RESET_MODAL_INFO': 'Your password will be set to the default: password123',
        'RESET_MODAL_NOTE': 'You will be required to change it immediately after logging in.',
        'CANCEL_BTN': 'Cancel',
        'CONFIRM_RESET_BTN': 'Confirm Reset',
        'SUCCESS_LOGIN': 'Login successful! Welcome back.',
        'WARNING_RESET': 'Your password has been reset. Please change it to continue.',
        'ERROR_INVALID': 'Invalid username or password',
        'ERROR_LOCKED': 'Your account has been locked. Please try again in 5 minutes.',
        'ERROR_DISABLED': 'Your account has been disabled. Please contact support.',
        'ERROR_CONNECTION': 'Unable to connect to server. Please check your connection.',
        'ERROR_GENERAL': 'An error occurred during login. Please try again.'
      },
      'CHANGE_PASSWORD': {
        'SECURE_ACCOUNT': 'Secure Your Account',
        'BRAND_SUBTITLE': 'Wood & Furniture Management System',
        'FEATURE_PROTECT': 'Protect your business data',
        'FEATURE_MANAGE': 'Manage your access securely',
        'FEATURE_ENTERPRISE': 'Enterprise-grade security',
        'TITLE': 'Change Password',
        'SUBTITLE': 'Update your login credentials',
        'USERNAME': 'Username',
        'CURRENT_PASSWORD': 'Current Password',
        'NEW_PASSWORD': 'New Password',
        'CONFIRM_PASSWORD': 'Confirm New Password',
        'USERNAME_PLACEHOLDER': 'Enter your username',
        'CURRENT_PASSWORD_PLACEHOLDER': 'Enter current password',
        'NEW_PASSWORD_PLACEHOLDER': 'At least 6 characters',
        'CONFIRM_PASSWORD_PLACEHOLDER': 'Must match new password',
        'STRENGTH_LABEL': 'Password Strength:',
        'PASSWORDS_NOT_MATCH': 'Passwords do not match',
        'UPDATE_BTN': 'Update Password',
        'UPDATING_BTN': 'Updating...',
        'CANCEL_GO_BACK': 'Cancel and go back',
        'SUCCESS_CHANGE': 'Password changed successfully! Please login with your new password.',
        'WARNING_MIN_LENGTH': 'New password must be at least 6 characters',
        'STRENGTH': {
            'WEAK': 'Weak',
            'FAIR': 'Fair',
            'GOOD': 'Good',
            'STRONG': 'Strong'
        }
      }
    },
    si: {
      'LOGIN': {
        'BRAND_TITLE': 'සචින්ත ටිම්බර් මෝල්',
        'BRAND_SUBTITLE': 'ලී සහ ගෘහ භාණ්ඩ කළමනාකරණ පද්ධතිය',
        'FEATURE_INVENTORY': 'විස්තීර්ණ තොග කළමනාකරණය',
        'FEATURE_TRACKING': 'තත්‍ය කාලීන ඇණවුම් ලුහුබැඳීම',
        'FEATURE_SECURE': 'ආරක්ෂිත සහ විශ්වසනීය',
        'WELCOME_BACK': 'නැවත සාදරයෙන් පිළිගනිමු',
        'SIGN_IN_PROMPT': 'කරුණාකර ඔබගේ ගිණුමට ඇතුළු වන්න',
        'USERNAME': 'පරිශීලක නාමය',
        'PASSWORD': 'මුරපදය',
        'USERNAME_PLACEHOLDER': 'ඔබගේ පරිශීලක නාමය ඇතුළත් කරන්න',
        'PASSWORD_PLACEHOLDER': 'ඔබගේ මුරපදය ඇතුළත් කරන්න',
        'REMEMBER_ME': 'මතක තබා ගන්න',
        'RESET_PASSWORD_LINK': 'මුරපදය නැවත සකසන්න',
        'CHANGE_PASSWORD_LINK': 'මුරපදය වෙනස් කරන්න?',
        'SIGN_IN_BTN': 'ඇතුළු වන්න',
        'SIGNING_IN_BTN': 'ඇතුළු වෙමින්...',
        'RESET_MODAL_TITLE': 'මුරපදය නැවත සකසන්නද?',
        'RESET_MODAL_BODY': 'පරිශීලකයා සඳහා මුරපදය නැවත සැකසීමට ඔබට විශ්වාසද?',
        'RESET_MODAL_INFO': 'ඔබගේ මුරපදය පෙරනිමියට සැකසෙනු ඇත: password123',
        'RESET_MODAL_NOTE': 'ඇතුළු වූ වහාම එය වෙනස් කිරීමට ඔබට අවශ්‍ය වනු ඇත.',
        'CANCEL_BTN': 'අවලංගු කරන්න',
        'CONFIRM_RESET_BTN': 'නැවත සැකසීම තහවුරු කරන්න',
        'SUCCESS_LOGIN': 'ප්‍රවිෂ්ට වීම සාර්ථකයි! නැවත සාදරයෙන් පිළිගනිමු.',
        'WARNING_RESET': 'ඔබේ මුරපදය නැවත සකසා ඇත. කරුණාකර ඉදිරියට යාමට එය වෙනස් කරන්න.',
        'ERROR_INVALID': 'වලංගු නොවන පරිශීලක නාමයක් හෝ මුරපදයක්',
        'ERROR_LOCKED': 'ඔබගේ ගිණුම අගුලු දමා ඇත. කරුණාකර මිනිත්තු 5කින් නැවත උත්සාහ කරන්න.',
        'ERROR_DISABLED': 'ඔබගේ ගිණුම අක්‍රිය කර ඇත. කරුණාකර සහාය අමතන්න.',
        'ERROR_CONNECTION': 'සේවාදායකයට සම්බන්ධ වීමට නොහැක. කරුණාකර ඔබේ සම්බන්ධතාවය පරීක්ෂා කරන්න.',
        'ERROR_GENERAL': 'ඇතුළු වීමේදී දෝෂයක් ඇති විය. කරුණාකර නැවත උත්සාහ කරන්න.'
      },
      'CHANGE_PASSWORD': {
        'SECURE_ACCOUNT': 'ඔබේ ගිණුම සුරක්ෂිත කරන්න',
        'BRAND_SUBTITLE': 'ලී සහ ගෘහ භාණ්ඩ කළමනාකරණ පද්ධතිය',
        'FEATURE_PROTECT': 'ඔබේ ව්‍යාපාරික දත්ත සුරකින්න',
        'FEATURE_MANAGE': 'ඔබේ ප්‍රවේශය ආරක්ෂිතව කළමනාකරණය කරන්න',
        'FEATURE_ENTERPRISE': 'ව්‍යවසාය මට්ටමේ ආරක්ෂාව',
        'TITLE': 'මුරපදය වෙනස් කරන්න',
        'SUBTITLE': 'ඔබගේ ප්‍රවිෂ්ට අක්තපත්‍ර යාවත්කාලීන කරන්න',
        'USERNAME': 'පරිශීලක නාමය',
        'CURRENT_PASSWORD': 'වත්මන් මුරපදය',
        'NEW_PASSWORD': 'නව මුරපදය',
        'CONFIRM_PASSWORD': 'නව මුරපදය තහවුරු කරන්න',
        'USERNAME_PLACEHOLDER': 'පරිශීලක නාමය ඇතුළත් කරන්න',
        'CURRENT_PASSWORD_PLACEHOLDER': 'වත්මන් මුරපදය ඇතුළත් කරන්න',
        'NEW_PASSWORD_PLACEHOLDER': 'අවම වශයෙන් අක්ෂර 6 ක්',
        'CONFIRM_PASSWORD_PLACEHOLDER': 'නව මුරපදයට සමාන විය යුතුය',
        'STRENGTH_LABEL': 'මුරපද ශක්තිය:',
        'PASSWORDS_NOT_MATCH': 'මුරපද නොගැලපේ',
        'UPDATE_BTN': 'මුරපදය යාවත්කාලීන කරන්න',
        'UPDATING_BTN': 'යාවත්කාලීන වෙමින්...',
        'CANCEL_GO_BACK': 'අවලංගු කර ආපසු යන්න',
        'SUCCESS_CHANGE': 'මුරපදය සාර්ථකව වෙනස් කරන ලදී! කරුණාකර ඔබගේ නව මුරපදය සමඟ ඇතුළු වන්න.',
        'WARNING_MIN_LENGTH': 'නව මුරපදය අවම වශයෙන් අක්ෂර 6 ක් විය යුතුය',
        'STRENGTH': {
            'WEAK': 'දුර්වල',
            'FAIR': 'සාමාන්‍ය',
            'GOOD': 'හොඳ',
            'STRONG': 'ශක්තිමත්'
        }
      }
    }
  };

  constructor() {
    if (typeof localStorage !== 'undefined') {
      const savedLang = localStorage.getItem('selectedLang');
      if (savedLang) {
        this.currentLang.next(savedLang);
      }
    }
  }

  setLanguage(lang: string) {
    this.currentLang.next(lang);
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('selectedLang', lang);
    }
  }

  getLanguage() {
    return this.currentLang.value;
  }

  translate(key: string): string {
    const lang = this.currentLang.value;
    const keys = key.split('.');
    let translation = this.translations[lang];
    
    for (const k of keys) {
      if (translation && translation[k]) {
        translation = translation[k];
      } else {
        return key; // Return key if translation not found
      }
    }
    
    return translation;
  }
}
