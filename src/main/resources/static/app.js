// Package Locker System - Frontend App
// Handles multi-step workflow with API calls

const API_BASE = window.location.protocol.startsWith('http') ? '/api' : 'http://localhost:8080/api';
let currentDelivery = null;

// ===== Utility Functions =====
function showStep(stepNumber) {
    document.querySelectorAll('.step-section').forEach(section => {
        section.classList.add('hidden');
    });
    document.getElementById(`step${stepNumber}`).classList.remove('hidden');
}

function renderJson(data) {
    return `<pre>${JSON.stringify(data, null, 2)}</pre>`;
}

function maskUuid(id) {
    if (!id || id.length < 7) {
        return id || '';
    }
    return `${id.slice(0, 3)}***${id.slice(-3)}`;
}

function renderDeliverySummary(delivery) {
    if (!delivery) {
        return '<p>No delivery information available.</p>';
    }

    return `
        <div>
            <p><strong>Status:</strong> ${delivery.status || 'UNKNOWN'}</p>
            <p><strong>Package size:</strong> ${delivery.size || 'N/A'}</p>
            <p><strong>Locker:</strong> ${maskUuid(delivery.assignedLockerId || delivery.lockerId || '')}</p>
            <p><strong>Customer:</strong> ${maskUuid(delivery.customerId || '')}</p>
        </div>
    `;
}

async function apiCall(method, endpoint, body = null) {
    try {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        if (body) {
            options.body = JSON.stringify(body);
        }
        
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const data = await response.json();
        
        if (!response.ok) {
            const error = new Error(data.message || `API error: ${response.status}`);
            error.status = response.status;
            throw error;
        }
        
        return data;
    } catch (error) {
        throw error;
    }
}

function formatLockerId(id) {
    return maskUuid(id);
}

function renderStatusCell(status) {
    const normalized = String(status || '').toUpperCase();
    const className = normalized === 'AVAILABLE' ? 'status-available' : normalized === 'OCCUPIED' ? 'status-occupied' : 'status-neutral';
    return `<span class="status-pill ${className}">${normalized}</span>`;
}

function renderLockers(lockers) {
    const lockersBody = document.getElementById('lockersBody');
    if (!lockers || lockers.length === 0) {
        lockersBody.innerHTML = '<tr><td colspan="4">No lockers available</td></tr>';
        return;
    }
    lockersBody.innerHTML = lockers.map(locker => `
        <tr>
            <td>${formatLockerId(locker.id)}</td>
            <td>${locker.code}</td>
            <td>${locker.size}</td>
            <td>${renderStatusCell(locker.status)}</td>
        </tr>
    `).join('');
}

async function loadLockers() {
    try {
        const lockers = await apiCall('GET', '/lockers');
        renderLockers(lockers);
    } catch (error) {
        const lockersBody = document.getElementById('lockersBody');
        lockersBody.innerHTML = `<tr><td colspan="4">Unable to load lockers</td></tr>`;
    }
}

// ===== Step 1: Create Delivery =====
document.getElementById('deliveryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const customerId = document.getElementById('customerId').value;
    const packageSize = document.getElementById('packageSize').value;
    
    try {
        const result = await apiCall('POST', '/deliveries', {
            customerId,
            packageSize
        });
        
        currentDelivery = result;
        
        // Show result in step 1
        const step1Result = document.getElementById('step1Result');
        step1Result.innerHTML = renderJson(result);
        step1Result.classList.remove('hidden');
        
        // Move to step 2
        showStep(2);
        
        // Populate step 2 with delivery info
        const step2Content = document.getElementById('step2Content');
        step2Content.innerHTML = `
            <strong>Delivery ID:</strong> ${result.deliveryId}<br>
            <strong>Locker:</strong> ${result.lockerId}<br>
            <strong>PIN:</strong> ${result.pin}<br>
            <strong>Status:</strong> ${result.assigned ? 'ASSIGNED' : 'UNASSIGNED'}<br>
            <div style="margin-top: 10px; padding: 10px; background-color: #fffacd;">
                <strong>⚠️ Important:</strong> Save this PIN to pickup your package later.
            </div>
        `;
        
        await loadLockers();
    } catch (error) {
        let message = error.message;
        if (error.status === 422) {
            message = 'No lockers available for the selected package size. Please try a different size or try again later.';
        }
        alert(`Error creating delivery: ${message}`);
    }
});

// ===== Step 2: Continue to Deposit =====
document.getElementById('continueToDepositBtn').addEventListener('click', () => {
    showStep(3);
});

// ===== Step 3: Confirm Deposit =====
document.getElementById('confirmDepositBtn').addEventListener('click', async () => {
    if (!currentDelivery) {
        alert('No active delivery. Please create one first.');
        return;
    }
    
    const deliveryId = currentDelivery.deliveryId || currentDelivery.id;
    
    try {
        const result = await apiCall('POST', `/deliveries/${deliveryId}/deposit`, {});
        
        const step3Result = document.getElementById('step3Result');
        step3Result.innerHTML = `
            <div>
                <p><strong>Deposit confirmed.</strong> The package is now in the locker.</p>
                ${renderDeliverySummary(result)}
            </div>
        `;
        step3Result.classList.remove('hidden');
        
        currentDelivery = result;
        await loadLockers();
        
        setTimeout(() => {
            showStep(4);
        }, 1500);
        
    } catch (error) {
        alert(`Error confirming deposit: ${error.message}`);
    }
});

// ===== Step 4: Pickup =====
document.getElementById('pickupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    if (!currentDelivery) {
        alert('No active delivery. Please start over.');
        return;
    }
    
    const pin = document.getElementById('pickupPin').value;
    
    try {
        const result = await apiCall('POST', `/deliveries/${currentDelivery.id}/pickup`, {
            pin
        });
        
        const step4Result = document.getElementById('step4Result');
        step4Result.innerHTML = `
            <div>
                <p><strong>Package picked up successfully.</strong></p>
                ${renderDeliverySummary(result)}
            </div>
        `;
        step4Result.classList.remove('hidden');
        
        currentDelivery = result;
        await loadLockers();
        
        setTimeout(() => {
            showStep(5);
        }, 1500);
        
    } catch (error) {
        alert(`Error picking up package: ${error.message}`);
    }
});

// ===== Step 5: Check Final Status =====
document.getElementById('checkStatusBtn').addEventListener('click', async () => {
    if (!currentDelivery) {
        alert('No active delivery.');
        return;
    }
    
    try {
        const deliveryId = currentDelivery.deliveryId || currentDelivery.id;
        const result = await apiCall('GET', `/deliveries/${deliveryId}`, null);
        
        const step5Content = document.getElementById('step5Content');
        step5Content.innerHTML = renderDeliverySummary(result);
        step5Content.classList.remove('hidden');
        
        // Show reset button
        document.getElementById('resetSection').classList.remove('hidden');
        
    } catch (error) {
        alert(`Error fetching delivery status: ${error.message}`);
    }
});

// ===== Reset =====
document.getElementById('resetBtn').addEventListener('click', () => {
    currentDelivery = null;
    document.getElementById('deliveryForm').reset();
    document.getElementById('pickupForm').reset();
    document.querySelectorAll('.result-box').forEach(box => box.classList.add('hidden'));
    document.getElementById('resetSection').classList.add('hidden');
    showStep(1);
});

// ===== Initialize =====
window.addEventListener('DOMContentLoaded', async () => {
    await loadLockers();
    showStep(1);
});
