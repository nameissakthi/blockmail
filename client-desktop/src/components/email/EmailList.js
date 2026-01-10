/**
 * Email List Component
 * Displays inbox or sent emails
 */

import emailAPI from '../../api/email.js';
import store from '../../store/store.js';

export function createEmailListComponent(type = 'inbox') {
  const container = document.createElement('div');
  container.className = 'email-list-container';

  const render = () => {
    const state = store.getState();
    const emails = state.emails[type] || [];

    container.innerHTML = `
      <div class="email-list-header">
        <h2>${type === 'inbox' ? '📥 Inbox' : '📤 Sent'}</h2>
        <button class="btn btn-refresh" id="refreshBtn">🔄 Refresh</button>
      </div>
      
      <div class="email-list">
        ${emails.length === 0 ? `
          <div class="empty-state">
            <p>No emails ${type === 'inbox' ? 'received' : 'sent'} yet</p>
          </div>
        ` : emails.map(email => `
          <div class="email-item ${email.read ? '' : 'unread'}" data-email-id="${email.id || email._id || email.emailId}">
            <div class="email-item-header">
              <span class="email-from">${type === 'inbox' ? (email.senderEmail || email.from || email.sender || 'Unknown Sender') : (email.recipientEmail || email.to || email.recipient || 'Unknown Recipient')}</span>
              <span class="email-date">${formatDate(email.sentAt || email.date || email.timestamp || email.createdAt)}</span>
            </div>
            <div class="email-subject">${email.subject || 'No Subject'}</div>
            <div class="email-preview">${email.preview || (email.content && typeof email.content === 'string' ? email.content.substring(0, 100) : '') || (email.body && typeof email.body === 'string' ? email.body.substring(0, 100) : '') || 'No preview available'}</div>
            <div class="email-security">
              ${getSecurityBadge(email.securityLevel || 4)}
            </div>
          </div>
        `).join('')}
      </div>
    `;

    // Add event listeners
    const refreshBtn = container.querySelector('#refreshBtn');
    if (refreshBtn) {
      refreshBtn.addEventListener('click', loadEmails);
    }

    const emailItems = container.querySelectorAll('.email-item');
    emailItems.forEach(item => {
      item.addEventListener('click', () => {
        const emailId = item.dataset.emailId;
        openEmail(emailId);
      });
    });
  };

  const loadEmails = async () => {
    try {
      store.setLoading(true);
      const response = type === 'inbox'
        ? await emailAPI.getInbox()
        : await emailAPI.getSent();

      // Handle different response formats
      const emailsData = response.emails || response.data || response || [];
      store.setEmails(type, Array.isArray(emailsData) ? emailsData : []);
      store.setLoading(false);
    } catch (error) {
      console.error('Failed to load emails:', error);
      store.setError(error.message);
      store.setLoading(false);
      // Set empty array on error so UI doesn't break
      store.setEmails(type, []);
    }
  };

  const openEmail = (emailId) => {
    window.dispatchEvent(new CustomEvent('email:open', { detail: emailId }));
  };

  // Subscribe to store changes
  store.subscribe(render);

  // Initial render
  render();

  // Load emails
  loadEmails();

  return container;
}

function formatDate(dateString) {
  if (!dateString) return 'Unknown';

  try {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;

    // Less than 24 hours
    if (diff < 86400000) {
      return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    }

    // Less than 7 days
    if (diff < 604800000) {
      return date.toLocaleDateString('en-US', { weekday: 'short' });
    }

    // Older
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  } catch (error) {
    return 'Invalid date';
  }
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

