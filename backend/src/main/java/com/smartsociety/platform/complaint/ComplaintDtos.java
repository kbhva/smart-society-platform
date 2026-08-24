package com.smartsociety.platform.complaint;

import com.smartsociety.platform.common.Enums.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;

public final class ComplaintDtos {
    private ComplaintDtos(){}
    public record CreateRequest(@NotNull Long categoryId,@NotBlank @Size(max=150) String title,@NotBlank @Size(max=5000) String description){}
    public record StatusRequest(@NotNull ComplaintStatus status,@Size(max=1000) String note){}
    public record PriorityRequest(@NotNull Priority priority){}
    public record PhotoResponse(UUID id,String filename,String mimeType,long fileSize,Instant createdAt,String url){}
    public record ComplaintResponse(UUID id,String title,String description,String category,Priority priority,ComplaintStatus status,Instant createdAt,Instant dueAt,boolean overdue,Instant resolvedAt,List<PhotoResponse> photos){}
    public record HistoryResponse(ComplaintStatus fromStatus,ComplaintStatus toStatus,String actor,String note,Instant createdAt){}
}
