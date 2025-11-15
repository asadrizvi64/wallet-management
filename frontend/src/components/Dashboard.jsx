import React, { useState, useEffect, useCallback } from 'react';
import {
  Box, Container, Grid, Paper, Typography, Button, AppBar, Toolbar,
  Divider, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Alert, Chip
} from '@mui/material';
import {
  AccountBalanceWallet, Send, Logout, Add, Remove
} from '@mui/icons-material';
import axios from 'axios';

const API_BASE_URL = '/api/v1';

function Dashboard({ user, onLogout }) {
  const [walletData, setWalletData] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [openDialog, setOpenDialog] = useState('');
  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState({ type: '', text: '' });
  const [loading, setLoading] = useState(false);

  const fetchWalletData = useCallback(async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/wallets/${user.walletNumber}`);
      if (response.data.success) {
        setWalletData(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching wallet data:', error);
    }
  }, [user.walletNumber]);

  const fetchTransactions = useCallback(async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/transactions/history/${user.walletNumber}`
      );
      if (response.data.success) {
        // Extract transactions array from TransactionHistoryResponse object
        const txnData = response.data.data.transactions || [];
        setTransactions(Array.isArray(txnData) ? txnData : []);
      }
    } catch (error) {
      console.error('Error fetching transactions:', error);
      setTransactions([]); // Reset to empty array on error
    }
  }, [user.walletNumber]);

  useEffect(() => {
    fetchWalletData();
    fetchTransactions();
  }, [fetchWalletData, fetchTransactions]);

  const handleAddMoney = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        `${API_BASE_URL}/wallets/${user.walletNumber}/add-money`,
        {
          amount: parseFloat(formData.amount),
          paymentMethod: 'CARD',
          paymentGatewayRef: 'PG-' + Date.now()
        }
      );
      
      if (response.data.success) {
        setMessage({ type: 'success', text: 'Money added successfully!' });
        fetchWalletData();
        fetchTransactions();
        setOpenDialog('');
        setFormData({});
      }
    } catch (error) {
      setMessage({ 
        type: 'error', 
        text: error.response?.data?.message || 'Failed to add money' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        `${API_BASE_URL}/wallets/${user.walletNumber}/withdraw`,
        {
          amount: parseFloat(formData.amount),
          bankAccount: formData.bankAccount || 'XXXXX1234',
          ifscCode: formData.ifscCode || 'XXXXX0000'
        }
      );
      
      if (response.data.success) {
        setMessage({ type: 'success', text: 'Withdrawal successful!' });
        fetchWalletData();
        fetchTransactions();
        setOpenDialog('');
        setFormData({});
      }
    } catch (error) {
      setMessage({ 
        type: 'error', 
        text: error.response?.data?.message || 'Withdrawal failed' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleTransfer = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        `${API_BASE_URL}/transactions/transfer`,
        {
          fromWalletId: walletData.id,
          toWalletNumber: formData.recipientWallet,
          amount: parseFloat(formData.amount),
          description: formData.description || 'Money transfer'
        }
      );

      if (response.data.success) {
        setMessage({ type: 'success', text: 'Transfer successful!' });
        fetchWalletData();
        fetchTransactions();
        setOpenDialog('');
        setFormData({});
      }
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Transfer failed'
      });
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-PK', {
      style: 'currency',
      currency: 'PKR',
      minimumFractionDigits: 0
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('en-PK', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <Box>
      <AppBar position="static">
        <Toolbar>
          <AccountBalanceWallet sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Wallet Management System
          </Typography>
          <Typography variant="body2" sx={{ mr: 2 }}>
            {user.fullName}
          </Typography>
          <Button color="inherit" onClick={onLogout} startIcon={<Logout />}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {message.text && (
          <Alert 
            severity={message.type} 
            onClose={() => setMessage({ type: '', text: '' })}
            sx={{ mb: 3 }}
          >
            {message.text}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* Wallet Balance Card */}
          <Grid item xs={12} md={8}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h5" gutterBottom>
                Wallet Overview
              </Typography>
              <Divider sx={{ my: 2 }} />
              
              <Box sx={{ textAlign: 'center', my: 3 }}>
                <Typography variant="h3" color="primary" fontWeight="bold">
                  {walletData ? formatCurrency(walletData.balance) : 'Loading...'}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  Available Balance
                </Typography>
                <Chip 
                  label={walletData?.status || 'ACTIVE'} 
                  color="success" 
                  size="small" 
                  sx={{ mt: 1 }}
                />
              </Box>

              <Divider sx={{ my: 2 }} />

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Wallet Number
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {user.walletNumber}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Currency
                  </Typography>
                  <Typography variant="body1" fontWeight="medium">
                    {walletData?.currency || 'PKR'}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Daily Limit
                  </Typography>
                  <Typography variant="body1">
                    {walletData ? formatCurrency(walletData.dailyLimit) : '-'}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Daily Spent
                  </Typography>
                  <Typography variant="body1">
                    {walletData ? formatCurrency(walletData.dailySpent) : '-'}
                  </Typography>
                </Grid>
              </Grid>
            </Paper>
          </Grid>

          {/* Quick Actions */}
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              <Divider sx={{ my: 2 }} />
              
              <Button
                fullWidth
                variant="contained"
                startIcon={<Add />}
                sx={{ mb: 2 }}
                onClick={() => setOpenDialog('addMoney')}
              >
                Add Money
              </Button>
              
              <Button
                fullWidth
                variant="contained"
                color="secondary"
                startIcon={<Send />}
                sx={{ mb: 2 }}
                onClick={() => setOpenDialog('transfer')}
              >
                Send Money
              </Button>
              
              <Button
                fullWidth
                variant="outlined"
                startIcon={<Remove />}
                onClick={() => setOpenDialog('withdraw')}
              >
                Withdraw
              </Button>
            </Paper>
          </Grid>

          {/* Transactions */}
          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Recent Transactions
              </Typography>
              <Divider sx={{ my: 2 }} />
              
              {!Array.isArray(transactions) || transactions.length === 0 ? (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No transactions yet
                </Typography>
              ) : (
                <Box>
                  {transactions.slice(0, 10).map((txn) => (
                    <Box key={txn.id}>
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          py: 2
                        }}
                      >
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Box
                            sx={{
                              width: 40,
                              height: 40,
                              borderRadius: '50%',
                              bgcolor: ['CREDIT', 'TOP_UP', 'TRANSFER_IN'].includes(txn.transactionType) ? 'success.light' :
                                      ['DEBIT', 'WITHDRAWAL', 'TRANSFER_OUT'].includes(txn.transactionType) ? 'error.light' : 'info.light',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              mr: 2
                            }}
                          >
                            {['CREDIT', 'TOP_UP', 'TRANSFER_IN'].includes(txn.transactionType) && <Add />}
                            {['DEBIT', 'WITHDRAWAL', 'TRANSFER_OUT'].includes(txn.transactionType) && <Remove />}
                            {['PAYMENT', 'REFUND'].includes(txn.transactionType) && <Send />}
                          </Box>
                          <Box>
                            <Typography variant="body1" fontWeight="medium">
                              {txn.description}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {formatDate(txn.createdAt)} • {txn.transactionRef}
                            </Typography>
                          </Box>
                        </Box>
                        <Box sx={{ textAlign: 'right' }}>
                          <Typography
                            variant="h6"
                            color={
                              ['CREDIT', 'TOP_UP', 'TRANSFER_IN'].includes(txn.transactionType) ? 'success.main' : 'error.main'
                            }
                          >
                            {['CREDIT', 'TOP_UP', 'TRANSFER_IN'].includes(txn.transactionType) ? '+' : '-'}
                            {formatCurrency(txn.amount)}
                          </Typography>
                          <Chip
                            label={txn.transactionStatus}
                            size="small"
                            color={txn.transactionStatus === 'COMPLETED' ? 'success' : 'default'}
                          />
                        </Box>
                      </Box>
                      <Divider />
                    </Box>
                  ))}
                </Box>
              )}
            </Paper>
          </Grid>
        </Grid>

        {/* Add Money Dialog */}
        <Dialog open={openDialog === 'addMoney'} onClose={() => setOpenDialog('')}>
          <DialogTitle>Add Money to Wallet</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="Amount (PKR)"
              type="number"
              fullWidth
              value={formData.amount || ''}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog('')}>Cancel</Button>
            <Button onClick={handleAddMoney} variant="contained" disabled={loading}>
              {loading ? 'Processing...' : 'Add Money'}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Withdraw Dialog */}
        <Dialog open={openDialog === 'withdraw'} onClose={() => setOpenDialog('')}>
          <DialogTitle>Withdraw Money</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="Amount (PKR)"
              type="number"
              fullWidth
              value={formData.amount || ''}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              margin="dense"
              label="Bank Account"
              fullWidth
              value={formData.bankAccount || ''}
              onChange={(e) => setFormData({ ...formData, bankAccount: e.target.value })}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog('')}>Cancel</Button>
            <Button onClick={handleWithdraw} variant="contained" disabled={loading}>
              {loading ? 'Processing...' : 'Withdraw'}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Transfer Dialog */}
        <Dialog open={openDialog === 'transfer'} onClose={() => setOpenDialog('')}>
          <DialogTitle>Transfer Money</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="Recipient Wallet Number"
              fullWidth
              value={formData.recipientWallet || ''}
              onChange={(e) => setFormData({ ...formData, recipientWallet: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              margin="dense"
              label="Amount (PKR)"
              type="number"
              fullWidth
              value={formData.amount || ''}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              margin="dense"
              label="Description (Optional)"
              fullWidth
              value={formData.description || ''}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog('')}>Cancel</Button>
            <Button onClick={handleTransfer} variant="contained" disabled={loading}>
              {loading ? 'Processing...' : 'Transfer'}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
}

export default Dashboard;
