export interface DailyRideMetric {
  date: string;
  ridesCount: number;
  kilometers: number;
  amount: number;
}

export interface RideReportResponse {
  startDate: string;
  endDate: string;
  daily: DailyRideMetric[];

  totalRides: number;
  totalKilometers: number;
  totalAmount: number;

  averageRidesPerDay: number;
  averageKilometersPerDay: number;
  averageAmountPerDay: number;
}
