package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RideStatusConstraintSync {

    private static final Logger logger = LoggerFactory.getLogger(RideStatusConstraintSync.class);
    private final JdbcTemplate jdbcTemplate;

    public RideStatusConstraintSync(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncRideStatusConstraint() {
        try {
            jdbcTemplate.execute("ALTER TABLE ride DROP CONSTRAINT IF EXISTS ride_status_check");
            jdbcTemplate.execute(
                "ALTER TABLE ride ADD CONSTRAINT ride_status_check CHECK (status IN ('REQUESTED','SCHEDULED','ASSIGNED','IN_PROGRESS','FINISHED','CANCELED_BY_DRIVER','CANCELED_BY_CLIENT','REJECTED'))"
            );
            logger.info("ride_status_check constraint synced with RideStatus enum values");
        } catch (Exception ex) {
            logger.warn("Could not sync ride_status_check constraint automatically: {}", ex.getMessage());
        }
    }
}
