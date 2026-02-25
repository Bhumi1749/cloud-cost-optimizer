let vmData = [];
let currentSimulationVM = null;
let clusterChart = null;
let costChart = null;
let utilizationChart = null;

// Load VMs on page load
window.onload = function() {
    loadVMs();
};

// Analyze VMs
async function analyzeVMs() {
    const filePath = "data/vm_usage_data.csv";
    
    try {
        const response = await fetch(`/api/vms/analyze?filePath=${filePath}`, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Analysis completed successfully!');
            loadVMs();
        } else {
            alert('Error: ' + result.message);
        }
    } catch (error) {
        alert('Error analyzing VMs: ' + error);
    }
}

// Load all VMs
async function loadVMs() {
    try {
        const response = await fetch('/api/vms/all');
        vmData = await response.json();
        displayVMs();
        updateStats();
    } catch (error) {
        console.error('Error loading VMs:', error);
    }
}

// Display VMs in table
function displayVMs() {
    const tbody = document.getElementById('vmTableBody');
    tbody.innerHTML = '';
    
    vmData.forEach(vm => {
        const row = document.createElement('tr');
        row.className = `cluster-${vm.cluster}`;
        
        const recClass = vm.recommendation.includes('Under') ? 'under-utilized' : 
                        vm.recommendation.includes('Over') ? 'over-utilized' : 'optimal';
        
        row.innerHTML = `
            <td>${vm.vmId}</td>
            <td>${vm.cpuUsage.toFixed(1)}%</td>
            <td>${vm.ramUsage.toFixed(1)}%</td>
            <td>${vm.diskUsage.toFixed(1)}%</td>
            <td>$${vm.costPerHour.toFixed(2)}</td>
            <td>${vm.cluster}</td>
            <td class="recommendation ${recClass}">${vm.recommendation}</td>
            <td>$${(vm.potentialSavings || 0).toFixed(2)}</td>
            <td>
                <button class="btn-simulate" onclick='openSimulation("${vm.vmId}")'>
                    🎯 Simulate
                </button>
            </td>
        `;
        
        tbody.appendChild(row);
    });
}

// Update statistics
function updateStats() {
    const totalVMs = vmData.length;
    const totalSavings = vmData.reduce((sum, vm) => sum + (vm.potentialSavings || 0), 0);
    const underUtilized = vmData.filter(vm => vm.recommendation.includes('Under')).length;
    const overUtilized = vmData.filter(vm => vm.recommendation.includes('Over')).length;
    
    document.getElementById('totalVMs').textContent = totalVMs;
    document.getElementById('totalSavings').textContent = '$' + totalSavings.toFixed(2);
    document.getElementById('underUtilized').textContent = underUtilized;
    document.getElementById('overUtilized').textContent = overUtilized;
    
    updateCharts();
}

// Clear all VMs
async function clearVMs() {
    if (confirm('Are you sure you want to clear all VM data?')) {
        try {
            await fetch('/api/vms/clear', { method: 'DELETE' });
            alert('All VMs cleared!');
            loadVMs();
        } catch (error) {
            alert('Error clearing VMs: ' + error);
        }
    }
}

// Open simulation modal
function openSimulation(vmId) {
    const vm = vmData.find(v => v.vmId === vmId);
    if (!vm) return;
    
    currentSimulationVM = vm;
    
    // Populate current state
    document.getElementById('sim-vmid').textContent = vm.vmId;
    document.getElementById('sim-current-cost').textContent = '$' + vm.costPerHour.toFixed(2);
    document.getElementById('sim-current-cpu').textContent = vm.cpuUsage.toFixed(1) + '%';
    document.getElementById('sim-current-ram').textContent = vm.ramUsage.toFixed(1) + '%';
    
    // Hide results initially
    document.getElementById('simulationResults').style.display = 'none';
    
    // Show modal
    document.getElementById('simulationModal').style.display = 'block';
}

// Close simulation modal
function closeSimulationModal() {
    document.getElementById('simulationModal').style.display = 'none';
    currentSimulationVM = null;
}

// Simulate action
async function simulateAction(actionType) {
    if (!currentSimulationVM) return;
    
    try {
        const response = await fetch(`/api/vms/simulate?vmId=${currentSimulationVM.vmId}&actionType=${actionType}`, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.success) {
            displaySimulationResults(result.scenario);
        } else {
            alert('Simulation error: ' + result.message);
        }
    } catch (error) {
        alert('Error running simulation: ' + error);
    }
}

// Display simulation results
function displaySimulationResults(scenario) {
    // Show results section
    document.getElementById('simulationResults').style.display = 'block';
    
    // Cost Impact
    const costImpact = scenario.costImpact;
    const costImpactEl = document.getElementById('sim-cost-impact');
    if (costImpact > 0) {
        costImpactEl.textContent = '+$' + costImpact.toFixed(2) + ' savings';
        costImpactEl.style.color = '#27ae60';
    } else {
        costImpactEl.textContent = '-$' + Math.abs(costImpact).toFixed(2) + ' cost';
        costImpactEl.style.color = '#e74c3c';
    }
    
    // Performance Impact
    const perfImpact = scenario.performanceImpact;
    const perfImpactEl = document.getElementById('sim-perf-impact');
    if (perfImpact > 0) {
        perfImpactEl.textContent = '+' + perfImpact.toFixed(1) + '%';
        perfImpactEl.style.color = '#27ae60';
    } else {
        perfImpactEl.textContent = perfImpact.toFixed(1) + '%';
        perfImpactEl.style.color = '#e74c3c';
    }
    
    // Risk Level
    const riskEl = document.getElementById('sim-risk-level');
    riskEl.textContent = scenario.riskLevel + ' (' + scenario.riskScore.toFixed(0) + '/100)';
    
    if (scenario.riskLevel === 'LOW') {
        riskEl.style.color = '#27ae60';
    } else if (scenario.riskLevel === 'MEDIUM') {
        riskEl.style.color = '#f39c12';
    } else {
        riskEl.style.color = '#e74c3c';
    }
    
    // Confidence
    document.getElementById('sim-confidence').textContent = scenario.confidenceScore.toFixed(0) + '%';
    
    // Recommendation Badge
    const recBadge = document.getElementById('sim-recommendation');
    recBadge.textContent = scenario.recommendation.replace(/_/g, ' ');
    
    if (scenario.recommendation === 'SAFE_TO_APPLY') {
        recBadge.className = 'badge badge-safe';
    } else if (scenario.recommendation === 'APPLY_WITH_CAUTION') {
        recBadge.className = 'badge badge-caution';
    } else {
        recBadge.className = 'badge badge-danger';
    }
    
    // Reasoning
    document.getElementById('sim-reasoning').textContent = scenario.reasoning;
    
    // Scroll to results
    document.getElementById('simulationResults').scrollIntoView({ behavior: 'smooth' });
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('simulationModal');
    if (event.target == modal) {
        closeSimulationModal();
    }
}

// Create and update charts
function updateCharts() {
    if (vmData.length === 0) return;
    
    // Cluster Distribution Chart
    const clusterCounts = [0, 0, 0];
    vmData.forEach(vm => {
        if (vm.cluster !== null && vm.cluster !== undefined) {
            clusterCounts[vm.cluster]++;
        }
    });
    
    const clusterCtx = document.getElementById('clusterChart');
    if (clusterChart) clusterChart.destroy();
    
    clusterChart = new Chart(clusterCtx, {
        type: 'pie',
        data: {
            labels: ['Cluster 0 (Under-utilized)', 'Cluster 1 (Optimal)', 'Cluster 2 (Over-utilized)'],
            datasets: [{
                data: clusterCounts,
                backgroundColor: ['#e74c3c', '#27ae60', '#f39c12'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
    
    // Cost Analysis Chart
    const vmIds = vmData.map(vm => vm.vmId);
    const currentCosts = vmData.map(vm => (vm.costPerHour * 730).toFixed(2));
    const potentialSavings = vmData.map(vm => (vm.potentialSavings || 0).toFixed(2));
    
    const costCtx = document.getElementById('costChart');
    if (costChart) costChart.destroy();
    
    costChart = new Chart(costCtx, {
        type: 'bar',
        data: {
            labels: vmIds,
            datasets: [
                {
                    label: 'Current Monthly Cost ($)',
                    data: currentCosts,
                    backgroundColor: '#3498db',
                    borderWidth: 0
                },
                {
                    label: 'Potential Savings ($)',
                    data: potentialSavings,
                    backgroundColor: '#27ae60',
                    borderWidth: 0
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
    
    // Resource Utilization Chart
    const cpuUsage = vmData.map(vm => vm.cpuUsage.toFixed(1));
    const ramUsage = vmData.map(vm => vm.ramUsage.toFixed(1));
    const diskUsage = vmData.map(vm => vm.diskUsage.toFixed(1));
    
    const utilCtx = document.getElementById('utilizationChart');
    if (utilizationChart) utilizationChart.destroy();
    
    utilizationChart = new Chart(utilCtx, {
        type: 'line',
        data: {
            labels: vmIds,
            datasets: [
                {
                    label: 'CPU Usage (%)',
                    data: cpuUsage,
                    borderColor: '#e74c3c',
                    backgroundColor: 'rgba(231, 76, 60, 0.1)',
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'RAM Usage (%)',
                    data: ramUsage,
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'Disk Usage (%)',
                    data: diskUsage,
                    borderColor: '#f39c12',
                    backgroundColor: 'rgba(243, 156, 18, 0.1)',
                    tension: 0.4,
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Export to Excel
function exportToExcel() {
    window.location.href = '/api/vms/export/excel';
}

// Export to PDF
function exportToPDF() {
    window.location.href = '/api/vms/export/pdf';
}
// Upload CSV file
async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    
    if (!file) {
        alert('Please select a file');
        return;
    }
    
    if (!file.name.endsWith('.csv')) {
        alert('Please select a CSV file');
        return;
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    try {
        const response = await fetch('/api/vms/upload', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('File uploaded and analyzed successfully!');
            loadVMs();
        } else {
            alert('Error: ' + result.message);
        }
    } catch (error) {
        alert('Error uploading file: ' + error);
    }
    
    // Reset file input
    fileInput.value = '';
}