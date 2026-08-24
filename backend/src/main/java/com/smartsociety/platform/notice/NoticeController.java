package com.smartsociety.platform.notice;

import com.smartsociety.platform.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {
    final NoticeService s; public NoticeController(NoticeService x){s=x;}
    @GetMapping public List<NoticeDtos.Response> list(){return s.published();}
    @PostMapping("/admin") @PreAuthorize("hasRole('ADMIN')") public NoticeDtos.Response create(@Valid @RequestBody NoticeDtos.Request r,@AuthenticationPrincipal SecurityUser u){return s.create(r,u);}
    @PutMapping("/admin/{id}") @PreAuthorize("hasRole('ADMIN')") public NoticeDtos.Response update(@PathVariable UUID id,@Valid @RequestBody NoticeDtos.Request r,@AuthenticationPrincipal SecurityUser u){return s.update(id,r,u);}
    @DeleteMapping("/admin/{id}") @PreAuthorize("hasRole('ADMIN')") public void archive(@PathVariable UUID id,@AuthenticationPrincipal SecurityUser u){s.archive(id);}
}
