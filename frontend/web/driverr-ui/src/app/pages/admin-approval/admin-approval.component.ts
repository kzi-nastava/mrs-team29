import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-admin-approval',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-approval.component.html',
  styleUrls: ['./admin-approval.component.css']
})
export class AdminApprovalComponent implements OnInit {

  profileChangeRequests: any[] = [];
  loading = false;
  message = '';
  messageType = '';
  processingRequestIds = new Set<string>();

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.loadPendingRequests();
  }

  loadPendingRequests() {
    this.loading = true;
    this.profileService.getAllPendingProfileChangeRequests().subscribe({
      next: (requests) => {
        console.log('Admin approval requests loaded:', requests);
        console.log('Total requests:', requests.length);
        if (requests.length > 0) {
          console.log('First request structure:', JSON.stringify(requests[0], null, 2));
        }
        this.profileChangeRequests = requests.filter(
          (request) => request.status === 'PENDING' || !request.status
        );
        console.log('Filtered requests:', this.profileChangeRequests);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading admin approval requests:', error);
        this.loading = false;
        this.message = error?.error?.message || error?.message || 'Failed to load requests';
        this.messageType = 'error';
      }
    });
  }

  approveRequest(requestId: string) {
    if (this.processingRequestIds.has(requestId)) {
      return;
    }
    this.processingRequestIds.add(requestId);
    
    this.profileService.approveProfileChangeRequest(requestId).subscribe({
      next: () => {
        this.message = 'Request approved successfully!';
        this.messageType = 'success';
        this.profileChangeRequests = this.profileChangeRequests.filter(
          (request) => request.id !== requestId
        );
        this.processingRequestIds.delete(requestId);
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.message = error.error?.message || 'Failed to approve request';
        this.messageType = 'error';
        this.processingRequestIds.delete(requestId);
      }
    });
  }

  rejectRequest(requestId: string) {
    if (this.processingRequestIds.has(requestId)) {
      return;
    }
    this.processingRequestIds.add(requestId);
    
    this.profileService.rejectProfileChangeRequest(requestId).subscribe({
      next: () => {
        this.message = 'Request rejected successfully!';
        this.messageType = 'success';
        this.profileChangeRequests = this.profileChangeRequests.filter(
          (request) => request.id !== requestId
        );
        this.processingRequestIds.delete(requestId);
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.message = error.error?.message || 'Failed to reject request';
        this.messageType = 'error';
        this.processingRequestIds.delete(requestId);
      }
    });
  }
}
