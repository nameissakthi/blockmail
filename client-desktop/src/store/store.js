/**
 * Application State Store
 * Simple state management for the application
 */

class Store {
  constructor() {
    this.state = {
      user: null,
      isAuthenticated: false,
      emails: {
        inbox: [],
        sent: []
      },
      currentEmail: null,
      kmStatus: null,
      quantumKeys: [],
      securityLevel: 2, // Default to Quantum-aided AES
      loading: false,
      error: null
    };

    this.listeners = new Set();
  }

  /**
   * Get current state
   */
  getState() {
    return { ...this.state };
  }

  /**
   * Update state
   */
  setState(updates) {
    this.state = { ...this.state, ...updates };
    this.notifyListeners();
  }

  /**
   * Subscribe to state changes
   */
  subscribe(listener) {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  /**
   * Notify all listeners of state change
   */
  notifyListeners() {
    this.listeners.forEach(listener => listener(this.state));
  }

  /**
   * Set user authentication
   */
  setAuth(user, token) {
    this.setState({
      user,
      isAuthenticated: !!user,
      error: null
    });

    if (token) {
      localStorage.setItem('authToken', token);
    }

    if (user && user.email) {
      localStorage.setItem('userEmail', user.email);
    }
  }

  /**
   * Clear authentication
   */
  clearAuth() {
    this.setState({
      user: null,
      isAuthenticated: false
    });
    localStorage.removeItem('authToken');
    localStorage.removeItem('userEmail');
  }

  /**
   * Set emails
   */
  setEmails(type, emails) {
    const emailsState = { ...this.state.emails };
    emailsState[type] = emails;
    this.setState({ emails: emailsState });
  }

  /**
   * Add email
   */
  addEmail(type, email) {
    const emailsState = { ...this.state.emails };
    emailsState[type] = [email, ...emailsState[type]];
    this.setState({ emails: emailsState });
  }

  /**
   * Set current email
   */
  setCurrentEmail(email) {
    this.setState({ currentEmail: email });
  }

  /**
   * Set KM status
   */
  setKMStatus(status) {
    this.setState({ kmStatus: status });
  }

  /**
   * Add quantum key
   */
  addQuantumKey(key) {
    this.setState({
      quantumKeys: [...this.state.quantumKeys, key]
    });
  }

  /**
   * Remove quantum key
   */
  removeQuantumKey(keyId) {
    this.setState({
      quantumKeys: this.state.quantumKeys.filter(k => k.id !== keyId)
    });
  }

  /**
   * Set security level
   */
  setSecurityLevel(level) {
    this.setState({ securityLevel: level });
  }

  /**
   * Set loading state
   */
  setLoading(loading) {
    this.setState({ loading });
  }

  /**
   * Set error
   */
  setError(error) {
    this.setState({ error });
  }

  /**
   * Clear error
   */
  clearError() {
    this.setState({ error: null });
  }
}

export default new Store();

