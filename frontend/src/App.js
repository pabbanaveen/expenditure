import React, { useState, useEffect } from 'react';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  Container,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Chip,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Checkbox,
  Paper,
  Drawer,
  FormControlLabel,
  Alert,
  CircularProgress,
  Accordion,
  AccordionSummary,
  AccordionDetails
} from '@mui/material';
import {
  Add,
  People,
  AccountBalanceWallet,
  ExpandMore,
  Info,
  Payment,
  CheckCircle,
  Close
} from '@mui/icons-material';
import axios from 'axios';

const API_BASE = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

function App() {
  const [chitties, setChitties] = useState([]);
  const [selectedChitty, setSelectedChitty] = useState(null);
  const [monthlySlips, setMonthlySlips] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Dialog states
  const [createChittyOpen, setCreateChittyOpen] = useState(false);
  const [slipDrawerOpen, setSlipDrawerOpen] = useState(false);
  const [selectedSlip, setSelectedSlip] = useState(null);
  
  // Form states
  const [newChitty, setNewChitty] = useState({
    name: '',
    amount: '',
    totalMembers: '',
    totalMonths: '',
    memberNames: ''
  });

  useEffect(() => {
    fetchChitties();
  }, []);

  const fetchChitties = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE}/api/chitties`);
      setChitties(response.data.data);
    } catch (err) {
      setError('Failed to fetch chitties');
    } finally {
      setLoading(false);
    }
  };

  const fetchMembers = async (chittiId) => {
    try {
      const response = await axios.get(`${API_BASE}/api/chitties/${chittiId}/members`);
      setMembers(response.data.data);
    } catch (err) {
      setError('Failed to fetch members');
    }
  };

  const fetchMonthlySlips = async (chittiId) => {
    try {
      const response = await axios.get(`${API_BASE}/api/monthly-slips/chitty/${chittiId}`);
      setMonthlySlips(response.data.data);
    } catch (err) {
      setError('Failed to fetch monthly slips');
    }
  };

  const handleChittyClick = async (chitty) => {
    setSelectedChitty(chitty);
    await fetchMembers(chitty.id);
    await fetchMonthlySlips(chitty.id);
  };

  const createChitty = async () => {
    try {
      setLoading(true);
      const memberNamesArray = newChitty.memberNames.split(',').map(name => name.trim()).filter(name => name);
      
      const chittyData = {
        name: newChitty.name,
        amount: parseFloat(newChitty.amount),
        totalMembers: parseInt(newChitty.totalMembers),
        totalMonths: parseInt(newChitty.totalMonths),
        memberNames: memberNamesArray
      };

      await axios.post(`${API_BASE}/api/chitties`, chittyData);
      setSuccess('Chitty created successfully!');
      setCreateChittyOpen(false);
      setNewChitty({ name: '', amount: '', totalMembers: '', totalMonths: '', memberNames: '' });
      fetchChitties();
    } catch (err) {
      setError('Failed to create chitty');
    } finally {
      setLoading(false);
    }
  };

  const generateMonthlySlip = async (chittiId, month) => {
    try {
      setLoading(true);
      await axios.post(`${API_BASE}/api/monthly-slips/generate?chittiId=${chittiId}&month=${month}`);
      setSuccess(`Monthly slip generated for month ${month}!`);
      fetchMonthlySlips(chittiId);
    } catch (err) {
      setError('Failed to generate monthly slip');
    } finally {
      setLoading(false);
    }
  };

  const openSlipDrawer = (slip) => {
    setSelectedSlip(slip);
    setSlipDrawerOpen(true);
  };

  const markPayment = async (slipId, memberId, isPaid) => {
    try {
      await axios.post(`${API_BASE}/api/monthly-slips/${slipId}/payment?memberId=${memberId}&isPaid=${isPaid}`);
      setSuccess('Payment status updated!');
      
      // Update local state
      setSelectedSlip(prev => ({
        ...prev,
        paymentRecords: prev.paymentRecords.map(record =>
          record.memberId === memberId 
            ? { ...record, paid: isPaid, paymentDate: isPaid ? new Date().toISOString() : null }
            : record
        )
      }));
    } catch (err) {
      setError('Failed to update payment status');
    }
  };

  const markAsLifted = async (slipId, memberId) => {
    try {
      await axios.post(`${API_BASE}/api/monthly-slips/${slipId}/lift?memberId=${memberId}`);
      setSuccess('Member marked as lifted!');
      
      // Refresh slip data
      const response = await axios.get(`${API_BASE}/api/monthly-slips/chitty/${selectedChitty.id}/month/${selectedSlip.month}`);
      setSelectedSlip(response.data.data);
    } catch (err) {
      setError('Failed to mark member as lifted');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  };

  const generateMonthCards = () => {
    const months = [];
    const totalMonths = selectedChitty?.totalMonths || 20;
    
    for (let i = 1; i <= totalMonths; i++) {
      const slip = monthlySlips.find(s => s.month === i);
      const hasLift = slip?.liftedMemberId;
      
      months.push(
        <Grid item xs={6} sm={4} md={3} key={i}>
          <Card 
            sx={{ 
              cursor: 'pointer',
              border: hasLift ? '2px solid #4caf50' : '1px solid #ddd',
              '&:hover': { boxShadow: 6 }
            }}
            onClick={() => {
              if (slip) {
                openSlipDrawer(slip);
              } else {
                generateMonthlySlip(selectedChitty.id, i);
              }
            }}
          >
            <CardContent sx={{ textAlign: 'center', p: 2 }}>
              <Typography variant="h6">Month {i}</Typography>
              {hasLift && (
                <Chip 
                  label="Lifted" 
                  color="success" 
                  size="small" 
                  sx={{ mt: 1 }}
                />
              )}
              {!slip && (
                <Typography variant="caption" color="textSecondary">
                  Click to generate slip
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      );
    }
    return months;
  };

  if (loading && chitties.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static" sx={{ background: 'linear-gradient(45deg, #2196F3 30%, #21CBF3 90%)' }}>
        <Toolbar>
          <AccountBalanceWallet sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Chitti Manager
          </Typography>
        </Toolbar>
      </AppBar>

      {error && (
        <Alert severity="error" onClose={() => setError('')} sx={{ m: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" onClose={() => setSuccess('')} sx={{ m: 2 }}>
          {success}
        </Alert>
      )}

      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {!selectedChitty ? (
          // Chitty List View
          <>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
              <Typography variant="h4" component="h1">
                My Chitties
              </Typography>
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => setCreateChittyOpen(true)}
                sx={{ background: 'linear-gradient(45deg, #FF6B6B 30%, #FF8E53 90%)' }}
              >
                Create Chitty
              </Button>
            </Box>

            <Grid container spacing={3}>
              {chitties.map((chitty) => (
                <Grid item xs={12} sm={6} md={4} key={chitty.id}>
                  <Card 
                    sx={{ 
                      cursor: 'pointer',
                      transition: 'all 0.3s ease',
                      '&:hover': { 
                        transform: 'translateY(-4px)',
                        boxShadow: 8 
                      }
                    }}
                    onClick={() => handleChittyClick(chitty)}
                  >
                    <CardContent>
                      <Typography variant="h6" component="h2" gutterBottom>
                        {chitty.name}
                      </Typography>
                      <Typography variant="h4" color="primary" gutterBottom>
                        {formatCurrency(chitty.amount)}
                      </Typography>
                      <Box display="flex" justifyContent="space-between" mt={2}>
                        <Chip 
                          icon={<People />} 
                          label={`${chitty.memberIds?.length || 0} Members`} 
                          size="small" 
                        />
                        <Chip 
                          label={`${chitty.totalMonths} Months`} 
                          size="small" 
                          color="secondary"
                        />
                      </Box>
                      <Box mt={2}>
                        <Typography variant="body2" color="textSecondary">
                          Regular: {formatCurrency(chitty.regularPayment)}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          Lifted: {formatCurrency(chitty.liftedPayment)}
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </>
        ) : (
          // Chitty Detail View
          <>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
              <Box>
                <Button onClick={() => setSelectedChitty(null)} sx={{ mb: 1 }}>
                  ← Back to Chitties
                </Button>
                <Typography variant="h4" component="h1">
                  {selectedChitty.name}
                </Typography>
                <Typography variant="h6" color="primary">
                  {formatCurrency(selectedChitty.amount)}
                </Typography>
              </Box>
            </Box>

            <Grid container spacing={3}>
              <Grid item xs={12} md={8}>
                <Paper sx={{ p: 3, mb: 3 }}>
                  <Typography variant="h6" gutterBottom>Monthly Slips</Typography>
                  <Grid container spacing={2}>
                    {generateMonthCards()}
                  </Grid>
                </Paper>
              </Grid>

              <Grid item xs={12} md={4}>
                <Paper sx={{ p: 3 }}>
                  <Typography variant="h6" gutterBottom>Members ({members.length})</Typography>
                  <List dense>
                    {members.map((member) => (
                      <ListItem key={member.id}>
                        <ListItemText 
                          primary={member.name}
                          secondary={member.hasLifted ? `Lifted in Month ${member.liftedMonth}` : 'Not lifted'}
                        />
                        {member.hasLifted && (
                          <Chip 
                            label="Lifted" 
                            color="success" 
                            size="small"
                            icon={<CheckCircle />}
                          />
                        )}
                      </ListItem>
                    ))}
                  </List>
                </Paper>
              </Grid>
            </Grid>
          </>
        )}
      </Container>

      {/* Create Chitty Dialog */}
      <Dialog open={createChittyOpen} onClose={() => setCreateChittyOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create New Chitty</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Chitty Name"
            value={newChitty.name}
            onChange={(e) => setNewChitty({...newChitty, name: e.target.value})}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Amount"
            type="number"
            value={newChitty.amount}
            onChange={(e) => setNewChitty({...newChitty, amount: e.target.value})}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Total Members"
            type="number"
            value={newChitty.totalMembers}
            onChange={(e) => setNewChitty({...newChitty, totalMembers: e.target.value})}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Total Months"
            type="number"
            value={newChitty.totalMonths}
            onChange={(e) => setNewChitty({...newChitty, totalMonths: e.target.value})}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Member Names (comma separated)"
            multiline
            rows={3}
            value={newChitty.memberNames}
            onChange={(e) => setNewChitty({...newChitty, memberNames: e.target.value})}
            margin="normal"
            placeholder="Rajesh Kumar, Priya Sharma, Amit Singh..."
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateChittyOpen(false)}>Cancel</Button>
          <Button onClick={createChitty} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={20} /> : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Monthly Slip Drawer */}
      <Drawer
        anchor="right"
        open={slipDrawerOpen}
        onClose={() => setSlipDrawerOpen(false)}
        sx={{ '& .MuiDrawer-paper': { width: { xs: '100%', sm: 400 } } }}
      >
        {selectedSlip && (
          <Box sx={{ p: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
              <Typography variant="h6">
                Month {selectedSlip.month} Slip
              </Typography>
              <IconButton onClick={() => setSlipDrawerOpen(false)}>
                <Close />
              </IconButton>
            </Box>

            <List>
              {selectedSlip.paymentRecords?.map((record) => (
                <ListItem key={record.memberId} sx={{ mb: 2, border: 1, borderColor: 'divider', borderRadius: 1 }}>
                  <Box sx={{ width: '100%' }}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography variant="subtitle1">{record.memberName}</Typography>
                      {record.lifted && (
                        <Chip 
                          label="Lifted" 
                          color="warning" 
                          size="small"
                          icon={<Info />}
                        />
                      )}
                    </Box>
                    <Typography variant="h6" color="primary">
                      {formatCurrency(record.amount)}
                    </Typography>
                    
                    <Box display="flex" justifyContent="space-between" alignItems="center" mt={1}>
                      <FormControlLabel
                        control={
                          <Checkbox
                            checked={record.paid}
                            onChange={(e) => markPayment(selectedSlip.id, record.memberId, e.target.checked)}
                            color="success"
                          />
                        }
                        label="Paid"
                      />
                      
                      {!record.lifted && (
                        <Button
                          size="small"
                          variant="outlined"
                          color="warning"
                          onClick={() => markAsLifted(selectedSlip.id, record.memberId)}
                        >
                          Mark as Lifted
                        </Button>
                      )}
                    </Box>
                    
                    {record.paymentDate && (
                      <Typography variant="caption" color="textSecondary">
                        Paid: {new Date(record.paymentDate).toLocaleString()}
                      </Typography>
                    )}
                  </Box>
                </ListItem>
              ))}
            </List>
          </Box>
        )}
      </Drawer>
    </Box>
  );
}

export default App;