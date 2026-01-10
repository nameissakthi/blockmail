/**
 * Login Component
 */

import authAPI from '../../api/auth.js';
import store from '../../store/store.js';

export function createLoginComponent() {
  const container = document.createElement('div');
  container.className = 'auth-container';
  container.innerHTML = `
    <div class="auth-card">
      <div class="auth-header">
        <h1>🔐 BlockMail</h1>
        <p>Quantum Secure Email Client</p>
      </div>
      
      <form id="loginForm" class="auth-form">
        <div class="form-group">
          <label for="email">Email Address</label>
          <input 
            type="email" 
            id="email" 
            name="email" 
            required 
            placeholder="your.email@example.com"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <div class="password-input-wrapper">
            <input 
              type="password" 
              id="password" 
              name="password" 
              required 
              placeholder="Enter your password"
            />
            <button type="button" class="password-toggle" id="togglePassword">
              <span class="toggle-icon">👁️</span>
            </button>
          </div>
        </div>
        
        <div id="errorMessage" class="error-message" style="display: none;"></div>
        
        <button type="submit" class="btn btn-primary">
          <span class="btn-text">Sign In</span>
          <span class="btn-loader" style="display: none;">⏳</span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p>Don't have an account? <a href="#" id="showRegister">Register here</a></p>
      </div>
    </div>
  `;

  // Setup password toggle
  const togglePassword = container.querySelector('#togglePassword');
  const passwordInput = container.querySelector('#password');

  togglePassword.addEventListener('click', () => {
    const type = passwordInput.type === 'password' ? 'text' : 'password';
    passwordInput.type = type;
    togglePassword.querySelector('.toggle-icon').textContent = type === 'password' ? '👁️' : '🙈';
  });

  // Handle form submission
  const form = container.querySelector('#loginForm');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = form.email.value;
    const password = form.password.value;
    const errorMsg = container.querySelector('#errorMessage');
    const btnText = container.querySelector('.btn-text');
    const btnLoader = container.querySelector('.btn-loader');

    try {
      // Show loading
      btnText.style.display = 'none';
      btnLoader.style.display = 'inline';
      errorMsg.style.display = 'none';

      // Call login API
      const response = await authAPI.login({ email, password });

      // Update store with user and token
      store.setAuth(response.user, response.token);

      // Dispatch login success event
      window.dispatchEvent(new CustomEvent('auth:login', { detail: response.user }));

    } catch (error) {
      // Show error
      errorMsg.textContent = error.message || 'Login failed. Please check your credentials.';
      errorMsg.style.display = 'block';

      // Reset button
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });

  // Handle register link
  const registerLink = container.querySelector('#showRegister');
  registerLink.addEventListener('click', (e) => {
    e.preventDefault();
    window.dispatchEvent(new Event('auth:showRegister'));
  });

  return container;
}

