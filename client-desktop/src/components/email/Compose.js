/**
 * Compose Email Component
 */

import emailAPI from '../../api/email.js';
import kmAPI from '../../api/km.js';
import qkdAPI from '../../api/km.js'; // Add qkdAPI as alias for kmAPI
import encryptionManager from '../../services/encryption/encryptionManager.js';
import store from '../../store/store.js';
import { SECURITY_LEVELS } from '../../services/encryption/securityLevels.js';

export function createComposeComponent() {
  const container = document.createElement('div');
  container.className = 'compose-container';

  const state = store.getState();
  const securityLevels = Object.values(SECURITY_LEVELS);

  container.innerHTML = `
    <div class="compose-header">
      <h2>✉️ Compose Email</h2>
      <button class="btn btn-close" id="closeBtn">✕</button>
    </div>
    
    <form id="composeForm" class="compose-form">
      <div class="form-group">
        <label for="to">To:</label>
        <input 
          type="email" 
          id="to" 
          name="to" 
          required 
          placeholder="recipient@example.com"
        />
      </div>
      
      <div class="form-group">
        <label for="subject">Subject:</label>
        <input 
          type="text" 
          id="subject" 
          name="subject" 
          required 
          placeholder="Email subject"
        />
      </div>
      
      <div class="form-group">
        <label for="securityLevel">Security Level:</label>
        <select id="securityLevel" name="securityLevel">
          ${securityLevels.map(level => `
            <option value="${level.id}" ${level.id === state.securityLevel ? 'selected' : ''}>
              ${level.icon} ${level.name} - ${level.description}
            </option>
          `).join('')}
        </select>
      </div>
      
      <div class="form-group">
        <label for="body">Message:</label>
        <textarea 
          id="body" 
          name="body" 
          rows="10" 
          required 
          placeholder="Type your message here..."
        ></textarea>
      </div>
      
      <div class="form-group">
        <label for="attachment">Attachment: (Optional)</label>
        <input 
          type="file" 
          id="attachment" 
          name="attachment"
        />
      </div>
      
      <div id="errorMessage" class="error-message" style="display: none;"></div>
      <div id="successMessage" class="success-message" style="display: none;"></div>
      
      <div class="compose-actions">
        <button type="submit" class="btn btn-primary">
          <span class="btn-text">📧 Send Email</span>
          <span class="btn-loader" style="display: none;">⏳</span>
        </button>
        <button type="button" class="btn btn-secondary" id="cancelBtn">Cancel</button>
      </div>
    </form>
  `;

  const form = container.querySelector('#composeForm');
  const errorMsg = container.querySelector('#errorMessage');
  const successMsg = container.querySelector('#successMessage');
  const btnText = container.querySelector('.btn-text');
  const btnLoader = container.querySelector('.btn-loader');

  // Handle form submission
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const to = form.to.value;
    const subject = form.subject.value;
    const body = form.body.value;
    const securityLevel = parseInt(form.securityLevel.value);

    try {
      // Show loading
      btnText.style.display = 'none';
      btnLoader.style.display = 'inline';
      errorMsg.style.display = 'none';
      successMsg.style.display = 'none';

      // Backend handles all encryption with quantum keys
      // Frontend just sends plaintext content

      // Handle attachment if present
      let attachmentData = null;
      const attachmentFile = form.attachment.files[0];
      if (attachmentFile) {
        attachmentData = await readFileAsBase64(attachmentFile);
      }

      // Map security level number to backend enum
      const securityLevelMap = {
        1: 'QUANTUM_SECURE_OTP',
        2: 'QUANTUM_AIDED_AES',
        3: 'POST_QUANTUM_CRYPTO',
        4: 'STANDARD_ENCRYPTION'
      };

      // Send email with plaintext content - backend will encrypt it
      const emailData = {
        recipientEmail: to,  // Backend expects 'recipientEmail'
        subject: subject,
        content: body,  // Send plaintext - backend will encrypt
        securityLevel: securityLevelMap[securityLevel],  // Backend expects enum string
        attachments: attachmentData ? [{
          filename: attachmentFile.name,
          data: attachmentData,
          contentType: attachmentFile.type
        }] : []
      };

      await emailAPI.sendEmail(emailData);

      // Show success
      successMsg.textContent = 'Email sent successfully! ✓';
      successMsg.style.display = 'block';

      // Reset form
      setTimeout(() => {
        form.reset();
        window.dispatchEvent(new Event('email:sent'));
      }, 1500);

    } catch (error) {
      errorMsg.textContent = error.message || 'Failed to send email';
      errorMsg.style.display = 'block';
    } finally {
      btnText.style.display = 'inline';
      btnLoader.style.display = 'none';
    }
  });

  // Handle close and cancel buttons
  const closeBtn = container.querySelector('#closeBtn');
  const cancelBtn = container.querySelector('#cancelBtn');

  [closeBtn, cancelBtn].forEach(btn => {
    btn.addEventListener('click', () => {
      window.dispatchEvent(new Event('compose:close'));
    });
  });

  return container;
}

async function readFileAsBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

