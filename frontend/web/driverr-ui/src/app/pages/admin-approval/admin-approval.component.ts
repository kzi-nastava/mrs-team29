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

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.loadPendingRequests();
  }

  loadPendingRequests() {
    this.loading = true;
    this.profileService.getAllPendingProfileChangeRequests().subscribe({
      next: (requests) => {
        this.profileChangeRequests = requests;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.message = 'Failed to load requests';
        this.messageType = 'error';
      }
    });
  }

  approveRequest(requestId: string) {
    this.profileService.approveProfileChangeRequest(requestId).subscribe({
      next: () => {
        this.message = 'Request approved successfully!';
        this.messageType = 'success';
        this.loadPendingRequests();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.message = error.error?.message || 'Failed to approve request';
        this.messageType = 'error';
      }
    });
  }

  rejectRequest(requestId: string) {
    this.profileService.rejectProfileChangeRequest(requestId).subscribe({
      next: () => {
        this.message = 'Request rejected successfully!';
        this.messageType = 'success';
        this.loadPendingRequests();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.message = error.error?.message || 'Failed to reject request';
        this.messageType = 'error';
      }
    });
  }
}
