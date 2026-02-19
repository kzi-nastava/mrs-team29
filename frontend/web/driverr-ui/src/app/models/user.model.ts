export interface UserBlockStatus {
  userId: string;
  userName: string;
  email: string;
  firstName: string;
  lastName: string;
  blocked: boolean;
  blockNote: string | null;
  userType: string;
}

export interface BlockUserRequest {
  userId: string;
  blocked: boolean;
  blockNote: string;
}
