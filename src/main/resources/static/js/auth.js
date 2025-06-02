/**
 * Authentication utilities for the admin panel
 */

// Store token in localStorage
const TOKEN_KEY = 'duolingo_admin_token';

const BASE_URL = 'localhost:8080';

// Check if user is authenticated
function isAuthenticated() {
    return localStorage.getItem(TOKEN_KEY) !== null;
}

// Get the authentication token
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// Set the authentication token
function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

// Remove the authentication token (logout)
function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}

// Add authorization header to fetch options
function getAuthHeaders() {
    return {
        'Authorization': `Bearer ${getToken()}`,
        'Content-Type': 'application/json'
    };
}

// Login function
async function login(username, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Login failed');
        }

        const data = await response.json();

        console.log(data)
        // Check if user is an admin
        if (!data.authorities || !data.authorities.includes('ADMIN')) {
            throw new Error('You do not have admin privileges');
        }
        
        setToken(data.token);
        return data;
    } catch (error) {
        console.error('Login error:', error);
        throw error;
    }
}

// Logout function
function logout() {
    removeToken();
    window.location.href = '/login.html';
}

// Check if token is expired or invalid
async function validateToken() {
    if (!isAuthenticated()) {
        return false;
    }

    try {
        const response = await fetch('/api/admin/validate', {
            method: 'GET',
            headers: getAuthHeaders()
        });

        return response.ok;
    } catch (error) {
        console.error('Token validation error:', error);
        return false;
    }
}

// Redirect to login page if not authenticated
async function requireAuth() {
    const isValid = await validateToken();
    
    if (!isValid) {
        removeToken();
        window.location.href = '/login.html';
        return false;
    }
    
    return true;
}

// Initialize authentication check
document.addEventListener('DOMContentLoaded', async () => {
    // If we're not on the login page, check authentication
    if (!window.location.pathname.includes('login.html')) {
        const isValid = await requireAuth();
        if (!isValid) {
            return;
        }
    }
    
    // Setup logout button if it exists
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            logout();
        });
    }
});