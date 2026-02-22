package service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import domain.entities.Address;
import domain.entities.Ride;
import domain.entities.User;
import domain.enums.RideStatus;
import domain.enums.UserType;
import dto.report.DailyRideMetricDTO;
import dto.report.RideReportResponseDTO;
import repository.RideRepository;
import repository.UserRepository;
import service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RideReportResponseDTO getUserRideReport(String userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserType scope = user.getUserType() == UserType.DRIVER ? UserType.DRIVER : UserType.CLIENT;
        return buildReport(scope, userId, startDate, endDate);
    }

    @Override
    public RideReportResponseDTO getAdminRideReport(
            UserType scope,
            String userId,
            String userEmail,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (scope != UserType.DRIVER && scope != UserType.CLIENT) {
            throw new RuntimeException("Scope must be DRIVER or CLIENT");
        }

        String resolvedUserId = userId;
        if (userEmail != null && !userEmail.isBlank()) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found for email: " + userEmail));
            resolvedUserId = user.getId();
        }

        return buildReport(scope, resolvedUserId, startDate, endDate);
    }

    private RideReportResponseDTO buildReport(UserType scope, String userId, LocalDate startDate, LocalDate endDate) {
        LocalDate resolvedEnd = endDate == null ? LocalDate.now() : endDate;
        LocalDate resolvedStart = startDate == null ? resolvedEnd.minusDays(6) : startDate;

        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        List<Ride> finishedRides = getRelevantFinishedRides(scope, userId);

        Map<LocalDate, MutableDayMetric> dayMap = new LinkedHashMap<>();
        LocalDate cursor = resolvedStart;
        while (!cursor.isAfter(resolvedEnd)) {
            dayMap.put(cursor, new MutableDayMetric());
            cursor = cursor.plusDays(1);
        }

        for (Ride ride : finishedRides) {
            LocalDateTime finishedAt = getRideFinishedAt(ride);
            if (finishedAt == null) {
                continue;
            }
            LocalDate day = finishedAt.toLocalDate();
            if (day.isBefore(resolvedStart) || day.isAfter(resolvedEnd)) {
                continue;
            }

            MutableDayMetric metric = dayMap.get(day);
            if (metric == null) {
                continue;
            }

            double distanceKm = calculateRideDistance(ride);
            double amount = ride.getPrice();

            if (scope == UserType.CLIENT && userId == null) {
                int passengerCount = ride.getPassengers() == null ? 0 : ride.getPassengers().size();
                if (passengerCount <= 0) {
                    continue;
                }
                metric.rides += passengerCount;
                metric.kilometers += distanceKm * passengerCount;
                metric.amount += amount * passengerCount;
            } else {
                metric.rides += 1;
                metric.kilometers += distanceKm;
                metric.amount += amount;
            }
        }

        List<DailyRideMetricDTO> daily = new ArrayList<>();
        int totalRides = 0;
        double totalKm = 0.0;
        double totalAmount = 0.0;

        for (Map.Entry<LocalDate, MutableDayMetric> entry : dayMap.entrySet()) {
            LocalDate day = entry.getKey();
            MutableDayMetric metric = entry.getValue();

            totalRides += metric.rides;
            totalKm += metric.kilometers;
            totalAmount += metric.amount;

            daily.add(new DailyRideMetricDTO(
                    day,
                    metric.rides,
                    round2(metric.kilometers),
                    round2(metric.amount)
            ));
        }

        long dayCount = ChronoUnit.DAYS.between(resolvedStart, resolvedEnd) + 1;
        if (dayCount <= 0) {
            dayCount = 1;
        }

        RideReportResponseDTO response = new RideReportResponseDTO();
        response.setStartDate(resolvedStart);
        response.setEndDate(resolvedEnd);
        response.setDaily(daily);

        response.setTotalRides(totalRides);
        response.setTotalKilometers(round2(totalKm));
        response.setTotalAmount(round2(totalAmount));

        response.setAverageRidesPerDay(round2((double) totalRides / dayCount));
        response.setAverageKilometersPerDay(round2(totalKm / dayCount));
        response.setAverageAmountPerDay(round2(totalAmount / dayCount));

        return response;
    }

    private List<Ride> getRelevantFinishedRides(UserType scope, String userId) {
        if (scope == UserType.DRIVER) {
            if (userId == null || userId.isBlank()) {
                return rideRepository.findByStatusOrderByTimestampsDesc(RideStatus.FINISHED);
            }
            return rideRepository.findByDriver_IdAndStatusOrderByTimestampsDesc(userId, RideStatus.FINISHED);
        }

        if (userId == null || userId.isBlank()) {
            return rideRepository.findByStatusOrderByTimestampsDesc(RideStatus.FINISHED);
        }
        return rideRepository.findUserRideHistory(userId, RideStatus.FINISHED);
    }

    private LocalDateTime getRideFinishedAt(Ride ride) {
        if (ride.getTimestamps() == null || ride.getTimestamps().isEmpty()) {
            return null;
        }
        return ride.getTimestamps().stream()
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private double calculateRideDistance(Ride ride) {
        List<Address> points = new ArrayList<>();
        if (ride.getPickupAddress() != null) {
            points.add(ride.getPickupAddress());
        }
        if (ride.getStops() != null && !ride.getStops().isEmpty()) {
            points.addAll(ride.getStops());
        }
        if (ride.getDestinationAddress() != null) {
            points.add(ride.getDestinationAddress());
        }

        if (points.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            total += haversine(points.get(i), points.get(i + 1));
        }
        return total;
    }

    private double haversine(Address from, Address to) {
        final double earthRadiusKm = 6371.0;

        double lat1 = Math.toRadians(from.getLatitude());
        double lon1 = Math.toRadians(from.getLongitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double lon2 = Math.toRadians(to.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class MutableDayMetric {
        int rides;
        double kilometers;
        double amount;
    }
}
