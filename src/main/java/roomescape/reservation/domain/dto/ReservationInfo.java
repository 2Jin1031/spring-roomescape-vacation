package roomescape.reservation.domain.dto;

import java.time.LocalDate;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public record ReservationInfo(LocalDate date, ReservationTime time, Theme theme) {

    public static ReservationInfo of(Reservation reservation) {
        return new ReservationInfo(reservation.getDate(), reservation.getReservationTime(), reservation.getTheme());
    }
}
