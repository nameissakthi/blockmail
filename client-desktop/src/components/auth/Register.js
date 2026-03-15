/**
 * Register Component with OTP Verification
 */

import authAPI from '../../api/auth.js';
import apiClient from '../../api/client.js';
import store from '../../store/store.js';

export function createRegisterComponent() {
  const container = document.createElement('div');
  container.className = 'auth-container';
  
  let registrationStep = 1; // 1: Email & Password, 2: OTP Verification
  let registrationData = {};

  const renderStep1 = () => {
    container.innerHTML = `
      <div class="auth-card">
        <div class="auth-header">
          <h1>🔐 BlockMail</h1>
          <p>Create Your Quantum Secure Account</p>
        </div>
        
        <form id="registerForm" class="auth-form">
          <div class="form-group">
            <label for="email">Email Address</label>
            <input 
              type="email" 
              id="email" 
              name="email" 
              required 
              placeholder="your.email@gmail.com"
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
                minlength="6"
                placeholder="At least 6 characters"
              />
              <button type="button" class="password-toggle" id="togglePassword">
                <span class="toggle-icon">👁️</span>
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <div class="password-input-wrapper">
              <input 
                type="password" 
                id="confirmPassword" 
                name="confirmPassword" 
                required 
                placeholder="Re-enter password"
              />
              <button type="button" class="password-toggle" id="toggleConfirmPassword">
                <span class="toggle-icon">👁️</span>
              </button>
            </div>
          </div>
          
          <div id="errorMessage" class="error-message" style="display: none;"></div>
          
          <button type="submit" class="btn btn-primary">
            <span class="btn-text">Send Verification Code</span>
            <span class="btn-loader" style="display: none;">⏳</span>
          </button>
        </form>
        
        <div class="auth-footer">
          <p>Already have an account? <a href="#" id="showLogin">Sign in here</a></p>
        </div>
      </div>
    `;

    setupPasswordToggles();
    setupStep1Form();
    setupLoginLink();
  };

  const renderStep2 = () => {
    container.innerHTML = `
      <div class="auth-card">
        <div class="auth-header">
          <h1>🔐 BlockMail</h1>
          <p>Verify Your Email</p>
        </div>
        
        <div class="otp-info">
          <p>We've sent a 6-digit verification code to:</p>
          <strong>${registrationData.email}</strong>
          <p class="otp-hint">Please check your inbox (and spam folder)</p>
        </div>
        
        <form id="otpForm" class="auth-form">
          <div class="form-group">
            <label for="otpCode">Enter 6-Digit Code</label>
            <input 
              type="text" 
              id="otpCode" 
              name="otpCode" 
              required 
              maxlength="6"
              pattern="[0-9]{6}"
              placeholder="000000"
              class="otp-input"
              autocomplete="off"
            />
          </div>
          
          <div id="errorMessage" class="error-message" style="display: none;"></div>
          
          <button type="submit" class="btn btn-primary">
            <span class="btn-text">Verify & Create Account</span>
            <span class="btn-loader" style="display: none;">⏳</span>
          </button>
          
          <button type="button" class="btn btn-secondary" id="resendOtp" style="margin-top: 10px;">
            Resend Code
          </button>
          
          <button type="button" class="btn btn-text" id="backBtn" style="margin-top: 10px;">
            ← Change Email
          </button>
        </form>
      </div>
    `;

    setupStep2Form();
  };

  const setupPasswordToggles = () => {
    const togglePassword = container.querySelector('#togglePassword');
    const toggleConfirmPassword = container.querySelector('#toggleConfirmPassword');
    const passwordInput = container.querySelector('#password');
    const confirmPasswordInput = container.querySelector('#confirmPassword');

    togglePassword?.addEventListener('click', () => {
      const type = passwordInput.type === 'password' ? 'text' : 'password';
      passwordInput.type = type;
      togglePassword.querySelector('.toggle-icon').textContent = type === 'password' ? '👁️' : '🙈';
    });

    toggleConfirmPassword?.addEventListener('click', () => {
      const type = confirmPasswordInput.type === 'password' ? 'text' : 'password';
      confirmPasswordInput.type = type;
      toggleConfirmPassword.querySelector('.toggle-icon').textContent = type === 'password' ? '👁️' : '🙈';
    });
  };

  const setupStep1Form = () => {
    const form = container.querySelector('#registerForm');
    const errorMsg = container.querySelector('#errorMessage');
    const btnText = container.querySelector('.btn-text');
    const btnLoader = container.querySelector('.btn-loader');

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const email = form.email.value.trim();
      const password = form.password.value;
      const confirmPassword = form.confirmPassword.value;

      // Validate passwords match
      if (password !== confirmPassword) {
        errorMsg.textContent = 'Passwords do not match';
        errorMsg.style.display = 'block';
        return;
      }

      // Validate email domain (basic check)
      if (!email.includes('@') || !email.includes('.')) {
        errorMsg.textContent = 'Please enter a valid email address';
        errorMsg.style.display = 'block';
        return;
      }

      try {
        btnText.style.display = 'none';
        btnLoader.style.display = 'inline';
        errorMsg.style.display = 'none';

        // Send OTP
        const response = await apiClient.post('/api/otp/generate', { email });

        if (!response.success) {
          throw new Error(response.message || 'Failed to send verification code');
        }

        // Store registration data
        registrationData = { email, password };
        
        // Move to step 2
        registrationStep = 2;
        renderStep2();

      } catch (error) {
        errorMsg.textContent = error.message || 'Failed to send verification code. Please check your email and try again.';
        errorMsg.style.display = 'block';
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
      }
    });
  };

  const setupStep2Form = () => {
    const form = container.querySelector('#otpForm');
    const errorMsg = container.querySelector('#errorMessage');
    const btnText = container.querySelector('.btn-text');
    const btnLoader = container.querySelector('.btn-loader');
    const resendBtn = container.querySelector('#resendOtp');
    const backBtn = container.querySelector('#backBtn');
    const otpInput = container.querySelector('#otpCode');

    // Auto-focus OTP input
    otpInput.focus();

    // Format OTP input (only numbers)
    otpInput.addEventListener('input', (e) => {
      e.target.value = e.target.value.replace(/[^0-9]/g, '');
    });

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const otpCode = otpInput.value.trim();

      if (otpCode.length !== 6) {
        errorMsg.textContent = 'Please enter a 6-digit code';
        errorMsg.style.display = 'block';
        return;
      }

      try {
        btnText.style.display = 'none';
        btnLoader.style.display = 'inline';
        errorMsg.style.display = 'none';

        // Verify OTP
        const verifyResponse = await apiClient.post('/api/otp/verify', {
          email: registrationData.email,
          otpCode: otpCode
        });

        if (!verifyResponse.success || !verifyResponse.verified) {
          throw new Error(verifyResponse.message || 'Invalid verification code');
        }

        // Register user
        const registerResponse = await authAPI.register({
          email: registrationData.email,
          password: registrationData.password
        });

        if (registerResponse.requiresLogin) {
          errorMsg.textContent = registerResponse.message;
          errorMsg.style.display = 'block';
          btnText.style.display = 'inline';
          btnLoader.style.display = 'none';

          setTimeout(() => {
            window.dispatchEvent(new Event('auth:showLogin'));
          }, 1200);

          return;
        }

        // Update store
        store.setAuth(registerResponse.user, registerResponse.token);

        // Dispatch login success event
        window.dispatchEvent(new CustomEvent('auth:login', { detail: registerResponse.user }));

      } catch (error) {
        errorMsg.textContent = error.message || 'Verification failed. Please try again.';
        errorMsg.style.display = 'block';
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
      }
    });

    resendBtn.addEventListener('click', async () => {
      try {
        resendBtn.disabled = true;
        resendBtn.textContent = 'Sending...';
        errorMsg.style.display = 'none';

        await apiClient.post('/api/otp/generate', { email: registrationData.email });

        resendBtn.textContent = '✓ Code Resent';
        setTimeout(() => {
          resendBtn.textContent = 'Resend Code';
          resendBtn.disabled = false;
        }, 3000);

      } catch (error) {
        errorMsg.textContent = 'Failed to resend code. Please try again.';
        errorMsg.style.display = 'block';
        resendBtn.textContent = 'Resend Code';
        resendBtn.disabled = false;
      }
    });

    backBtn.addEventListener('click', () => {
      registrationStep = 1;
      renderStep1();
    });
  };

  const setupLoginLink = () => {
    const loginLink = container.querySelector('#showLogin');
    loginLink?.addEventListener('click', (e) => {
      e.preventDefault();
      window.dispatchEvent(new Event('auth:showLogin'));
    });
  };

  // Initial render
  renderStep1();

  return container;
}

