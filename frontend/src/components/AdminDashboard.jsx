import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Tab,
  Tabs,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Button,
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
  CircularProgress
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  AccountBalanceWallet,
  People,
  Receipt,
  TrendingUp,
  Edit,
  Delete,
  Block,
  CheckCircle,
  Warning,
  Refresh,
  Payment
} from '@mui/icons-material';
import axios from 'axios';

function AdminDashboard() {
  const [currentTab, setCurrentTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Dashboard stats
  const [stats, setStats] = useState(null);

  // Wallets data
  const [wallets, setWallets] = useState([]);
  const [walletDialog, setWalletDialog] = useState(false);
  const [selectedWallet, setSelectedWallet] = useState(null);

  // Transactions data
  const [transactions, setTransactions] = useState([]);
  const [transactionFilter, setTransactionFilter] = useState({ status: '', type: '' });

  // Payment methods data
  const [paymentMethods, setPaymentMethods] = useState([]);

  const API_BASE_URL = 'http://localhost:8080/api/v1';

  // Get auth token
  const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return { headers: { Authorization: `Bearer ${token}` } };
  };

  // Load dashboard stats
  const loadDashboardStats = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/admin/dashboard`,
        getAuthHeader()
      );
      if (response.data.success) {
        setStats(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load dashboard stats');
    } finally {
      setLoading(false);
    }
  };

  // Load wallets
  const loadWallets = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/admin/wallets`,
        getAuthHeader()
      );
      if (response.data.success) {
        setWallets(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load wallets');
    } finally {
      setLoading(false);
    }
  };

  // Load transactions
  const loadTransactions = async () => {
    try {
      setLoading(true);
      const params = {};
      if (transactionFilter.status) params.status = transactionFilter.status;
      if (transactionFilter.type) params.type = transactionFilter.type;

      const response = await axios.get(
        `${API_BASE_URL}/admin/transactions`,
        { ...getAuthHeader(), params }
      );
      if (response.data.success) {
        setTransactions(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load transactions');
    } finally {
      setLoading(false);
    }
  };

  // Load payment methods
  const loadPaymentMethods = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/admin/payment-methods`,
        getAuthHeader()
      );
      if (response.data.success) {
        setPaymentMethods(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load payment methods');
    } finally {
      setLoading(false);
    }
  };

  // Update wallet status
  const updateWalletStatus = async (walletId, status) => {
    try {
      const response = await axios.put(
        `${API_BASE_URL}/admin/wallets/${walletId}`,
        { status },
        getAuthHeader()
      );
      if (response.data.success) {
        setSuccess('Wallet status updated successfully');
        loadWallets();
        setWalletDialog(false);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update wallet status');
    }
  };

  // Refund transaction
  const refundTransaction = async (transactionRef, reason) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/admin/transactions/${transactionRef}/refund`,
        { reason },
        getAuthHeader()
      );
      if (response.data.success) {
        setSuccess('Transaction refunded successfully');
        loadTransactions();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to refund transaction');
    }
  };

  // Delete payment method
  const deletePaymentMethod = async (paymentMethodId) => {
    if (!window.confirm('Are you sure you want to delete this payment method?')) {
      return;
    }

    try {
      const response = await axios.delete(
        `${API_BASE_URL}/admin/payment-methods/${paymentMethodId}`,
        getAuthHeader()
      );
      if (response.data.success) {
        setSuccess('Payment method deleted successfully');
        loadPaymentMethods();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete payment method');
    }
  };

  // Load data based on current tab
  useEffect(() => {
    switch (currentTab) {
      case 0:
        loadDashboardStats();
        break;
      case 1:
        loadWallets();
        break;
      case 2:
        loadTransactions();
        break;
      case 3:
        loadPaymentMethods();
        break;
      default:
        break;
    }
  }, [currentTab, transactionFilter]);

  // Handle tab change
  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
    setError('');
    setSuccess('');
  };

  // Get status chip color
  const getStatusColor = (status) => {
    const colors = {
      ACTIVE: 'success',
      COMPLETED: 'success',
      VERIFIED: 'success',
      INACTIVE: 'default',
      FROZEN: 'warning',
      BLOCKED: 'error',
      PENDING: 'warning',
      FAILED: 'error',
      CANCELLED: 'error'
    };
    return colors[status] || 'default';
  };

  // Format currency
  const formatCurrency = (amount, currency = 'PKR') => {
    return new Intl.NumberFormat('en-PK', {
      style: 'currency',
      currency: currency
    }).format(amount);
  };

  // Format date
  const formatDate = (date) => {
    return new Date(date).toLocaleString('en-PK', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          <DashboardIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
          Superuser Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Complete wallet management system administration
        </Typography>
      </Box>

      {/* Alerts */}
      {error && (
        <Alert severity="error" onClose={() => setError('')} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert severity="success" onClose={() => setSuccess('')} sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={currentTab} onChange={handleTabChange}>
          <Tab label="Overview" icon={<DashboardIcon />} iconPosition="start" />
          <Tab label="Wallets" icon={<AccountBalanceWallet />} iconPosition="start" />
          <Tab label="Transactions" icon={<Receipt />} iconPosition="start" />
          <Tab label="Payment Methods" icon={<Payment />} iconPosition="start" />
        </Tabs>
      </Paper>

      {/* Loading */}
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {/* Tab 0: Dashboard Overview */}
      {!loading && currentTab === 0 && stats && (
        <Grid container spacing={3}>
          {/* User Statistics */}
          <Grid item xs={12} md={6} lg={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography color="text.secondary" gutterBottom>
                      Total Users
                    </Typography>
                    <Typography variant="h4">{stats.totalUsers}</Typography>
                    <Typography variant="caption" color="success.main">
                      {stats.activeUsers} active
                    </Typography>
                  </Box>
                  <People sx={{ fontSize: 48, color: 'primary.main', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6} lg={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography color="text.secondary" gutterBottom>
                      Total Wallets
                    </Typography>
                    <Typography variant="h4">{stats.totalWallets}</Typography>
                    <Typography variant="caption" color="success.main">
                      {stats.activeWallets} active
                    </Typography>
                  </Box>
                  <AccountBalanceWallet sx={{ fontSize: 48, color: 'success.main', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6} lg={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography color="text.secondary" gutterBottom>
                      Total Balance
                    </Typography>
                    <Typography variant="h5">
                      {formatCurrency(stats.totalBalance)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      System-wide
                    </Typography>
                  </Box>
                  <TrendingUp sx={{ fontSize: 48, color: 'warning.main', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6} lg={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography color="text.secondary" gutterBottom>
                      Total Revenue
                    </Typography>
                    <Typography variant="h5">
                      {formatCurrency(stats.totalRevenue)}
                    </Typography>
                    <Typography variant="caption" color="success.main">
                      From fees
                    </Typography>
                  </Box>
                  <Receipt sx={{ fontSize: 48, color: 'error.main', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Transaction Statistics */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Transaction Statistics
                </Typography>
                <Grid container spacing={2} sx={{ mt: 1 }}>
                  <Grid item xs={6} md={3}>
                    <Box>
                      <Typography variant="h4" color="text.primary">
                        {stats.totalTransactions}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Total Transactions
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box>
                      <Typography variant="h4" color="success.main">
                        {stats.completedTransactions}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Completed
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box>
                      <Typography variant="h4" color="warning.main">
                        {stats.pendingTransactions}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Pending
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box>
                      <Typography variant="h4" color="error.main">
                        {stats.failedTransactions}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Failed
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* KYC Statistics */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  KYC Verification Status
                </Typography>
                <Grid container spacing={2} sx={{ mt: 1 }}>
                  <Grid item xs={6}>
                    <Box>
                      <Typography variant="h4" color="warning.main">
                        {stats.pendingKyc}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Pending Verification
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6}>
                    <Box>
                      <Typography variant="h4" color="success.main">
                        {stats.verifiedKyc}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Verified Users
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Transaction Volume
                </Typography>
                <Typography variant="h4" color="primary.main" sx={{ mt: 2 }}>
                  {formatCurrency(stats.totalTransactionVolume)}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Total transaction volume processed
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Tab 1: Wallets Management */}
      {!loading && currentTab === 1 && (
        <Paper>
          <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">Wallet Management</Typography>
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={loadWallets}
            >
              Refresh
            </Button>
          </Box>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Wallet Number</TableCell>
                  <TableCell>User</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Balance</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Created</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {wallets.map((wallet) => (
                  <TableRow key={wallet.id}>
                    <TableCell>{wallet.walletNumber}</TableCell>
                    <TableCell>{wallet.userFullName}</TableCell>
                    <TableCell>{wallet.userEmail}</TableCell>
                    <TableCell>{formatCurrency(wallet.balance, wallet.currency)}</TableCell>
                    <TableCell>{wallet.walletType}</TableCell>
                    <TableCell>
                      <Chip
                        label={wallet.walletStatus}
                        color={getStatusColor(wallet.walletStatus)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{formatDate(wallet.createdAt)}</TableCell>
                    <TableCell>
                      <IconButton
                        onClick={() => {
                          setSelectedWallet(wallet);
                          setWalletDialog(true);
                        }}
                        color="primary"
                        size="small"
                      >
                        <Edit />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      )}

      {/* Tab 2: Transactions */}
      {!loading && currentTab === 2 && (
        <>
          <Paper sx={{ p: 2, mb: 2 }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={transactionFilter.status}
                    label="Status"
                    onChange={(e) => setTransactionFilter({ ...transactionFilter, status: e.target.value })}
                  >
                    <MenuItem value="">All</MenuItem>
                    <MenuItem value="COMPLETED">Completed</MenuItem>
                    <MenuItem value="PENDING">Pending</MenuItem>
                    <MenuItem value="FAILED">Failed</MenuItem>
                    <MenuItem value="CANCELLED">Cancelled</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Type</InputLabel>
                  <Select
                    value={transactionFilter.type}
                    label="Type"
                    onChange={(e) => setTransactionFilter({ ...transactionFilter, type: e.target.value })}
                  >
                    <MenuItem value="">All</MenuItem>
                    <MenuItem value="CREDIT">Credit</MenuItem>
                    <MenuItem value="DEBIT">Debit</MenuItem>
                    <MenuItem value="TRANSFER_IN">Transfer In</MenuItem>
                    <MenuItem value="TRANSFER_OUT">Transfer Out</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} md={4}>
                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  onClick={loadTransactions}
                  fullWidth
                >
                  Refresh
                </Button>
              </Grid>
            </Grid>
          </Paper>

          <Paper>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Transaction Ref</TableCell>
                    <TableCell>User</TableCell>
                    <TableCell>Amount</TableCell>
                    <TableCell>Fee</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {transactions.map((tx) => (
                    <TableRow key={tx.id}>
                      <TableCell>{tx.transactionRef}</TableCell>
                      <TableCell>{tx.userFullName}</TableCell>
                      <TableCell>{formatCurrency(tx.amount, tx.currency)}</TableCell>
                      <TableCell>{tx.fee ? formatCurrency(tx.fee, tx.currency) : '-'}</TableCell>
                      <TableCell>
                        <Chip label={tx.transactionType} size="small" />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={tx.transactionStatus}
                          color={getStatusColor(tx.transactionStatus)}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>{formatDate(tx.createdAt)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </>
      )}

      {/* Tab 3: Payment Methods */}
      {!loading && currentTab === 3 && (
        <Paper>
          <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">Payment Methods</Typography>
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={loadPaymentMethods}
            >
              Refresh
            </Button>
          </Box>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>User</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Provider</TableCell>
                  <TableCell>Account/Card</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Created</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {paymentMethods.map((pm) => (
                  <TableRow key={pm.id}>
                    <TableCell>{pm.userFullName}</TableCell>
                    <TableCell>{pm.userEmail}</TableCell>
                    <TableCell>{pm.paymentType}</TableCell>
                    <TableCell>{pm.providerName || '-'}</TableCell>
                    <TableCell>
                      {pm.cardLastFour
                        ? `**** ${pm.cardLastFour}`
                        : pm.accountNumber || '-'}
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={pm.status}
                        color={getStatusColor(pm.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{formatDate(pm.createdAt)}</TableCell>
                    <TableCell>
                      <IconButton
                        onClick={() => deletePaymentMethod(pm.id)}
                        color="error"
                        size="small"
                      >
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      )}

      {/* Wallet Status Dialog */}
      <Dialog open={walletDialog} onClose={() => setWalletDialog(false)}>
        <DialogTitle>Update Wallet Status</DialogTitle>
        <DialogContent>
          {selectedWallet && (
            <Box sx={{ pt: 2 }}>
              <Typography variant="body2" gutterBottom>
                Wallet: {selectedWallet.walletNumber}
              </Typography>
              <Typography variant="body2" gutterBottom>
                User: {selectedWallet.userFullName}
              </Typography>
              <FormControl fullWidth sx={{ mt: 2 }}>
                <InputLabel>Status</InputLabel>
                <Select
                  value={selectedWallet.walletStatus}
                  label="Status"
                  onChange={(e) =>
                    setSelectedWallet({ ...selectedWallet, walletStatus: e.target.value })
                  }
                >
                  <MenuItem value="ACTIVE">Active</MenuItem>
                  <MenuItem value="INACTIVE">Inactive</MenuItem>
                  <MenuItem value="FROZEN">Frozen</MenuItem>
                  <MenuItem value="BLOCKED">Blocked</MenuItem>
                </Select>
              </FormControl>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setWalletDialog(false)}>Cancel</Button>
          <Button
            onClick={() => updateWalletStatus(selectedWallet.id, selectedWallet.walletStatus)}
            variant="contained"
          >
            Update
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default AdminDashboard;
