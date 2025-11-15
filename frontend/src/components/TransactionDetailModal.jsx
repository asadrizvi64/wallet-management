import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Grid,
  Typography,
  Divider,
  Chip,
  Box,
  Paper
} from '@mui/material';
import {
  AccountBalanceWallet,
  Person,
  CalendarToday,
  Receipt,
  AttachMoney,
  TrendingUp,
  TrendingDown,
  Payment,
  Description
} from '@mui/icons-material';

const TransactionDetailModal = ({ open, onClose, transaction }) => {
  if (!transaction) return null;

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-PK', {
      style: 'currency',
      currency: 'PKR',
      minimumFractionDigits: 2,
    }).format(amount);
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'FAILED':
        return 'error';
      case 'CANCELLED':
        return 'default';
      case 'REFUNDED':
        return 'info';
      default:
        return 'default';
    }
  };

  const getTransactionTypeColor = (type) => {
    switch (type) {
      case 'CREDIT':
      case 'TOP_UP':
      case 'TRANSFER_IN':
        return 'success';
      case 'DEBIT':
      case 'WITHDRAWAL':
      case 'TRANSFER_OUT':
        return 'error';
      case 'PAYMENT':
      case 'REFUND':
        return 'info';
      default:
        return 'default';
    }
  };

  const DetailRow = ({ icon, label, value, valueColor }) => (
    <Box sx={{ display: 'flex', alignItems: 'center', py: 1.5 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', minWidth: 200 }}>
        {icon && <Box sx={{ mr: 1, display: 'flex', color: 'text.secondary' }}>{icon}</Box>}
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
      </Box>
      <Typography variant="body1" fontWeight={500} color={valueColor || 'text.primary'}>
        {value}
      </Typography>
    </Box>
  );

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Transaction Details</Typography>
          <Chip
            label={transaction.transactionStatus}
            color={getStatusColor(transaction.transactionStatus)}
            size="small"
          />
        </Box>
      </DialogTitle>
      <DialogContent dividers>
        <Grid container spacing={3}>
          {/* Transaction Information */}
          <Grid item xs={12}>
            <Paper elevation={0} sx={{ bgcolor: 'grey.50', p: 2 }}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                TRANSACTION INFORMATION
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <DetailRow
                icon={<Receipt fontSize="small" />}
                label="Transaction Reference"
                value={transaction.transactionRef}
              />
              <DetailRow
                icon={<Description fontSize="small" />}
                label="Type"
                value={
                  <Chip
                    label={transaction.transactionType}
                    color={getTransactionTypeColor(transaction.transactionType)}
                    size="small"
                  />
                }
              />
              <DetailRow
                icon={<AttachMoney fontSize="small" />}
                label="Amount"
                value={formatCurrency(transaction.amount)}
                valueColor="primary.main"
              />
              {transaction.transactionFee > 0 && (
                <DetailRow
                  icon={<Payment fontSize="small" />}
                  label="Transaction Fee"
                  value={formatCurrency(transaction.transactionFee)}
                />
              )}
              {transaction.description && (
                <DetailRow
                  icon={<Description fontSize="small" />}
                  label="Description"
                  value={transaction.description}
                />
              )}
            </Paper>
          </Grid>

          {/* Sender Information */}
          <Grid item xs={12} md={6}>
            <Paper elevation={0} sx={{ bgcolor: 'grey.50', p: 2, height: '100%' }}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                SENDER INFORMATION
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <DetailRow
                icon={<Person fontSize="small" />}
                label="Name"
                value={transaction.senderName}
              />
              <DetailRow
                icon={<AccountBalanceWallet fontSize="small" />}
                label="Wallet Number"
                value={transaction.senderWalletNumber}
              />
            </Paper>
          </Grid>

          {/* Recipient Information */}
          <Grid item xs={12} md={6}>
            <Paper elevation={0} sx={{ bgcolor: 'grey.50', p: 2, height: '100%' }}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                RECIPIENT INFORMATION
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {transaction.recipientWalletNumber ? (
                <>
                  <DetailRow
                    icon={<Person fontSize="small" />}
                    label="Name"
                    value={transaction.recipientName || 'N/A'}
                  />
                  <DetailRow
                    icon={<AccountBalanceWallet fontSize="small" />}
                    label="Wallet Number"
                    value={transaction.recipientWalletNumber}
                  />
                </>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  No recipient (external transaction)
                </Typography>
              )}
            </Paper>
          </Grid>

          {/* Balance Information */}
          <Grid item xs={12}>
            <Paper elevation={0} sx={{ bgcolor: 'grey.50', p: 2 }}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                BALANCE INFORMATION
              </Typography>
              <Divider sx={{ mb: 2 }} />

              <DetailRow
                icon={<TrendingDown fontSize="small" />}
                label="Balance Before"
                value={formatCurrency(transaction.balanceBefore)}
              />
              <DetailRow
                icon={<TrendingUp fontSize="small" />}
                label="Balance After"
                value={formatCurrency(transaction.balanceAfter)}
              />
            </Paper>
          </Grid>

          {/* Additional Details */}
          <Grid item xs={12}>
            <Paper elevation={0} sx={{ bgcolor: 'grey.50', p: 2 }}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                ADDITIONAL DETAILS
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {transaction.paymentMethod && (
                <DetailRow
                  icon={<Payment fontSize="small" />}
                  label="Payment Method"
                  value={transaction.paymentMethod}
                />
              )}
              <DetailRow
                icon={<CalendarToday fontSize="small" />}
                label="Created At"
                value={formatDateTime(transaction.createdAt)}
              />
              {transaction.completedAt && (
                <DetailRow
                  icon={<CalendarToday fontSize="small" />}
                  label="Completed At"
                  value={formatDateTime(transaction.completedAt)}
                />
              )}
              <DetailRow
                label="Currency"
                value={transaction.currency}
              />
            </Paper>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary" variant="contained">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TransactionDetailModal;
