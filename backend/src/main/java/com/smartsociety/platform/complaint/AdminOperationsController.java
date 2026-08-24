package com.smartsociety.platform.complaint;

import com.smartsociety.platform.common.Enums.*;
import com.smartsociety.platform.security.SecurityUser;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/complaints")
public class AdminOperationsController {
    final AdminComplaintQueryRepository repo;
    public AdminOperationsController(AdminComplaintQueryRepository r){repo=r;}

    @GetMapping
    public Page<ComplaintDtos.ComplaintResponse> search(@RequestParam(required=false) ComplaintStatus status,@RequestParam(required=false) Priority priority,@RequestParam(required=false) Long categoryId,@RequestParam(required=false) String from,@RequestParam(required=false) String to,@RequestParam(required=false) String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@AuthenticationPrincipal SecurityUser u){
        if(page<0||page>100) throw new IllegalArgumentException("page must be between 0 and 100");
        if(size<1||size>100) throw new IllegalArgumentException("size must be between 1 and 100");
        Instant f=from==null?null:LocalDate.parse(from).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant t=to==null?null:LocalDate.parse(to).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return repo.search(status,priority,categoryId,f,t,q==null||q.isBlank()?null:q.trim(),PageRequest.of(page,size))
            .map(c->new ComplaintDtos.ComplaintResponse(c.getId(),c.getTitle(),c.getDescription(),c.getCategory().getName(),c.getPriority(),c.getCurrentStatus(),c.getCreatedAt(),c.getDueAt(),c.getCurrentStatus()!=ComplaintStatus.RESOLVED&&c.getDueAt()!=null&&Instant.now().isAfter(c.getDueAt()),c.getResolvedAt(),List.of()));
    }
}
