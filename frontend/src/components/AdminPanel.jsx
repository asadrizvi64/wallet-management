import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  Alert,
  CircularProgress,
  InputAdornment,
  Grid,
  Avatar,
  Switch,
  FormControlLabel,
  Divider
} from '@mui/material';
import {
  Edit,
  Close,
  Search,
  AdminPanelSettings,
  ArrowBack,
  Person,
  Email,
  Phone,
  Badge,
  AccountBalanceWallet,
  CalendarToday,
  CheckCircle,
  Cancel as CancelIcon
} from '@mui/icons-material';
import axios from '../axiosConfig';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  // Edit dialog state
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [editFormData, setEditFormData] = useState({
    kycStatus: '',
    userRole: '',
    isActive: true
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchAllUsers();
  }, []);

  useEffect(() => {
    // Filter users based on search query
    if (searchQuery.trim() === '') {
      setFilteredUsers(users);
    } else {
      const query = searchQuery.toLowerCase();
      const filtered = users.filter(user =>
        user.username.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query) ||
        user.fullName.toLowerCase().includes(query) ||
        (user.walletNumber && user.walletNumber.toLowerCase().includes(query))
      );
      setFilteredUsers(filtered);
    }
  }, [searchQuery, users]);

  const fetchAllUsers = async () => {
    try {
      setLoading(true);
      setError('');

      const response = await axios.get('http://localhost:8080/api/v1/users');

      if (response.data && response.data.data) {
        setUsers(response.data.data);
        setFilteredUsers(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (user) => {
    setSelectedUser(user);
    setEditFormData({
      kycStatus: user.kycStatus,
      userRole: user.userRole,
      isActive: user.isActive
    });
    setEditDialogOpen(true);
    setError('');
    setSuccess('');
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setSelectedUser(null);
    setEditFormData({
      kycStatus: '',
      userRole: '',
      isActive: true
    });
  };

  const handleEditChange = (e) => {
    const { name, value, checked } = e.target;
    setEditFormData({
      ...editFormData,
      [name]: name === 'isActive' ? checked : value
    });
  };

  const handleSaveUser = async () => {
    try {
      setSaving(true);
      setError('');
      setSuccess('');

      await axios.put(
        `http://localhost:8080/api/v1/users/${selectedUser.userId}/admin`,
        editFormData
      );

      setSuccess('User updated successfully!');
      handleEditClose();
      fetchAllUsers(); // Refresh the user list
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update user');
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

  const getRoleColor = (role) => {
    return role === 'ADMIN' ? 'secondary' : 'primary';
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  };

  if (loading) {
    return (
      <Container maxWidth="xl" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
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
          <Avatar sx={{ bgcolor: 'secondary.main', width: 64, height: 64, mr: 2 }}>
            <AdminPanelSettings sx={{ fontSize: 40 }} />
          </Avatar>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" gutterBottom>
              Admin Panel
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Manage users, KYC verification, and account settings
            </Typography>
          </Box>
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

        {/* Search Bar */}
        <Box sx={{ mb: 3 }}>
          <TextField
            fullWidth
            placeholder="Search by username, email, name, or wallet number..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
          />
        </Box>

        {/* Users Table */}
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Username</TableCell>
                <TableCell>Full Name</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Phone</TableCell>
                <TableCell>Wallet Number</TableCell>
                <TableCell>Balance</TableCell>
                <TableCell>KYC Status</TableCell>
                <TableCell>Role</TableCell>
                <TableCell>Active</TableCell>
                <TableCell>Created</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredUsers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={12} align="center">
                    <Typography variant="body2" color="text.secondary">
                      No users found
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                filteredUsers.map((user) => (
                  <TableRow key={user.userId} hover>
                    <TableCell>{user.userId}</TableCell>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>{user.fullName}</TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>{user.phoneNumber || 'N/A'}</TableCell>
                    <TableCell>{user.walletNumber}</TableCell>
                    <TableCell>
                      PKR {parseFloat(user.balance || 0).toLocaleString()}
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={user.kycStatus}
                        color={getKycStatusColor(user.kycStatus)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={user.userRole}
                        color={getRoleColor(user.userRole)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {user.isActive ? (
                        <CheckCircle color="success" />
                      ) : (
                        <CancelIcon color="error" />
                      )}
                    </TableCell>
                    <TableCell sx={{ fontSize: '0.75rem' }}>
                      {formatDate(user.createdAt)}
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        color="primary"
                        onClick={() => handleEditClick(user)}
                        size="small"
                      >
                        <Edit />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <Box sx={{ mt: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Total Users: {filteredUsers.length} {searchQuery && `of ${users.length}`}
          </Typography>
        </Box>
      </Paper>

      {/* Edit User Dialog */}
      <Dialog
        open={editDialogOpen}
        onClose={handleEditClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Typography variant="h6">Edit User</Typography>
            <IconButton onClick={handleEditClose}>
              <Close />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent dividers>
          {selectedUser && (
            <Grid container spacing={3}>
              {/* User Info (Read-only) */}
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="primary" gutterBottom>
                  User Information (Read-only)
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="User ID"
                  value={selectedUser.userId}
                  disabled
                  InputProps={{
                    startAdornment: <Person sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Username"
                  value={selectedUser.username}
                  disabled
                  InputProps={{
                    startAdornment: <Badge sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Email"
                  value={selectedUser.email}
                  disabled
                  InputProps={{
                    startAdornment: <Email sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Full Name"
                  value={selectedUser.fullName}
                  disabled
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone Number"
                  value={selectedUser.phoneNumber || 'N/A'}
                  disabled
                  InputProps={{
                    startAdornment: <Phone sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="CNIC"
                  value={selectedUser.cnicNumber || 'N/A'}
                  disabled
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Wallet Number"
                  value={selectedUser.walletNumber}
                  disabled
                  InputProps={{
                    startAdornment: <AccountBalanceWallet sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Balance"
                  value={`PKR ${parseFloat(selectedUser.balance || 0).toLocaleString()}`}
                  disabled
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Created At"
                  value={formatDate(selectedUser.createdAt)}
                  disabled
                  InputProps={{
                    startAdornment: <CalendarToday sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Updated At"
                  value={formatDate(selectedUser.updatedAt)}
                  disabled
                  InputProps={{
                    startAdornment: <CalendarToday sx={{ mr: 1, color: 'action.active' }} />
                  }}
                />
              </Grid>

              {/* Editable Admin Fields */}
              <Grid item xs={12} sx={{ mt: 2 }}>
                <Divider />
                <Typography variant="subtitle2" color="secondary" gutterBottom sx={{ mt: 2 }}>
                  Admin Controls (Editable)
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>KYC Status</InputLabel>
                  <Select
                    name="kycStatus"
                    value={editFormData.kycStatus}
                    onChange={handleEditChange}
                    label="KYC Status"
                  >
                    <MenuItem value="PENDING">PENDING</MenuItem>
                    <MenuItem value="VERIFIED">VERIFIED</MenuItem>
                    <MenuItem value="REJECTED">REJECTED</MenuItem>
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>User Role</InputLabel>
                  <Select
                    name="userRole"
                    value={editFormData.userRole}
                    onChange={handleEditChange}
                    label="User Role"
                  >
                    <MenuItem value="USER">USER</MenuItem>
                    <MenuItem value="ADMIN">ADMIN</MenuItem>
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      name="isActive"
                      checked={editFormData.isActive}
                      onChange={handleEditChange}
                      color="primary"
                    />
                  }
                  label={`Account Status: ${editFormData.isActive ? 'Active' : 'Inactive'}`}
                />
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose} disabled={saving}>
            Cancel
          </Button>
          <Button
            onClick={handleSaveUser}
            variant="contained"
            disabled={saving}
            startIcon={saving ? <CircularProgress size={20} /> : null}
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminPanel;
