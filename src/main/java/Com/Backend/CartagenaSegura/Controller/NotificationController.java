package Com.Backend.CartagenaSegura.Controller;

import Com.Backend.CartagenaSegura.Dto.SharedDto.ApiResponse;
import Com.Backend.CartagenaSegura.Model.Notification;
import Com.Backend.CartagenaSegura.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Centro de notificaciones del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Mis notificaciones", description = "Todas las notificaciones del usuario, ordenadas por fecha descendente.")
    public ResponseEntity<ApiResponse<List<Notification>>> getMine(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                notificationService.getByUser(userDetails.getUsername())));
    }

    @GetMapping("/unread")
    @Operation(summary = "Notificaciones no leÃƒÂ­das")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnread(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                notificationService.getUnreadByUser(userDetails.getUsername())));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Contador de no leÃƒÂ­das", description = "ÃƒÅ¡til para mostrar el badge rojo en la campana de notificaciones.")
    public ResponseEntity<ApiResponse<Long>> countUnread(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                notificationService.countUnread(userDetails.getUsername())));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar como leÃƒÂ­da")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @Parameter(description = "ID de la notificaciÃƒÂ³n") @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Marcada como leÃƒÂ­da", notificationService.markAsRead(id)));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Marcar todas como leÃƒÂ­das")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Todas marcadas como leÃƒÂ­das", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificaciÃƒÂ³n")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID de la notificaciÃƒÂ³n") @PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("NotificaciÃƒÂ³n eliminada", null));
    }
}
