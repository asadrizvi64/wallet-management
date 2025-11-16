import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme, CssBaseline, Box } from '@mui/material';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import EditProfile from './components/EditProfile';
import AdminPanel from './components/AdminPanel';
import AdminDashboard from './components/AdminDashboard';
import './App.css';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
});

function App() {
  const [user, setUser] = useState(null);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
    if (userData.token) {
      localStorage.setItem('token', userData.token);
    }
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  // Check if user has admin or superuser role
  const isAdmin = () => {
    return user && (user.userRole === 'ADMIN' || user.userRole === 'SUPERUSER');
  };

  const isSuperuser = () => {
    return user && user.userRole === 'SUPERUSER';
  };

  // Check if user is already logged in
  React.useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
          <Routes>
            <Route 
              path="/login" 
              element={
                user ? <Navigate to="/dashboard" /> : <Login onLogin={handleLogin} />
              } 
            />
            <Route 
              path="/register" 
              element={
                user ? <Navigate to="/dashboard" /> : <Register onRegister={handleLogin} />
              } 
            />
            <Route
              path="/dashboard"
              element={
                user ? <Dashboard user={user} onLogout={handleLogout} /> : <Navigate to="/login" />
              }
            />
            <Route
              path="/profile"
              element={
                user ? <EditProfile /> : <Navigate to="/login" />
              }
            />
            <Route
              path="/admin"
              element={
                user ? (isAdmin() ? <AdminPanel /> : <Navigate to="/dashboard" />) : <Navigate to="/login" />
              }
            />
            <Route
              path="/admin/dashboard"
              element={
                user ? (isSuperuser() ? <AdminDashboard /> : <Navigate to="/dashboard" />) : <Navigate to="/login" />
              }
            />
            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;
