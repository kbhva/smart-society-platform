package com.smartsociety.platform.complaint;

import com.smartsociety.platform.audit.AuditLog;
import com.smartsociety.platform.audit.AuditLogRepository;
import com.smartsociety.platform.auth.*;
import com.smartsociety.platform.common.DomainException;
import com.smartsociety.platform.common.Enums.*;
import com.smartsociety.platform.media.ComplaintPhotoRepository;
import com.smartsociety.platform.notification.NotificationService;
import com.smartsociety.platform.security.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    final ComplaintRepository repo;
    final CategoryRepository cats;
    final HistoryRepository history;
    final UserRepository users;
    final NotificationService notifications;
    final AuditLogRepository audits;
    final ComplaintPhotoRepository photos;

    public ComplaintService(
            ComplaintRepository r,
            CategoryRepository c,
            HistoryRepository h,
            UserRepository u,
            NotificationService n,
            AuditLogRepository a,
            ComplaintPhotoRepository p
    ) {
        repo = r;
        cats = c;
        history = h;
        users = u;
        notifications = n;
        audits = a;
        photos = p;
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse create(
            ComplaintDtos.CreateRequest r,
            SecurityUser su
    ) {
        var c = new Complaint();

        c.setResident(su.user());

        c.setCategory(
                cats.findById(r.categoryId())
                        .orElseThrow(() ->
                                new DomainException(
                                        HttpStatus.NOT_FOUND,
                                        "Category not found"
                                )
                        )
        );

        c.setTitle(r.title().trim());
        c.setDescription(r.description().trim());
        c.setDueAt(
                Instant.now().plus(
                        slaHours(c),
                        ChronoUnit.HOURS
                )
        );

        repo.save(c);

        addHistory(
                c,
                null,
                ComplaintStatus.OPEN,
                su.user(),
                "Complaint created"
        );

        audit(
                su.user(),
                "COMPLAINT_CREATED",
                c.getId()
        );

        return map(c, false);
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse status(
            UUID id,
            ComplaintDtos.StatusRequest r,
            SecurityUser actor
    ) {
        var c = find(id);

        validateAccess(c, actor);

        var from = c.getCurrentStatus();

        if (!valid(from, r.status())) {
            throw new DomainException(
                    HttpStatus.CONFLICT,
                    "Invalid status transition"
            );
        }

        c.setCurrentStatus(r.status());

        if (r.status() == ComplaintStatus.RESOLVED) {
            c.setResolvedAt(Instant.now());
        }

        addHistory(
                c,
                from,
                r.status(),
                actor.user(),
                r.note()
        );

        notifications.statusChanged(
                c,
                from,
                r.status()
        );

        audit(
                actor.user(),
                "STATUS_CHANGED",
                c.getId()
        );

        return map(c, false);
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse priority(
            UUID id,
            ComplaintDtos.PriorityRequest r,
            SecurityUser actor
    ) {
        var c = find(id);

        validateAdmin(actor);

        if (c.getCurrentStatus() == ComplaintStatus.RESOLVED) {
            throw new DomainException(
                    HttpStatus.CONFLICT,
                    "Resolved complaints cannot be reprioritized"
            );
        }

        c.setPriority(r.priority());

        c.setDueAt(
                c.getCreatedAt().plus(
                        slaHours(c),
                        ChronoUnit.HOURS
                )
        );

        audit(
                actor.user(),
                "PRIORITY_CHANGED",
                c.getId()
        );

        return map(c, false);
    }

    @Transactional(readOnly = true)
    public Page<ComplaintDtos.ComplaintResponse> list(
            SecurityUser u,
            Pageable p
    ) {
        return (
                u.user().getRole() == Role.ADMIN
                        ? repo.findAll(p)
                        : repo.findByResidentId(u.id(), p)
        ).map(c -> map(c, false));
    }

    @Transactional(readOnly = true)
    public List<ComplaintDtos.HistoryResponse> history(
            UUID id,
            SecurityUser u
    ) {
        var c = find(id);

        validateAccess(c, u);

        return history
                .findByComplaintIdOrderByCreatedAtAsc(id)
                .stream()
                .map(h ->
                        new ComplaintDtos.HistoryResponse(
                                h.getFromStatus(),
                                h.getToStatus(),
                                h.getActor().getFullName(),
                                h.getNote(),
                                h.getCreatedAt()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintDtos.ComplaintResponse get(
            UUID id,
            SecurityUser u
    ) {
        var c = find(id);

        validateAccess(c, u);

        return map(c, true);
    }

    private Complaint find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new DomainException(
                                HttpStatus.NOT_FOUND,
                                "Complaint not found"
                        )
                );
    }

    private void validateAdmin(SecurityUser u) {
        if (u.user().getRole() != Role.ADMIN) {
            throw new DomainException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }

    private void validateAccess(
            Complaint c,
            SecurityUser u
    ) {
        if (
                u.user().getRole() != Role.ADMIN
                        && !c.getResident().getId().equals(u.id())
        ) {
            throw new DomainException(
                    HttpStatus.FORBIDDEN,
                    "Not allowed"
            );
        }
    }

    private boolean valid(
            ComplaintStatus a,
            ComplaintStatus b
    ) {
        return (
                a == ComplaintStatus.OPEN
                        && b == ComplaintStatus.IN_PROGRESS
        ) || (
                a == ComplaintStatus.IN_PROGRESS
                        && b == ComplaintStatus.RESOLVED
        );
    }

    private int slaHours(Complaint c) {
        int base = c.getCategory().getSlaHours();

        return switch (c.getPriority()) {
            case HIGH -> Math.max(1, base / 2);
            case LOW -> base * 2;
            default -> base;
        };
    }

    private void addHistory(
            Complaint c,
            ComplaintStatus from,
            ComplaintStatus to,
            User actor,
            String note
    ) {
        var h = new ComplaintHistory();

        h.setComplaint(c);
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setActor(actor);
        h.setNote(note);

        history.save(h);
    }

    private void audit(
            User actor,
            String action,
            UUID id
    ) {
        var a = new AuditLog();

        a.setActor(actor);
        a.setAction(action);
        a.setEntityType("COMPLAINT");
        a.setEntityId(id.toString());

        audits.save(a);
    }

    private ComplaintDtos.ComplaintResponse map(
            Complaint c,
            boolean includePhotos
    ) {
        boolean overdue =
                c.getCurrentStatus() != ComplaintStatus.RESOLVED
                        && c.getDueAt() != null
                        && Instant.now().isAfter(c.getDueAt());

        List<ComplaintDtos.PhotoResponse> ps;

        if (includePhotos) {
            ps = photos
                    .findByComplaintIdOrderByCreatedAtAsc(c.getId())
                    .stream()
                    .map(p ->
                            new ComplaintDtos.PhotoResponse(
                                    p.getId(),
                                    p.getOriginalFilename(),
                                    p.getMimeType(),
                                    p.getFileSize(),
                                    p.getCreatedAt(),
                                    "/api/v1/complaints/"
                                            + c.getId()
                                            + "/photos/"
                                            + p.getId()
                            )
                    )
                    .toList();
        } else {
            ps = List.of();
        }

        return new ComplaintDtos.ComplaintResponse(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getCategory().getName(),
                c.getPriority(),
                c.getCurrentStatus(),
                c.getCreatedAt(),
                c.getDueAt(),
                overdue,
                c.getResolvedAt(),
                ps
        );
    }
}