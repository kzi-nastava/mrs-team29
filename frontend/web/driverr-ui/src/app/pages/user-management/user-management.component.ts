import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { UserBlockStatus } from '../../models/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: UserBlockStatus[] = [];
  loading = false;
  message = '';
  messageType = '';
  
  // Modal state
  showBlockModal = false;
  selectedUser: UserBlockStatus | null = null;
  blockNote = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.adminService.getAllUsersBlockStatus().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.showMessage('Failed to load users', 'error');
        this.loading = false;
      }
    });
  }

  openBlockModal(user: UserBlockStatus) {
    this.selectedUser = user;
    this.blockNote = user.blockNote || '';
    this.showBlockModal = true;
  }

  closeBlockModal() {
    this.showBlockModal = false;
    this.selectedUser = null;
    this.blockNote = '';
  }

  blockUser() {
    if (!this.selectedUser || !this.blockNote.trim()) {
      this.showMessage('Please provide a reason for blocking', 'error');
      return;
    }

    this.adminService.blockUser(this.selectedUser.userId, this.blockNote).subscribe({
      next: () => {
        this.showMessage('User blocked successfully', 'success');
        this.closeBlockModal();
        this.loadUsers();
      },
      error: (error) => {
        console.error('Error blocking user:', error);
        this.showMessage(error.error?.message || 'Failed to block user', 'error');
      }
    });
  }

  unblockUser(user: UserBlockStatus) {
    if (!confirm(`Are you sure you want to unblock ${user.firstName} ${user.lastName}?`)) {
      return;
    }

    this.adminService.unblockUser(user.userId).subscribe({
      next: () => {
        this.showMessage('User unblocked successfully', 'success');
        this.loadUsers();
      },
      error: (error) => {
        console.error('Error unblocking user:', error);
        this.showMessage(error.error?.message || 'Failed to unblock user', 'error');
      }
    });
  }

  showMessage(text: string, type: string) {
    this.message = text;
    this.messageType = type;
    setTimeout(() => this.message = '', 3000);
  }

  getUserTypeDisplay(userType: string): string {
    return userType.charAt(0) + userType.slice(1).toLowerCase();
  }
}
