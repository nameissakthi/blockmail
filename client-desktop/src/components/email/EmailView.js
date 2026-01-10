/**
 * Email View Component
 * Displays a single email with decryption
 */

import emailAPI from '../../api/email.js';
import kmAPI from '../../api/km.js';
import encryptionManager from '../../services/encryption/encryptionManager.js';
import store from '../../store/store.js';

export function createEmailViewComponent(emailId) {
  const container = document.createElement('div');
  container.className = 'email-view-container';
  container.innerHTML = '<div class="loading">Loading email...</div>';

  const loadEmail = async () => {
    try {
      // Get email - backend should return decrypted email
      const response = await emailAPI.getEmail(emailId);

      // Handle different response formats
      const email = response.email || response.data || response;

      if (!email || !email.id) {
        throw new Error('Invalid email data received from server');
      }

      // The body might already be decrypted by backend or might be encrypted
      let decryptedBody = email.body || email.content || email.message || '';

      // If body is still encrypted (has ciphertext property), try to decrypt client-side
      if (typeof decryptedBody === 'object' && decryptedBody.ciphertext) {
        try {
          // Get decryption key
          let decryptionKey = null;
          if (email.securityLevel === 1 || email.securityLevel === 2) {
            // Get quantum key from QKD
            const keyResponse = await qkdAPI.obtainKeys({
              numberOfKeys: 1,
              keySize: 256
            });
            decryptionKey = keyResponse.keys?.[0] || keyResponse.key;
          } else if (email.securityLevel === 4) {
            // Prompt for password
            decryptionKey = prompt('Enter decryption password:');
          }

          if (decryptionKey) {
            decryptedBody = await encryptionManager.decrypt(decryptedBody, decryptionKey);
          } else {
            decryptedBody = '[Unable to decrypt - key not available]';
          }
        } catch (error) {
          console.error('Decryption error:', error);
          decryptedBody = `[Decryption failed: ${error.message}]`;
        }
      }

      // Render email
      container.innerHTML = `
        <div class="email-view-header">
          <button class="btn btn-back" id="backBtn">← Back</button>
          <div class="email-actions">
            <button class="btn btn-icon" id="replyBtn" title="Reply">↩️</button>
            <button class="btn btn-icon" id="deleteBtn" title="Delete">🗑️</button>
          </div>
        </div>
        
        <div class="email-view-content">
          <div class="email-subject">
            <h2>${email.subject || 'No Subject'}</h2>
            ${getSecurityBadge(email.securityLevel || 4)}
          </div>
          
          <div class="email-meta">
            <div class="email-from">
              <strong>From:</strong> ${email.senderEmail || email.from || email.sender || 'Unknown'}
            </div>
            <div class="email-to">
              <strong>To:</strong> ${email.recipientEmail || email.to || email.recipient || 'Unknown'}
            </div>
            <div class="email-date">
              <strong>Date:</strong> ${email.sentAt || email.date || email.timestamp || email.createdAt ? new Date(email.sentAt || email.date || email.timestamp || email.createdAt).toLocaleString() : 'Unknown'}
            </div>
          </div>
          
          <div class="email-body">
            ${(decryptedBody || 'No content').toString().replace(/\n/g, '<br>')}
          </div>
          
          ${email.attachment ? `
            <div class="email-attachment">
              <strong>📎 Attachment:</strong>
              <a href="${email.attachment.url}" download="${email.attachment.name}">
                ${email.attachment.name}
              </a>
            </div>
          ` : ''}
        </div>
      `;

      // Add event listeners
      const backBtn = container.querySelector('#backBtn');
      backBtn.addEventListener('click', () => {
        window.dispatchEvent(new Event('email:close'));
      });

      const replyBtn = container.querySelector('#replyBtn');
      replyBtn.addEventListener('click', () => {
        window.dispatchEvent(new CustomEvent('email:reply', { detail: email }));
      });

      const deleteBtn = container.querySelector('#deleteBtn');
      deleteBtn.addEventListener('click', async () => {
        if (confirm('Are you sure you want to delete this email?')) {
          try {
            await emailAPI.deleteEmail(emailId);
            window.dispatchEvent(new Event('email:deleted'));
            window.dispatchEvent(new Event('email:close'));
          } catch (error) {
            console.error('Failed to delete email:', error);
            alert(`Failed to delete email: ${error.message || 'Unknown error'}`);
          }
        }
      });

    } catch (error) {
      console.error('Error loading email:', error);
      container.innerHTML = `
        <div class="error-state">
          <h3>⚠️ Failed to Load Email</h3>
          <p><strong>Error:</strong> ${error.message}</p>
          <p><strong>Email ID:</strong> ${emailId}</p>
          <p class="error-hint">This might be a backend issue. Please check:</p>
          <ul class="error-hints">
            <li>Backend is running and accessible</li>
            <li>The decrypt endpoint is working correctly</li>
            <li>The email exists in the database</li>
            <li>Check backend logs for more details</li>
          </ul>
          <button class="btn btn-primary" id="backBtn">← Back to Inbox</button>
        </div>
      `;

      const backBtn = container.querySelector('#backBtn');
      backBtn.addEventListener('click', () => {
        window.dispatchEvent(new Event('email:close'));
      });
    }
  };

  loadEmail();

  return container;
}

function getSecurityBadge(level) {
  const badges = {
    1: '<span class="badge badge-success">🔐 Quantum Secure</span>',
    2: '<span class="badge badge-info">🛡️ Quantum-AES</span>',
    3: '<span class="badge badge-warning">🔒 PQC</span>',
    4: '<span class="badge badge-secondary">🔓 Standard</span>'
  };
  return badges[level] || badges[4];
}

