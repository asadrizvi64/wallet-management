import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  Grid,
  Avatar,
  Divider,
  Alert,
  CircularProgress,
  Chip,
  IconButton
} from '@mui/material';
import {
  Person,
  Edit,
  Save,
  Cancel,
  AccountBalanceWallet,
  Email,
  Phone,
  Badge,
  CreditCard,
  ArrowBack
} from '@mui/icons-material';
import axios from 'axios';

const EditProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: ''
  });

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      setLoading(true);
      setError('');
      const savedUser = JSON.parse(localStorage.getItem('user'));

      if (!savedUser || !savedUser.userId) {
        setError('User not found. Please login again.');
        return;
      }

      const response = await axios.get(
        `http://localhost:8080/api/v1/users/${savedUser.userId}`
      );

      setUser(response.data);
      setFormData({
        fullName: response.data.fullName || '',
        phoneNumber: response.data.phoneNumber || ''
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setEditing(true);
    setError('');
    setSuccess('');
  };

  const handleCancel = () => {
    setEditing(false);
    setFormData({
      fullName: user.fullName || '',
      phoneNumber: user.phoneNumber || ''
    });
    setError('');
    setSuccess('');
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      setError('');
      setSuccess('');

      if (!formData.fullName.trim()) {
        setError('Full name is required');
        setSaving(false);
        return;
      }

      const savedUser = JSON.parse(localStorage.getItem('user'));

      const response = await axios.put(
        `http://localhost:8080/api/v1/users/${savedUser.userId}`,
        formData
      );

      setUser(response.data);

      // Update localStorage with new user data
      const updatedUser = {
        ...savedUser,
        fullName: response.data.fullName
      };
      localStorage.setItem('user', JSON.stringify(updatedUser));

      setSuccess('Profile updated successfully!');
      setEditing(false);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const getKycStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'VERIFIED':
        return 'success';
      case 'REJECTED':
        return 'error';
      default:
        return 'warning';
    }
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (!user) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">{error || 'Failed to load profile'}</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 2 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/dashboard')}
          sx={{ mb: 1 }}
        >
          Back to Dashboard
        </Button>
      </Box>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <Avatar sx={{ bgcolor: 'primary.main', width: 64, height: 64, mr: 2 }}>
            <Person sx={{ fontSize: 40 }} />
          </Avatar>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" gutterBottom>
              Profile
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Manage your personal information
            </Typography>
          </Box>
          {!editing && (
            <Button
              variant="contained"
              startIcon={<Edit />}
              onClick={handleEdit}
            >
              Edit Profile
            </Button>
          )}
        </Box>

        <Divider sx={{ mb: 3 }} />

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
            {success}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* Editable Fields */}
          <Grid item xs={12}>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
              <Person sx={{ mr: 1 }} /> Personal Information
            </Typography>
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Full Name"
              name="fullName"
              value={editing ? formData.fullName : user.fullName}
              onChange={handleChange}
              disabled={!editing}
              required
              InputProps={{
                readOnly: !editing,
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Phone Number"
              name="phoneNumber"
              value={editing ? formData.phoneNumber : (user.phoneNumber || '')}
              onChange={handleChange}
              disabled={!editing}
              InputProps={{
                readOnly: !editing,
                startAdornment: <Phone sx={{ mr: 1, color: 'action.active' }} />
              }}
              placeholder="+92-XXX-XXXXXXX"
            />
          </Grid>

          {/* Read-only Fields */}
          <Grid item xs={12} sx={{ mt: 2 }}>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
              <Email sx={{ mr: 1 }} /> Account Information
            </Typography>
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Email"
              value={user.email || ''}
              disabled
              InputProps={{
                readOnly: true,
                startAdornment: <Email sx={{ mr: 1, color: 'action.active' }} />
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="CNIC Number"
              value={user.cnicNumber || 'Not provided'}
              disabled
              InputProps={{
                readOnly: true,
                startAdornment: <Badge sx={{ mr: 1, color: 'action.active' }} />
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography variant="body1" sx={{ mr: 2 }}>
                KYC Status:
              </Typography>
              <Chip
                label={user.kycStatus || 'PENDING'}
                color={getKycStatusColor(user.kycStatus)}
                size="small"
              />
            </Box>
          </Grid>

          {/* Wallet Information */}
          <Grid item xs={12} sx={{ mt: 2 }}>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
              <AccountBalanceWallet sx={{ mr: 1 }} /> Wallet Information
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Wallet Number"
              value={user.walletNumber || 'N/A'}
              disabled
              InputProps={{
                readOnly: true,
                startAdornment: <CreditCard sx={{ mr: 1, color: 'action.active' }} />
              }}
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Balance"
              value={user.balance ? `PKR ${parseFloat(user.balance).toLocaleString()}` : 'PKR 0.00'}
              disabled
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>

          {/* Action Buttons */}
          {editing && (
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                <Button
                  variant="outlined"
                  startIcon={<Cancel />}
                  onClick={handleCancel}
                  disabled={saving}
                >
                  Cancel
                </Button>
                <Button
                  variant="contained"
                  startIcon={saving ? <CircularProgress size={20} /> : <Save />}
                  onClick={handleSave}
                  disabled={saving}
                >
                  {saving ? 'Saving...' : 'Save Changes'}
                </Button>
              </Box>
            </Grid>
          )}
        </Grid>
      </Paper>
    </Container>
  );
};

export default EditProfile;
