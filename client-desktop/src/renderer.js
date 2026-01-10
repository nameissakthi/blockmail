/**
 * BlockMail - Quantum Secure Email Client
 * Main Renderer Process
 */

import './index.css';
import { createLoginComponent } from './components/auth/Login.js';
import { createRegisterComponent } from './components/auth/Register.js';
import { createEmailListComponent } from './components/email/EmailList.js';
import { createComposeComponent } from './components/email/Compose.js';
import { createEmailViewComponent } from './components/email/EmailView.js';
import store from './store/store.js';
import authAPI from './api/auth.js';
import kmAPI from './api/km.js';

class BlockMailApp {
  constructor() {
    this.currentView = null;
    this.currentTab = 'inbox';
    this.init();
  }

  async init() {
    // Check if user is already authenticated
    const token = localStorage.getItem('authToken');
    const userEmail = localStorage.getItem('userEmail');

    if (token && userEmail) {
      // Token exists, restore session
      store.setAuth({ email: userEmail }, token);
      this.showMainApp();
    } else {
      this.showAuth('login');
    }

    // Set up event listeners
    this.setupEventListeners();

    // Subscribe to store changes
    store.subscribe((state) => this.onStateChange(state));
  }

  setupEventListeners() {
    // Auth events
    window.addEventListener('auth:login', () => this.showMainApp());
    window.addEventListener('auth:logout', () => this.logout());
    window.addEventListener('auth:showLogin', () => this.showAuth('login'));
    window.addEventListener('auth:showRegister', () => this.showAuth('register'));

    // Email events
    window.addEventListener('email:compose', () => this.showCompose());
    window.addEventListener('email:open', (e) => this.showEmail(e.detail));
    window.addEventListener('email:close', () => this.showEmailList(this.currentTab));
    window.addEventListener('email:sent', () => {
      this.showEmailList(this.currentTab);
      this.closeCompose();
    });
    window.addEventListener('email:deleted', () => this.showEmailList(this.currentTab));
    window.addEventListener('email:reply', (e) => this.showCompose(e.detail));
    window.addEventListener('compose:close', () => this.closeCompose());
  }

  showAuth(type = 'login') {
    const appContainer = document.getElementById('app');
    appContainer.innerHTML = '';

    const component = type === 'login'
      ? createLoginComponent()
      : createRegisterComponent();

    appContainer.appendChild(component);
  }

  async showMainApp() {
    const appContainer = document.getElementById('app');
    appContainer.innerHTML = `
      <div class="main-container">
        <aside class="sidebar">
          <div class="sidebar-header">
            <h1>🔐 BlockMail</h1>
            <p class="user-email">${store.getState().user?.email || ''}</p>
          </div>
          
          <nav class="sidebar-nav">
            <button class="nav-item active" data-tab="inbox">
              📥 Inbox
            </button>
            <button class="nav-item" data-tab="sent">
              📤 Sent
            </button>
            <button class="nav-item compose-btn">
              ✉️ Compose
            </button>
          </nav>
          
          <div class="sidebar-footer">
            <div class="km-status" id="kmStatus">
              <span class="status-indicator">⚫</span>
              <span class="status-text">Checking QKD...</span>
            </div>
            <button class="btn btn-text" id="logoutBtn">🚪 Logout</button>
          </div>
        </aside>
        
        <main class="main-content" id="mainContent">
          <!-- Content will be loaded here -->
        </main>
      </div>
      
      <div class="modal-overlay" id="modalOverlay" style="display: none;">
        <div class="modal-content" id="modalContent">
          <!-- Modal content will be loaded here -->
        </div>
      </div>
    `;

    // Set up navigation
    const navItems = appContainer.querySelectorAll('.nav-item:not(.compose-btn)');
    navItems.forEach(item => {
      item.addEventListener('click', (e) => {
        // Update active state
        navItems.forEach(nav => nav.classList.remove('active'));
        e.target.classList.add('active');

        // Show corresponding view
        const tab = e.target.dataset.tab;
        this.currentTab = tab;
        this.showEmailList(tab);
      });
    });

    // Compose button
    const composeBtn = appContainer.querySelector('.compose-btn');
    composeBtn.addEventListener('click', () => this.showCompose());

    // Logout button
    const logoutBtn = appContainer.querySelector('#logoutBtn');
    logoutBtn.addEventListener('click', () => this.logout());

    // Check KM status
    this.checkKMStatus();

    // Show inbox by default
    this.showEmailList('inbox');
  }

  showEmailList(type = 'inbox') {
    const mainContent = document.getElementById('mainContent');
    if (!mainContent) return;

    mainContent.innerHTML = '';
    const emailList = createEmailListComponent(type);
    mainContent.appendChild(emailList);
  }

  showCompose(replyTo = null) {
    const modalOverlay = document.getElementById('modalOverlay');
    const modalContent = document.getElementById('modalContent');

    if (!modalOverlay || !modalContent) return;

    modalContent.innerHTML = '';
    const composeComponent = createComposeComponent(replyTo);
    modalContent.appendChild(composeComponent);

    modalOverlay.style.display = 'flex';
  }

  closeCompose() {
    const modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) {
      modalOverlay.style.display = 'none';
    }
  }

  showEmail(emailId) {
    const mainContent = document.getElementById('mainContent');
    if (!mainContent) return;

    mainContent.innerHTML = '';
    const emailView = createEmailViewComponent(emailId);
    mainContent.appendChild(emailView);
  }

  async checkKMStatus() {
    const kmStatus = document.getElementById('kmStatus');
    if (!kmStatus) return;

    try {
      const status = await kmAPI.getStatus();
      const indicator = kmStatus.querySelector('.status-indicator');
      const text = kmStatus.querySelector('.status-text');

      if (status.connected) {
        indicator.textContent = '🟢';
        text.textContent = 'KM Connected';
        store.setKMStatus(status);
      } else {
        indicator.textContent = '🔴';
        text.textContent = 'KM Offline';
      }
    } catch (error) {
      const indicator = kmStatus.querySelector('.status-indicator');
      const text = kmStatus.querySelector('.status-text');
      indicator.textContent = '🔴';
      text.textContent = 'KM Error';
    }
  }

  async logout() {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    }

    store.clearAuth();
    this.showAuth('login');
  }

  onStateChange(state) {
    // Handle global state changes if needed
    if (state.error) {
      console.error('Application error:', state.error);
    }
  }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  new BlockMailApp();
});

