export interface Ride {
  rideId: string;
  status: RideStatus;
  price: number;
  driverId?: string;
  driverName?: string;
  pickupAddressId: string;
  pickupAddress: string;
  destinationAddressId: string;
  destinationAddress: string;
  stopAddressIds?: string[];
  passengerIds: string[];
  createdAt?: string;
  startedAt?: string;
  finishedAt?: string;
  scheduledTime?: string;
}

export enum RideStatus {
  REQUESTED = 'REQUESTED',
  SCHEDULED = 'SCHEDULED',
  ASSIGNED = 'ASSIGNED',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  CANCELED_BY_DRIVER = 'CANCELED_BY_DRIVER',
  CANCELED_BY_CLIENT = 'CANCELED_BY_CLIENT',
  REJECTED = 'REJECTED'
}
